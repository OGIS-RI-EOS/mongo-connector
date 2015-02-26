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
import org.mule.module.mongo.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FindObjectsUsingQueryMapTestCases extends MongoTestParent {
			

	@Before
	public void setUp() throws Exception {
			// create collection
			initializeTestRunMessage("findObjectsUsingQueryMap");
			runFlowAndGetPayload("create-collection");
			
			// Create a number of objects
			int extraObjects = (Integer) getTestRunMessageValue("extraObjects");
			int numberOfObjects = (Integer) getTestRunMessageValue("numObjects");
			String queryKey = getTestRunMessageValue("queryKey").toString();
			String queryValue = getTestRunMessageValue("queryValue").toString();
			
			List<DBObject> objects = new ArrayList<DBObject>();
			for (int i = 0; i < numberOfObjects; i++) {
				BasicDBObject obj = new BasicDBObject(queryKey, queryValue);
				objects.add(obj);
			}
			
			// Add extra objects which do not have the key-value pair defined in testObjects
			// These should not be retrieved
			objects.addAll(getEmptyDBObjects(extraObjects));
			
			insertObjects(objects);			
			

	}

	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testFindObjectsUsingQueryMap_WithQuery() {
		try {
			int numberOfObjects = (Integer) getTestRunMessageValue("numObjects");
			String queryKey = getTestRunMessageValue("queryKey").toString();
			String queryValue = getTestRunMessageValue("queryValue").toString();
			
			MongoCollection collection = runFlowAndGetPayload("find-objects-using-query-map-with-query");
			
			assertTrue(numberOfObjects == collection.size());
			for (DBObject obj : collection) {
				assertTrue(obj.containsField(queryKey));
				assertTrue(obj.get(queryKey).equals(queryValue));
			}
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
	
	@Category({RegressionTests.class})
	@Test
	public void testFindObjectsUsingQueryMap_WithoutQuery() {
		try {
			int extraObjects = (Integer) getTestRunMessageValue("extraObjects");
			int numberOfObjects = (Integer) getTestRunMessageValue("numObjects");
			
			MongoCollection collection = runFlowAndGetPayload("find-objects-using-query-map-without-query");
			
			// Assert that everything was retrieved (empty objects + key-value pair objects)
			assertTrue(numberOfObjects + extraObjects == collection.size());
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}

	@Category({RegressionTests.class})
	@Test
	public void testFindObjectsUsingQueryMap_WithLimit() {
		try {
		
			int limit = (Integer) getTestRunMessageValue("limit");
			
			MongoCollection collection = runFlowAndGetPayload("find-objects-using-query-map-with-limit");
			
			// Assert that only "limit" objects were retrieved
			assertTrue(limit == collection.size());
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
			
	}
	
	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection");


	}
	
}
