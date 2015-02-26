/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.MongoCollection;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class RemoveObjectsUsingQueryMapTestCases extends MongoTestParent{


	@Before
	public void setUp() throws Exception {
			// Create the collection
			initializeTestRunMessage("removeObjectsUsingQueryMap");
			runFlowAndGetPayload("create-collection");


			// Load variables from testObjects
			String key = getTestRunMessageValue("key").toString();
			String value = getTestRunMessageValue("value").toString();
			int numberOfObjects = (Integer) getTestRunMessageValue("numberOfObjects");
			int extraObjects = (Integer) getTestRunMessageValue("extraObjects");
			
			// Create list of objects, some with key-value pair, some without
			List<DBObject> objects = new ArrayList<DBObject>();
			for (int i = 0; i < numberOfObjects; i++) {
				DBObject dbObj = new BasicDBObject(key, value);
				objects.add(dbObj);
			}
			
			objects.addAll(getEmptyDBObjects(extraObjects));
			
			// Insert objects into collection
			insertObjects(objects);
			

	}
	
	@After
	public void tearDown() throws Exception {
			// Drop the collection
			runFlowAndGetPayload("drop-collection");


	}
	
	@Category({RegressionTests.class})
	@Test
	public void testRemoveUsingQueryMap_WithQueryMap() {
		try {
			int extraObjects = (Integer) getTestRunMessageValue("extraObjects");
			String key = getTestRunMessageValue("key").toString();
			
			// Remove all records matching key-value pair
			runFlowAndGetPayload("remove-objects-using-query-map-with-query-map");
			
			// Get all objects
			// Only objects which should be returned are those without the key value pairs
			MongoCollection objects = runFlowAndGetPayload("find-objects");
			assertTrue(objects.size() == extraObjects);
			
			// Check that each returned object does not contain the defined key-value pair
			for (DBObject obj : objects) {
				assertTrue(!obj.containsField(key));
			}
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }


	}

	@Category({ RegressionTests.class})
	@Test
	public void testRemoveUsingQueryMap_WithoutQueryMap() {
		try {
			
			// Remove all records
			runFlowAndGetPayload("remove-objects-using-query-map-without-query-map");
			
			// Get all objects
			MongoCollection objects = (MongoCollection) runFlowAndGetPayload("find-objects");
			assertTrue(objects.isEmpty());
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
}
