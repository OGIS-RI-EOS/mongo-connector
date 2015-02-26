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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UpdateObjectsTestCases extends MongoTestParent {


	@Before
	public void setUp() throws Exception {
			// Create the collection
			initializeTestRunMessage("updateObjects");
			runFlowAndGetPayload("create-collection");

			// Insert object
			runFlowAndGetPayload("insert-object");
			

	}
	
	@After
	public void tearDown() throws Exception {
			// Drop the collection
			runFlowAndGetPayload("drop-collection");


	}
	
	@Category({RegressionTests.class})
	@Test
	public void testUpdateObjects_OneObject() {
		try {
		
			// Grab the key-value pair
			String key = getTestRunMessageValue("key").toString();
			String value = getTestRunMessageValue("value").toString();
			
			// Create new DBObject based on key-value pair to replace existing DBObject
			DBObject newDBObject = new BasicDBObject(key, value);
			
			// Place it in testObjects so that the flow can access it
			upsertOnTestRunMessage("elementRef", newDBObject);
			
			// Update the object
			runFlowAndGetPayload("update-objects-single-object");
			
			// Attempt to find the object
			DBObject obj = runFlowAndGetPayload("find-one-object");
			
			// Assert that the object retrieved from MongoDB contains the key-value pairs
			// Not the most ideal way to test, but since update returns void in connector, 
			// we cannot retrieve the ID granted to newDBObject
			assertTrue(obj.containsField(key));
			assertTrue(obj.get(key).equals(value));
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }	

	}
	
}
