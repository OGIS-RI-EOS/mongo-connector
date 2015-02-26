/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.module.mongo.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.DBObject;

public class SaveObjectTestCases extends MongoTestParent {


	@Before
	public void setUp() throws Exception {
			initializeTestRunMessage("saveObject");
			runFlowAndGetPayload("create-collection");
				

	}
	
	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testSaveObject() {
		try {
			runFlowAndGetPayload("save-object");
			
			DBObject element = (DBObject) getTestRunMessageValue("elementRef");
			
			// Check that object was inserted
//			MongoCollection dbObjects = getObjects(testObjects);
//			assertTrue(dbObjects.contains(element));			
			
			// Get key and value from payload (defined in bean)
			String key = getTestRunMessageValue("key").toString();
			String value = getTestRunMessageValue("value").toString();
			
			// Modify object and save
			element.put(key, value);
			runFlowAndGetPayload("save-object");
			
			// Check that object was changed in MongoDB
//			dbObjects = getObjects(testObjects);
//			assertTrue(dbObjects.contains(element));
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
		
	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection");


	}
	
}
