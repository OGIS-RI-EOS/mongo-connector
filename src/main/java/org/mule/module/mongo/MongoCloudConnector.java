/**
 * Mule MongoDB Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

/**
 * This file was automatically generated by the Mule Cloud Connector Development Kit
 */
package org.mule.module.mongo;

import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.mongo.api.MongoClient;
import org.mule.module.mongo.api.MongoClientImpl;
import org.mule.module.mongo.api.WriteConcern;
import org.mule.tools.cloudconnect.annotations.Connector;
import org.mule.tools.cloudconnect.annotations.Operation;
import org.mule.tools.cloudconnect.annotations.Parameter;
import org.mule.tools.cloudconnect.annotations.Property;

import com.mongodb.DBObject;
import com.mongodb.Mongo;

import java.util.List;

/**
 * A Mongo Connector Facade
 * @author flbulgarelli
 */
@Connector(namespacePrefix = "mongo")
public class MongoCloudConnector implements Initialisable
{
    @Property(name = "client-ref", optional = true)
    private MongoClient client;
    
    /**
     * Lists names of collections available at this database
     * 
     * {@code <list-collections/>}
     * @return the list of names of collections available at this database
     */
    @Operation
    public List<String> listCollections()
    {
        return client.listCollections();
    }

    /**
     * Answers if a collection exists given its name
     * 
     * {@code <exists-collection name="aColllection"/>}
     * @param collection the name of the collection
     * @return if the collection exists 
     */
    @Operation
    public boolean existsCollection(@Parameter String collection)
    {
        return client.existsCollection(collection);
    }

    /**
     * Deletes a collection and all the objects it contains. 
     * Example:
     * {@code <drop-collection name="aCollection"/>}
     * @param collection the name of the collection to drop
     */
    @Operation
    public void dropCollection(@Parameter String collection)
    {
        client.dropCollection(collection);
    }

    /**
     * Example: {@code <create-collection name="aCollection" capped="true"/>}
     * 
     * @param collection the name of the collection to create
     * @param capped if the collection will be capped TODO document its meaning
     * @param maxObject the maximum number of documents the new collection is able to
     *            contain
     * @param size the maximum size of the new collection TODO maximum?
     */
    @Operation
    public void createCollection(@Parameter String collection,
                                 @Parameter(optional = true, defaultValue = "false") boolean capped,
                                 @Parameter(optional = true)/* TODO optional */Integer maxObjects,
                                 @Parameter(optional = true)/* TODO optional */Integer size)
    {
        client.createCollection(collection, capped, maxObjects, size);
    }
    
    /**
     * Inserts an object in a collection, setting its id if necessary.
     * Example:
     * {@code <insert-object collection="Employees" object="#[header:aBsonEmployee]" writeConcern="SAFE"/>}
     * @param collection the name of the collection where to insert the given object
     * @param dbObject the object to insert
     * @param writeConcern the optional write concern of insertion
     */
    @Operation
    public void insertObject(@Parameter String collection,
                             @Parameter(name = "object") DBObject dbObject,
                             @Parameter(optional = true, defaultValue = "NORMAL") WriteConcern writeConcern)
    {
        client.insertObject(collection, dbObject, writeConcern);
    }

    /**
     * Updates the first object that matches the given query
     * Example:
     * {@code <update-object collection="#[map-payload:aCollectionName]" 
     *         query="#[variable:aBsonQuery]" object="#[variable:aBsonObject]" upsert="true"/>} 
     * @param collection the name of the collection to update
     * @param query the query object used to detect the element to update
     * @param dbObject the object that will replace that one which matches the query
     * @param upsert TODO
     */
    @Operation
    public void updateObject(@Parameter String collection,
                             @Parameter DBObject query,
                             @Parameter(name = "object") DBObject dbObject,
                             @Parameter(optional = true, defaultValue = "false") boolean upsert,
                             @Parameter(optional = true, defaultValue = "NORMAL") WriteConcern writeConcern)
    {
        client.updateObject(collection, query, dbObject, upsert, writeConcern);
    }

    /**
     * Inserts or updates an object based on its object _id.
     * Example: 
     * {@code <save-object 
     *          collection="#[map-payload:aCollectionName]"
     *          object="#[header:aBsonObject]"/>} 
     * @param collection
     * @param dbObject
     */
    @Operation
    public void saveObject(@Parameter String collection,
                           @Parameter(name = "object") DBObject dbObject,
                           @Parameter(optional = true, defaultValue = "NORMAL") WriteConcern writeConcern)
    {
        client.saveObject(collection, dbObject, writeConcern);
    }

    /**
     * Removes all the objects that match the a given optional query. 
     * If query is not specified, all objects are removed. However, please notice that this is normally
     * less performant that dropping the collection and creating it and its indices again
     * 
     * Example:
     * {@code <remove-objects collection="#[map-payload:aCollectionName]" query="#[map-payload:aBsonQuery]"/>}
     * @param collection the collection whose elements will be removed 
     * @param query the query object. Objects that match it will be removed
     */
    @Operation
    public void removeObjects(@Parameter String collection, @Parameter(optional = true) DBObject query)
    {
        client.removeObject(collection, query);
    }
    
    /**
     * Maps and folds objects in a collection by applying a mapping function and then a folding function 
     * Example:
     * 
     * {@code  <map-reduce-objects 
     *      collection="myCollection"
     *      mapFunction="#[header:aJSMapFunction]"
     *      reduceFunction="#[header:aJSFoldFunction]"/>} 
     * @param collection the name of the collection to map and reduce
     * @param mapFunction a JavaScript encoded mapping function
     * @param reduceFunction a JavaScript encoded folding function 
     */
    @Operation
    public DBObject mapReduceObjects(@Parameter String collection,
                                     @Parameter String mapFunction,
                                     @Parameter String reduceFunction)
    {
        return client.mapReduceObjects(collection, mapFunction, reduceFunction);
    }
    
    /**
     * Counts the number of objects that match the given query. If no query
     * is passed, returns the number of elements in the collection
     * 
     * {@code <count-objects 
     *      collection="#[variable:aCollectionName]"
     *      query="#[variable:aBsonQuery]"/>}
     */
    @Operation
    public long countObjects(@Parameter String collection, @Parameter(optional = true) DBObject query)
    {
        return client.countObjects(collection, query);
    }

    /**
     * Finds all objects that match a given query. If no query is specified, all objects of the 
     * collection are retrieved
     * 
     * {@code <find-objects query="#[map-payload:aBsonQuery]" fields="#[header:aBsonFieldsSet]"/>}
     * @param collection
     * @param query
     * @param fields
     */
    @Operation
    public Iterable<DBObject> findObjects(@Parameter String collection,
                                          @Parameter(optional = true) DBObject query,
                                          @Parameter DBObject fields)
    {
        return client.findObjects(collection, query, fields);
    }

    /**
     * Finds the first object that matches a given query. TODO if not exists?
     * 
     * {@code <find-one-object 
     *      query="#[variable:aBsonQuery]" 
     *      fields="#[map-payload:aBsonFieldsSet]"/>}   
     * @param collection
     * @param query
     * @param fields
     */
    @Operation
    public Iterable<DBObject> findOneObject(@Parameter String collection,
                                            @Parameter DBObject query,
                                            @Parameter DBObject fields)
    {
        return client.findOneObject(collection, query, fields);
    }
    
    /**
     * Creates a new index
     * 
     * {@code <create-index collection="myCollection" keys="#[header:aBsonFieldsSet]"/>}
     * @param the name of the collection where the index will be created
     * @param keys
     */
    @Operation
    public void createIndex(@Parameter String collection, @Parameter DBObject keys)
    {
        client.createIndex(collection, keys);
    }
    
    /**
     * Drops an existing index
     * Example:
     * {@code <drop-index collection="myCollection" name="#[map-payload:anIndexName]"/>}
     * @param the name of the collection where the index is
     * @param name the name of the index to drop
     */
    @Operation
    public void dropIndex(@Parameter String collection, @Parameter String name)
    {
        client.dropIndex(collection, name);
    }

    public void initialise() throws InitialisationException
    {
        if (client == null)
        {
            //TODO get from a weak hashmasp
            client = new MongoClientImpl();
        }
    }
    
}
