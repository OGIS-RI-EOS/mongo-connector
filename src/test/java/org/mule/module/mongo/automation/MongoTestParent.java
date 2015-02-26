/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.mule.api.MuleEvent;
import org.mule.module.mongo.api.MongoCollection;
import org.mule.modules.tests.ConnectorTestCase;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoTestParent extends ConnectorTestCase {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// Set global timeout of tests to 10minutes
    @Rule
    public Timeout globalTimeout = new Timeout(600000);

	protected List<DBObject> getEmptyDBObjects(int num) {
		List<DBObject> list = new ArrayList<DBObject>();
		for (int i = 0; i < num; i++) {
			list.add(new BasicDBObject());
		}
		return list;
	}
	
	protected void setTestObjects(Map<String, Object> testObjects) {
	}

	protected void insertObjects(List<DBObject> objs) {
		try {
			for (DBObject obj : objs) {
				upsertOnTestRunMessage("dbObjectRef", obj);
				runFlowAndGetPayload("insert-object");
			}
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}

	
	// Returns all number of all files in database as per find-files operation
	protected int findFiles() {
		int size = 0;
		Iterable<DBObject> iterable = null;
		try {
//			MuleEvent event = getTestEvent(new BasicDBObject());
			iterable = runFlowAndGetPayload("find-files");

		
		for(DBObject dbObj : iterable) {
			if(dbObj.containsField("filename")) {
				size++;
			}
			
		}
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
		return size;
		
	}
	
	protected GridFSInputFile createFileFromPayload(DBObject dbObj, String filename) {
		GridFSInputFile res = null;
		try {
			File file = folder.newFile(filename);

			upsertOnTestRunMessage("filename", filename);
			upsertOnTestRunMessage("metadataRef", dbObj);
			upsertOnTestRunMessage("payloadContent", file);

			res = runFlowAndGetPayload("create-file-from-payload");
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
		return res;
	}
	
	protected GridFSInputFile createFileFromPayload(String filename) {
		return createFileFromPayload(new BasicDBObject(), filename);
	}
	
	protected GridFSInputFile createFileFromPayload(Object filename) {
		return createFileFromPayload(filename.toString());
	}
	
	protected void deleteFilesCreatedByCreateFileFromPayload() {
		try {
			upsertOnTestRunMessage("collection", "fs.chunks");
			runFlowAndGetPayload("drop-collection");
			
			upsertOnTestRunMessage("collection", "fs.files");
			runFlowAndGetPayload("drop-collection");
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
			
			
	}

	protected void dropIndex(String indexName) {
		upsertOnTestRunMessage("index", indexName);			
		try {
			runFlowAndGetPayload("drop-index");
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
	}
	
	protected MongoCollection getObjects(Map<String, Object> testObjects) throws Exception {
		return (MongoCollection) runFlowAndGetPayload("find-objects");
	}
	
}
