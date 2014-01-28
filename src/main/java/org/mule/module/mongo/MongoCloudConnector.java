/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo;

import static org.mule.module.mongo.api.DBObjects.adapt;
import static org.mule.module.mongo.api.DBObjects.from;
import static org.mule.module.mongo.api.DBObjects.fromCommand;
import static org.mule.module.mongo.api.DBObjects.fromFunction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.deftlabs.cursor.mongo.TailableCursor;
import com.deftlabs.cursor.mongo.TailableCursorImpl;
import com.deftlabs.cursor.mongo.TailableCursorOptions;
import com.mongodb.*;
import org.apache.commons.lang.Validate;
import org.bson.types.BasicBSONList;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Category;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.MetaDataSwitch;
import org.mule.api.annotations.Mime;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.SourceThreadingModel;
import org.mule.api.annotations.Transformer;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.Payload;
import org.mule.api.callback.SourceCallback;
import org.mule.api.callback.StopSourceCallback;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.MongoClient;
import org.mule.module.mongo.api.MongoClientAdaptor;
import org.mule.module.mongo.api.MongoClientImpl;
import org.mule.module.mongo.api.MongoCollection;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.module.mongo.tools.BackupConstants;
import org.mule.module.mongo.tools.IncrementalMongoDump;
import org.mule.module.mongo.tools.MongoDump;
import org.mule.module.mongo.tools.MongoRestore;
import org.mule.module.mongo.tools.TailableCursorDocListenerImpl;
import org.mule.transformer.types.MimeTypes;
import org.mule.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.util.JSON;

/**
 * MongoDB is an open source, high-performance, schema-free, document-oriented database that manages
 * collections of BSON documents.
 * 
 * @author MuleSoft, inc.
 */
@Connector(name = "mongo", schemaVersion = "2.0", friendlyName = "Mongo DB", minMuleVersion = "3.4", metaData = MetaDataSwitch.OFF)
public class MongoCloudConnector
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoCloudConnector.class);

    private static final String CAPPED_DEFAULT_VALUE = "false";
    private static final String WRITE_CONCERN_DEFAULT_VALUE = "DATABASE_DEFAULT";
    private static final String BACKUP_THREADS = "5";
    private static final String DEFAULT_OUTPUT_DIRECTORY = "dump";

    private final Mongo.Holder mongoClientHolder = Mongo.Holder.singleton();

    /**
     * The host of the Mongo server, it can also be a list of comma separated hosts for replicas
     */
    @Configurable
    @Optional
    @Default("localhost")
    private String host;

    /**
     * The port of the Mongo server
     */
    @Configurable
    @Optional
    @Default("27017")
    private int port;

    /**
     * The number of connections allowed per host (the pool size, per host)
     */
    @Configurable
    @Optional
    public Integer connectionsPerHost;

    /**
     * Multiplier for connectionsPerHost for # of threads that can block
     */
    @Configurable
    @Optional
    public Integer threadsAllowedToBlockForConnectionMultiplier;

    /**
     * The max wait time for a blocking thread for a connection from the pool in ms.
     */
    @Configurable
    @Optional
    public Integer maxWaitTime;

    /**
     * The connection timeout in milliseconds; this is for establishing the socket connections
     * (open). 0 is default and infinite.
     */
    @Configurable
    @Optional
    @Default("30000")
    private Integer connectTimeout;

    /**
     * The socket timeout. 0 is default and infinite.
     */
    @Configurable
    @Optional
    private Integer socketTimeout;

    /**
     * This controls whether the system retries automatically on connection errors.
     */
    @Configurable
    @Optional
    private Boolean autoConnectRetry;

    private String database;

    private Mongo mongo;

    private MongoClient client;

    /**
     * Adds a new user for this db
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:add-user}
     * 
     * @param newUsername Name of the user
     * @param newPassword Password that will be used for authentication
     * @return Result of the operation
     */
    @Processor
    public WriteResult addUser(final String newUsername, final String newPassword)
    {
        return client.addUser(newUsername, newPassword);
    }

    /**
     * Drop the current database
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:drop-database}
     */
    @Processor
    public void dropDatabase()
    {
        client.dropDatabase();
    }

    /**
     * Lists names of collections available at this database
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:list-collections}
     * 
     * @return the list of names of collections available at this database
     */
    @Processor
    public Collection<String> listCollections()
    {
        return client.listCollections();
    }

    /**
     * Answers if a collection exists given its name
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:exists-collection}
     * 
     * @param collection the name of the collection
     * @return if the collection exists
     */
    @Processor
    public boolean existsCollection(final String collection)
    {
        return client.existsCollection(collection);
    }

    /**
     * Deletes a collection and all the objects it contains. If the collection does not exist, does
     * nothing.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:drop-collection}
     * 
     * @param collection the name of the collection to drop
     */
    @Processor
    public void dropCollection(final String collection)
    {
        client.dropCollection(collection);
    }

    /**
     * Creates a new collection. If the collection already exists, a MongoException will be thrown.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:create-collection}
     * 
     * @param collection the name of the collection to create
     * @param capped if the collection will be capped
     * @param maxObjects the maximum number of documents the new collection is able to contain
     * @param size the maximum size of the new collection
     */
    @Processor
    public void createCollection(final String collection,
                                 @Optional @Default(CAPPED_DEFAULT_VALUE) final boolean capped,
                                 @Optional final Integer maxObjects,
                                 @Optional final Integer size)
    {
        client.createCollection(collection, capped, maxObjects, size);
    }

    /**
     * Inserts an object in a collection, setting its id if necessary.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:insert-object}
     * 
     * @param collection the name of the collection where to insert the given object
     * @param dbObject a {@link DBObject} instance.
     * @param writeConcern the optional write concern of insertion
     * @return the id that was just insterted
     */
    @Processor
    public String insertObject(final String collection,
                               @Optional @Default("#[payload]") final DBObject dbObject,
                               @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        return client.insertObject(collection, dbObject, writeConcern);
    }

    /**
     * Inserts an object in a collection, setting its id if necessary.
     * <p/>
     * A shallow conversion into DBObject is performed - that is, no conversion is performed to its
     * values.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:insert-object-from-map}
     * 
     * @param collection the name of the collection where to insert the given object
     * @param elementAttributes alternative way of specifying the element as a literal Map inside a
     *            Mule Flow
     * @param writeConcern the optional write concern of insertion
     * @return the id that was just insterted
     */
    @Processor
    public String insertObjectFromMap(final String collection,
                                      @Placement(group = "Element Attributes") final Map<String, Object> elementAttributes,
                                      @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        return client.insertObject(collection, (DBObject) adapt(elementAttributes), writeConcern);
    }

    /**
     * Updates objects that matches the given query. If parameter multi is set to false, only the
     * first document matching it will be updated. Otherwise, all the documents matching it will be
     * updated.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:update-objects}
     * 
     * @param collection the name of the collection to update
     * @param query the {@link DBObject} query object used to detect the element to update. If the object Id is an instance of ObjectId you need to specify the value pair as map with the following structure:
     * { "_id" : "ObjectId(OBJECT_ID_VALUE)"}
     * @param element the {@link DBObject} mandatory object that will replace that one which matches
     *            the query.
     * @param upsert if the database should create the element if it does not exist
     * @param multi if all or just the first object matching the query will be updated
     * @param writeConcern the write concern used to update
     */
    @Processor
    public void updateObjects(final String collection,
                              final DBObject query,
                              @Optional @Default("#[payload]") final DBObject element,
                              @Optional @Default(CAPPED_DEFAULT_VALUE) final boolean upsert,
                              @Optional @Default("true") final boolean multi,
                              @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        client.updateObjects(collection, query, element, upsert, multi, writeConcern);
    }

    /**
     * Updates objects that matches the given query. If parameter multi is set to false, only the
     * first document matching it will be updated. Otherwise, all the documents matching it will be
     * updated.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:update-objects-using-query-map}
     * 
     * @param collection the name of the collection to update
     * @param queryAttributes the query object used to detect the element to update. If the object Id is an instance of ObjectId you need to specify the value pair as map with the following structure:
     * { "_id" : "ObjectId(OBJECT_ID_VALUE)"}
     * @param element the {@link DBObject} mandatory object that will replace that one which matches
     *            the query.
     * @param upsert if the database should create the element if it does not exist
     * @param multi if all or just the first object matching the query will be updated
     * @param writeConcern the write concern used to update
     */
    @Processor
    public void updateObjectsUsingQueryMap(final String collection,
                                           final Map<String, Object> queryAttributes,
                                           final DBObject element,
                                           @Optional @Default(CAPPED_DEFAULT_VALUE) final boolean upsert,
                                           @Optional @Default("true") final boolean multi,
                                           @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        client.updateObjects(collection, (DBObject) adapt(queryAttributes), element, upsert, multi,
            writeConcern);
    }

    /**
     * Updates objects that matches the given query. If parameter multi is set to false, only the
     * first document matching it will be updated. Otherwise, all the documents matching it will be
     * updated.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:update-objects-using-map}
     * 
     * @param collection the name of the collection to update
     * @param queryAttributes the query object used to detect the element to update.
     * @param elementAttributes the mandatory object that will replace that one which matches the
     *            query.
     * @param upsert if the database should create the element if it does not exist
     * @param multi if all or just the first object matching the query will be updated
     * @param writeConcern the write concern used to update
     */
    @Processor
    public void updateObjectsUsingMap(final String collection,
                                      @Placement(group = "Query Attributes") final Map<String, Object> queryAttributes,
                                      @Placement(group = "Element Attributes") final Map<String, Object> elementAttributes,
                                      @Optional @Default(CAPPED_DEFAULT_VALUE) final boolean upsert,
                                      @Optional @Default("true") final boolean multi,
                                      @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        client.updateObjects(collection, (DBObject) adapt(queryAttributes),
            (DBObject) adapt(elementAttributes), upsert, multi, writeConcern);
    }

    /**
     * Update objects using a mongo function
     * <p/>
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:update-objects-by-function}
     * 
     * @param collection the name of the collection to update
     * @param function the function used to execute the update
     * @param query the {@link DBObject} query object used to detect the element to update.
     * @param element the {@link DBObject} mandatory object that will replace that one which matches
     *            the query.
     * @param upsert if the database should create the element if it does not exist
     * @param multi if all or just the first object matching the query will be updated
     * @param writeConcern the write concern used to update
     */
    @Processor
    public void updateObjectsByFunction(final String collection,
                                        final String function,
                                        final DBObject query,
                                        final DBObject element,
                                        @Optional @Default(CAPPED_DEFAULT_VALUE) final boolean upsert,
                                        @Optional @Default(value = "true") final boolean multi,
                                        @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        final DBObject functionDbObject = fromFunction(function, element);

        client.updateObjects(collection, query, functionDbObject, upsert, multi, writeConcern);
    }

    /**
     * Update objects using a mongo function
     * <p/>
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample
     * mongo:update-objects-by-function-using-map}
     * 
     * @param collection the name of the collection to update
     * @param function the function used to execute the update
     * @param queryAttributes the query object used to detect the element to update.
     * @param elementAttributes the mandatory object that will replace that one which matches the
     *            query.
     * @param upsert if the database should create the element if it does not exist
     * @param multi if all or just the first object matching the query will be updated
     * @param writeConcern the write concern used to update
     */
    @Processor
    public void updateObjectsByFunctionUsingMap(final String collection,
                                                final String function,
                                                final Map<String, Object> queryAttributes,
                                                final Map<String, Object> elementAttributes,
                                                @Optional @Default(CAPPED_DEFAULT_VALUE) final boolean upsert,
                                                @Optional @Default(value = "true") final boolean multi,
                                                @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        final DBObject functionDbObject = fromFunction(function, (DBObject) adapt(elementAttributes));

        client.updateObjects(collection, (DBObject) adapt(queryAttributes), functionDbObject, upsert, multi,
            writeConcern);
    }

    /**
     * Inserts or updates an object based on its object _id.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:save-object}
     * 
     * @param collection the collection where to insert the object
     * @param element the mandatory {@link DBObject} object to insert.
     * @param writeConcern the write concern used to persist the object
     */
    @Processor
    public void saveObject(final String collection,
                           @Optional @Default("#[payload]") final DBObject element,
                           @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        client.saveObject(collection, from(element), writeConcern);
    }

    /**
     * Inserts or updates an object based on its object _id.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:save-object-from-map}
     * 
     * @param collection the collection where to insert the object
     * @param elementAttributes the mandatory object to insert.
     * @param writeConcern the write concern used to persist the object
     */
    @Processor
    public void saveObjectFromMap(final String collection,
                                  @Placement(group = "Element Attributes") final Map<String, Object> elementAttributes,
                                  @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        client.saveObject(collection, (DBObject) adapt(elementAttributes), writeConcern);
    }

    /**
     * Removes all the objects that match the a given optional query. If query is not specified, all
     * objects are removed. However, please notice that this is normally less performant that
     * dropping the collection and creating it and its indices again
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-objects}
     * 
     * @param collection the collection whose elements will be removed
     * @param query the optional {@link DBObject} query object. Objects that match it will be
     *            removed.
     * @param writeConcern the write concern used to remove the object
     */
    @Processor
    public void removeObjects(final String collection,
                              @Optional @Default("#[payload]") final DBObject query,
                              @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        client.removeObjects(collection, query, writeConcern);
    }

    /**
     * Removes all the objects that match the a given optional query. If query is not specified, all
     * objects are removed. However, please notice that this is normally less performant that
     * dropping the collection and creating it and its indices again
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-objects-using-query-map}
     * 
     * @param collection the collection whose elements will be removed
     * @param queryAttributes the query object. Objects that match it will be removed.
     * @param writeConcern the write concern used to remove the object
     */
    @Processor
    public void removeObjectsUsingQueryMap(final String collection,
                                    @Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes,
                                    @Optional @Default(WRITE_CONCERN_DEFAULT_VALUE) final WriteConcern writeConcern)
    {
        client.removeObjects(collection, (DBObject) adapt(queryAttributes), writeConcern);
    }

    /**
     * Transforms a collection into a collection of aggregated groups, by applying a supplied
     * element-mapping function to each element, that transforms each one into a key-value pair,
     * grouping the resulting pairs by key, and finally reducing values in each group applying a
     * suppling 'reduce' function.
     * <p/>
     * Each supplied function is coded in JavaScript.
     * <p/>
     * Note that the correct way of writing those functions may not be obvious; please consult
     * MongoDB documentation for writing them.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:map-reduce-objects}
     * 
     * @param collection the name of the collection to map and reduce
     * @param mapFunction a JavaScript encoded mapping function
     * @param reduceFunction a JavaScript encoded reducing function
     * @param outputCollection the name of the output collection to write the results, replacing
     *            previous collection if existed, mandatory when results may be larger than 16MB. If
     *            outputCollection is unspecified, the computation is performed in-memory and not
     *            persisted.
     * @return an iterable that retrieves the resulting collection of {@link DBObject}
     */
    @Processor
    public Iterable<DBObject> mapReduceObjects(final String collection,
                                               final String mapFunction,
                                               final String reduceFunction,
                                               @Optional final String outputCollection)
    {
        return client.mapReduceObjects(collection, mapFunction, reduceFunction, outputCollection);
    }

    /**
     * Counts the number of objects that match the given query. If no query is passed, returns the
     * number of elements in the collection
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:count-objects}
     * 
     * @param collection the target collection
     * @param query the optional {@link DBObject} query for counting objects. Only objects matching
     *            it will be counted. If unspecified, all objects are counted.
     * @return the amount of objects that matches the query
     */
    @Processor
    public long countObjects(final String collection, @Optional @Default("#[payload]") final DBObject query)
    {
        return client.countObjects(collection, query);
    }

    /**
     * Counts the number of objects that match the given query. If no query is passed, returns the
     * number of elements in the collection
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:count-objects-using-query-map}
     * 
     * @param collection the target collection
     * @param queryAttributes the optional query for counting objects. Only objects matching it will
     *            be counted. If unspecified, all objects are counted.
     * @return the amount of objects that matches the query
     */
    @Processor
    public long countObjectsUsingQueryMap(final String collection,
                                          @Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes)
    {
        return client.countObjects(collection, (DBObject) adapt(queryAttributes));
    }

    /**
     * Finds all objects that match a given query. If no query is specified, all objects of the
     * collection are retrieved. If no fields object is specified, all fields are retrieved.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-objects}
     * 
     * @param collection the target collection
     * @param query the optional {@link DBObject} query object. If unspecified, all documents are
     *            returned.
     * @param fields alternative way of passing fields as a literal List
     * @param idExclusion the projection document the exclusion of the _id field
     * @param numToSkip number of objects skip (offset)
     * @param limit limit of objects to return
     * @return an iterable of {@link DBObject}
     */
    @Processor
    public Iterable<DBObject> findObjects(final String collection,
                                          @Optional @Default("") final DBObject query,
                                          @Placement(group = "Fields") @Optional final List<String> fields,
                                          @Optional @Default("false") Boolean idExclusion,
                                          @Optional final Integer numToSkip,
                                          @Optional final Integer limit)
    {
        return client.findObjects(collection, query, fields, idExclusion, numToSkip, limit);
    }

    /**
     * Finds all objects that match a given query. If no query is specified, all objects of the
     * collection are retrieved. If no fields object is specified, all fields are retrieved.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-objects-using-query-map}
     * 
     * @param collection the target collection
     * @param queryAttributes the optional query object. If unspecified, all documents are returned.
     * @param fields alternative way of passing fields as a literal List
     * @param idExclusion the projection document the exclusion of the _id field
     * @param numToSkip number of objects skip (offset)
     * @param limit limit of objects to return
     * @return an iterable of {@link DBObject}
     */
    @Processor
    public Iterable<DBObject> findObjectsUsingQueryMap(final String collection,
                                                       @Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes,
                                                       @Placement(group = "Fields") @Optional final List<String> fields,
                                                       @Optional @Default("false") Boolean idExclusion,
                                                       @Optional final Integer numToSkip,
                                                       @Optional final Integer limit)
    {
        return client.findObjects(collection, (DBObject) adapt(queryAttributes), fields, idExclusion, numToSkip, limit);
    }

    /**
     * Finds the first object that matches a given query. Throws a {@link MongoException} if no one
     * matches the given query
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-one-object}
     * 
     * @param collection the target collection
     * @param query the mandatory {@link DBObject} query object that the returned object matches.
     * @param fields alternative way of passing fields as a literal List
     * @param failOnNotFound Flag to specify if an exception will be thrown when no object is found. For backward compatibility the default value is true.
     * @return a {@link DBObject} that matches the query. If nothing matches and the failOnNotFound is set to false, null will be returned
     */
    @Processor
    public DBObject findOneObject(final String collection,
                                  @Optional @Default("#[payload]") final DBObject query,
                                  @Placement(group = "Fields") @Optional final List<String> fields,
                                  @Optional @Default("true") Boolean failOnNotFound)
    {
        return client.findOneObject(collection, query, fields, failOnNotFound);

    }

    /**
     * Finds the first object that matches a given query. Throws a {@link MongoException} if no one
     * matches the given query
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-one-object-using-query-map}
     * 
     * @param collection the target collection
     * @param queryAttributes the mandatory query object that the returned object matches.
     * @param fields alternative way of passing fields as a literal List
     * @param failOnNotFound Flag to specify if an exception will be thrown when no object is found. For backward compatibility the default value is true.
     * @return a {@link DBObject} that matches the query. If nothing matches and the failOnNotFound is set to false, null will be returned
     */
    @Processor
    public DBObject findOneObjectUsingQueryMap(final String collection,
                                               @Placement(group = "Query Attributes") final Map<String, Object> queryAttributes,
                                               @Placement(group = "Fields") @Optional final List<String> fields,
                                               @Optional @Default("true") Boolean failOnNotFound)
    {
        return client.findOneObject(collection, (DBObject) adapt(queryAttributes), fields, failOnNotFound);

    }

    /**
     * Creates a new index
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:create-index}
     * 
     * @param collection the name of the collection where the index will be created
     * @param field the name of the field which will be indexed
     * @param order the indexing order
     */
    @Processor
    public void createIndex(final String collection,
                            final String field,
                            @Optional @Default("ASC") final IndexOrder order)
    {
        client.createIndex(collection, field, order);
    }

    /**
     * Drops an existing index
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:drop-index}
     * 
     * @param collection the name of the collection where the index is
     * @param index the name of the index to drop
     */
    @Processor
    public void dropIndex(final String collection, final String index)
    {
        client.dropIndex(collection, index);
    }

    /**
     * List existent indices in a collection
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:list-indices}
     * 
     * @param collection the name of the collection
     * @return a collection of {@link DBObject} with indices information
     */
    @Processor
    public Collection<DBObject> listIndices(final String collection)
    {
        return client.listIndices(collection);
    }

    /**
     * Creates a new GridFSFile in the database, saving the given content, filename, contentType,
     * and extraData, and answers it.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:create-file-from-payload}
     * 
     * @param payload the mandatory content of the new gridfs file. It may be a java.io.File, a
     *            byte[] or an InputStream.
     * @param filename the mandatory name of new file.
     * @param contentType the optional content type of the new file
     * @param metadata the optional {@link DBObject} metadata of the new content type
     * @return the new GridFSFile {@link DBObject}
     * @throws IOException IOException
     */
    @Processor
    public DBObject createFileFromPayload(@Payload final Object payload,
                                          final String filename,
                                          @Optional final String contentType,
                                          @Optional final DBObject metadata) throws IOException
    {
        final InputStream stream = toStream(payload);
        try
        {
            return client.createFile(stream, filename, contentType, metadata);
        }
        finally
        {
            stream.close();
        }
    }

    private InputStream toStream(final Object content) throws FileNotFoundException
    {
        if (content instanceof InputStream)
        {
            return (InputStream) content;
        }
        if (content instanceof byte[])
        {
            return new ByteArrayInputStream((byte[]) content);
        }
        if (content instanceof File)
        {
            return new FileInputStream((File) content);
        }
        throw new IllegalArgumentException("Content " + content + " is not supported");
    }

    /**
     * Lists all the files that match the given query
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-files}
     * 
     * @param query a {@link DBObject} query the optional query
     * @return a {@link DBObject} files iterable
     */
    @Processor
    public Iterable<DBObject> findFiles(@Optional @Default("#[payload]") final DBObject query)
    {
        return client.findFiles(from(query));
    }

    /**
     * Lists all the files that match the given query
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-files-using-query-map}
     * 
     * @param queryAttributes the optional query attributes
     * @return a {@link DBObject} files iterable
     */
    @Processor
    public Iterable<DBObject> findFilesUsingQueryMap(@Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes)
    {
        return client.findFiles((DBObject) adapt(queryAttributes));
    }

    /**
     * Answers the first file that matches the given query. If no object matches it, a
     * MongoException is thrown.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-one-file}
     * 
     * @param query the {@link DBObject} mandatory query
     * @return a {@link DBObject}
     */
    @Processor
    public DBObject findOneFile(final DBObject query)
    {
        return client.findOneFile(from(query));
    }

    /**
     * Answers the first file that matches the given query. If no object matches it, a
     * MongoException is thrown.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:find-one-file-using-query-map}
     * 
     * @param queryAttributes the mandatory query
     * @return a {@link DBObject}
     */
    @Processor
    public DBObject findOneFileUsingQueryMap(@Placement(group = "Query Attributes") final Map<String, Object> queryAttributes)
    {
        return client.findOneFile((DBObject) adapt(queryAttributes));
    }

    /**
     * Answers an inputstream to the contents of the first file that matches the given query. If no
     * object matches it, a MongoException is thrown.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:get-file-content}
     * 
     * @param query the {@link DBObject} mandatory query
     * @return an InputStream to the file contents
     */
    @Processor
    public InputStream getFileContent(@Optional @Default("#[payload]") final DBObject query)
    {
        return client.getFileContent(from(query));
    }

    /**
     * Answers an inputstream to the contents of the first file that matches the given
     * queryAttributes. If no object matches it, a MongoException is thrown.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:get-file-content-using-query-map}
     * 
     * @param queryAttributes the mandatory query attributes
     * @return an InputStream to the file contents
     */
    @Processor
    public InputStream getFileContentUsingQueryMap(@Placement(group = "Query Attributes") final Map<String, Object> queryAttributes)
    {
        return client.getFileContent((DBObject) adapt(queryAttributes));
    }

    /**
     * Lists all the files that match the given query, sorting them by filename. If no query is
     * specified, all files are listed.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:list-files}
     * 
     * @param query the {@link DBObject} optional query
     * @return an iterable of {@link DBObject}
     */
    @Processor
    public Iterable<DBObject> listFiles(@Optional @Default("#[payload]") final DBObject query)
    {
        return client.listFiles(from(query));
    }

    /**
     * Lists all the files that match the given query, sorting them by filename. If no query is
     * specified, all files are listed.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:list-files-using-query-map}
     * 
     * @param queryAttributes the optional query
     * @return an iterable of {@link DBObject}
     */
    @Processor
    public Iterable<DBObject> listFilesUsingQueryMap(@Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes)
    {
        return client.listFiles((DBObject) adapt(queryAttributes));
    }

    /**
     * Removes all the files that match the given query. If no query is specified, all files are
     * removed
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-files}
     * 
     * @param query the {@link DBObject} optional query
     */
    @Processor
    public void removeFiles(@Optional @Default("#[payload]") final DBObject query)
    {
        client.removeFiles(from(query));
    }

    /**
     * Removes all the files that match the given query. If no query is specified, all files are
     * removed
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-files-using-query-map}
     * 
     * @param queryAttributes the optional query
     */
    @Processor
    public void removeFilesUsingQueryMap(@Placement(group = "Query Attributes") @Optional final Map<String, Object> queryAttributes)
    {
        client.removeFiles((DBObject) adapt(queryAttributes));
    }

    /**
     * Executes a command on the database
     * <p/>
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:remove-files}
     * 
     * @param commandName The command to execute on the database
     * @param commandValue The value for the command
     * @return The result of the command
     */
    @Processor
    public DBObject executeCommand(final String commandName, @Optional final String commandValue)
    {
        final DBObject dbObject = fromCommand(commandName, commandValue);

        return client.executeComamnd(dbObject);
    }

    /**
     * Executes a dump of the database to the specified output directory. If no output directory is
     * provided then the default /dump directory is used.
     * <p/>
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:dump}
     * 
     * @param outputDirectory output directory path, if no output directory is provided the default
     *            /dump directory is assumed
     * @param outputName output file name, if it's not specified the database name is used
     * @param zip whether to zip the created dump file or not
     * @param oplog point in time backup (requires an oplog)
     * @param threads amount of threads to execute the dump
     * @throws IOException if an error occurs during the dump
     */
    @Processor
    public void dump(@Optional @Default(DEFAULT_OUTPUT_DIRECTORY) final String outputDirectory,
                     @Optional final String outputName,
                     @Optional @Default("false") final boolean zip,
                     @Optional @Default("false") final boolean oplog,
                     @Optional @Default(BACKUP_THREADS) final int threads) throws IOException
    {
        final MongoDump mongoDump = new MongoDump(client);
        mongoDump.setZip(zip);
        if (oplog)
        {
            mongoDump.setOplog(oplog);
            mongoDump.addDB(mongo.getDB(BackupConstants.ADMIN_DB));
            mongoDump.addDB(mongo.getDB(BackupConstants.LOCAL_DB));
        }
        mongoDump.dump(outputDirectory, database, outputName != null ? outputName : database, threads);
    }

    /**
     * Executes an incremental dump of the database
     * <p/>
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:incremental-dump}
     * 
     * @param outputDirectory output directory path, if no output directory is provided the default
     *            /dump directory is assumed
     * @param incrementalTimestampFile file that keeps track of the last timestamp processed, if no
     *            file is provided one is created on the output directory
     * @throws IOException if an error occurs during the incremental dump
     */
    @Processor
    public void incrementalDump(@Optional @Default(DEFAULT_OUTPUT_DIRECTORY) final String outputDirectory,
                                @Optional final String incrementalTimestampFile) throws IOException
    {
        final IncrementalMongoDump incrementalMongoDump = new IncrementalMongoDump();
        incrementalMongoDump.addDB(mongo.getDB(BackupConstants.ADMIN_DB));
        incrementalMongoDump.addDB(mongo.getDB(BackupConstants.LOCAL_DB));
        incrementalMongoDump.setIncrementalTimestampFile(incrementalTimestampFile);
        incrementalMongoDump.dump(outputDirectory, database);
    }

    /**
     * Takes the output from the dump and restores it. Indexes will be created on a restore. It only
     * does inserts with the data to restore, if existing data is there, it will not be replaced.
     * <p/>
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:restore}
     * 
     * @param inputPath input path to the dump files, it can be a directory, a zip file or just a
     *            bson file
     * @param drop whether to drop existing collections before restore
     * @param oplogReplay replay oplog for point-in-time restore
     * @throws IOException if an error occurs during restore of the database
     */
    @Processor
    public void restore(@Optional @Default(DEFAULT_OUTPUT_DIRECTORY) final String inputPath,
                        @Optional @Default("false") final boolean drop,
                        @Optional @Default("false") final boolean oplogReplay) throws IOException
    {
        final MongoRestore mongoRestore = new MongoRestore(client, database);
        mongoRestore.setDrop(drop);
        mongoRestore.setOplogReplay(oplogReplay);
        mongoRestore.restore(inputPath);
    }

    /**
     * Begins a consistent request, which allows you to be sure that each subsequent request to MongoDB happens in sequence.
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:start-consistent-request}
     *
     * @see <a href="http://docs.mongodb.org/ecosystem/drivers/java-concurrency/">MongoDB: Java Driver Concurrency</a>
     */
    @Processor
    public void startConsistentRequest() {
        client.requestStart();
    }

    /**
     * Ends a consistent request.
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:end-consistent-request}
     *
     * @see org.mule.module.mongo.MongoCloudConnector#startConsistentRequest()
     * @see <a href="http://docs.mongodb.org/ecosystem/drivers/java-concurrency/">MongoDB: Java Driver Concurrency</a>
     */
    @Processor
    public void endConsistentRequest() {
        client.requestDone();
    }
    
    /**
     * TailableCursor function
     * 
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:next-tailable-cursor}
     * 
     * @param collection the target collection
     * @param query the {@link DBObject} optional query
     * @param noDocSleepTime the time if it cat not be find next doc 
     * @param callback The callback to be called when a message is received
     * @return {@link org.mule.api.callback.StopSourceCallback}
     */
    @Source(primaryNodeOnly = true, threadingModel = SourceThreadingModel.NONE)
    @Category(name = "Streaming API", description = "Tailable cursors are conceptually equivalent to the tail Unix command with the -f option (i.e. with “follow” mode.) After clients insert new additional documents into a capped collection, the tailable cursor will continue to retrieve documents.")
    public StopSourceCallback nextTailableCursor(String collection, 
                                                 @Optional @Default("") final String query,
                                                 @Optional @Default("10000") Long noDocSleepTime,
                                                 final SourceCallback callback) {
        final TailableCursorOptions options
	    	= new TailableCursorOptions("mongodb://" + host + ":" + port , database, collection);
	    options.setInitialQuery((DBObject) JSON.parse(query));
	    options.setNoDocSleepTime(noDocSleepTime);
	    options.setDocListener(new TailableCursorDocListenerImpl(callback));

	    final TailableCursor tailableCursor = new TailableCursorImpl(options);
    	
	    tailableCursor.start();
	    
        return new StopSourceCallback() {
            @Override
            public void stop() throws Exception {
                tailableCursor.stop();
            }
        };
    }
    
    /**
     * Convert JSON to DBObject.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:jsonToDbobject}
     * 
     * @param input the input for this transformer
     * @return the converted {@link DBObject}
     */
    @Transformer(sourceTypes = {String.class})
    public static DBObject jsonToDbobject(final String input)
    {
        return (DBObject) JSON.parse(input);
    }

    /**
     * Convert DBObject to Json.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:dbobjectToJson}
     * 
     * @param input the input for this transformer
     * @return the converted string representation
     */
    @Mime(MimeTypes.JSON)
    @Transformer(sourceTypes = {DBObject.class})
    public static String dbobjectToJson(final DBObject input)
    {
        return JSON.serialize(input);
    }

    /**
     * Convert a BasicBSONList into Json.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:bsonListToJson}
     * 
     * @param input the input for this transformer
     * @return the converted string representation
     */
    @Mime(MimeTypes.JSON)
    @Transformer(sourceTypes = {BasicBSONList.class})
    public static String bsonListToJson(final BasicBSONList input)
    {
        return JSON.serialize(input);
    }

    /**
     * Convert a BasicBSONList into Json.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:mongoCollectionToJson}
     * 
     * @param input the input for this transformer
     * @return the converted string representation
     */
    @Mime(MimeTypes.JSON)
    @Transformer(sourceTypes = {MongoCollection.class})
    public static String mongoCollectionToJson(final MongoCollection input)
    {
        return JSON.serialize(input);
    }

    /**
     * Convert a DBObject into Map.
     * <p/>
     * {@sample.xml ../../../doc/mongo-connector.xml.sample mongo:dbObjectToMap}
     * 
     * @param input the input for this transformer
     * @return the converted Map representation
     */
    @SuppressWarnings("rawtypes")
    @Transformer(sourceTypes = {DBObject.class})
    public static Map dbObjectToMap(final DBObject input)
    {
        return input.toMap();
    }

    /**
     * Method invoked when a {@link MongoSession} needs to be created.
     * 
     * @param username the username to use for authentication. NOTE: Please use a dummy user if you
     *            have disabled Mongo authentication
     * @param password the password to use for authentication. NOTE: Please use a dummy password if
     *            you have disabled Mongo authentication
     * @param database Name of the database
     * @return the newly created {@link MongoSession}
     * @throws org.mule.api.ConnectionException
     */
    @Connect
    public void connect(@ConnectionKey final String username,
                        @Password final String password,
                        @ConnectionKey @Optional @Default("test") final String database) throws ConnectionException
    {
        try
        {
            // TODO: use MongoClient instead of Mongo once the Holder supports it (in driver version 2.12.0)
            mongo = mongoClientHolder.connect(new MongoURI(getMongoClientURI(username, password, database)));

            this.client = new MongoClientImpl(getDatabase(mongo, username, password, database));
        }
        catch (final MongoException me)
        {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, null, me.getMessage());
        }
        catch (final UnknownHostException e)
        {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, null, e.getMessage());
        }
    }

    private MongoClientURI getMongoClientURI(final String username, final String password, final String database) {
        List<String> hostsWithPort = new LinkedList<String>();
        for (String hostname : host.split(",\\s?")) {
            hostsWithPort.add(hostname + ":" + port);
        }
        return new MongoClientURI(MongoURI.MONGODB_PREFIX +
//                        username + ":" + password + "@" +
                        StringUtils.join(hostsWithPort, ",") +
                        "/" + database
                , getMongoOptions(database));
    }

    private MongoClientOptions.Builder getMongoOptions(String database) {
        final MongoClientOptions.Builder options = MongoClientOptions.builder();

        if (connectionsPerHost != null)
        {
            options.connectionsPerHost(connectionsPerHost);
        }
        if (threadsAllowedToBlockForConnectionMultiplier != null)
        {
            options.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier);
        }
        if (maxWaitTime != null)
        {
            options.maxWaitTime(maxWaitTime);
        }
        if (connectTimeout != null)
        {
            options.connectTimeout(connectTimeout);
        }
        if (socketTimeout != null)
        {
            options.socketTimeout(socketTimeout);
        }
        if (autoConnectRetry != null)
        {
            options.autoConnectRetry(autoConnectRetry);
        }
        if (database != null)
        {
            this.database = database;
        }
        return options;
    }

    /**
     * Method invoked when the {@link MongoSession} is to be destroyed.
     * 
     * @throws IOException in case something goes wrong when disconnecting.
     */
    @Disconnect
    public void disconnect() throws IOException
    {
        if (client != null)
        {
            try
            {
                client.close();
            }
            catch (final Exception e)
            {
                LOGGER.warn("Failed to properly close client: " + client, e);
            }
            finally
            {
                client = null;
            }
        }

        if (mongo != null)
        {
            try
            {
                mongo.close();
            }
            catch (final Exception e)
            {
                LOGGER.warn("Failed to properly close mongo: " + mongo, e);
            }
            finally
            {
                mongo = null;
            }
        }
    }

    @ValidateConnection
    public boolean isConnected()
    {
        return this.client != null;
    }

    @ConnectionIdentifier
    public String connectionId()
    {
        return mongo == null ? "n/a" : mongo.toString();
    }

    private DB getDatabase(final Mongo mongo,
                           final String username,
                           final String password,
                           final String database) throws ConnectionException
    {
        final DB db = mongo.getDB(database);
        if (password != null)
        {
            Validate.notNull(username, "Username must not be null if password is set");
            if (!db.isAuthenticated())
            {
                if (!db.authenticate(username, password.toCharArray()))
                {
                    throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, null,
                        "Couldn't connect with the given credentials");
                }
            }
        }
        return db;
    }

    protected MongoClient adaptClient(final MongoClient client)
    {
        return MongoClientAdaptor.adapt(client);
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(final String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(final int port)
    {
        this.port = port;
    }

    public String getDatabase()
    {
        return database;
    }

    public void setDatabase(final String database)
    {
        this.database = database;
    }

    public Integer getConnectionsPerHost()
    {
        return connectionsPerHost;
    }

    public void setConnectionsPerHost(final Integer connectionsPerHost)
    {
        this.connectionsPerHost = connectionsPerHost;
    }

    public Integer getThreadsAllowedToBlockForConnectionMultiplier()
    {
        return threadsAllowedToBlockForConnectionMultiplier;
    }

    public void setThreadsAllowedToBlockForConnectionMultiplier(final Integer threadsAllowedToBlockForConnectionMultiplier)
    {
        this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
    }

    public Integer getMaxWaitTime()
    {
        return maxWaitTime;
    }

    public void setMaxWaitTime(final Integer maxWaitTime)
    {
        this.maxWaitTime = maxWaitTime;
    }

    public Integer getConnectTimeout()
    {
        return connectTimeout;
    }

    public void setConnectTimeout(final Integer connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSocketTimeout()
    {
        return socketTimeout;
    }

    public void setSocketTimeout(final Integer socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }

    public Boolean getAutoConnectRetry()
    {
        return autoConnectRetry;
    }

    public void setAutoConnectRetry(final Boolean autoConnectRetry)
    {
        this.autoConnectRetry = autoConnectRetry;
    }

}
