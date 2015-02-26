/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

public class SaveObjectFromMapTestCases extends MongoTestParent {


	@Before
	public void setUp() throws Exception {
			initializeTestRunMessage("saveObjectFromMap");
			runFlowAndGetPayload("create-collection");


	}
	
	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection");


	}
	
	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testSaveObjectFromMap() {
		try {
		
			String key = getTestRunMessageValue("key").toString();
			String value = getTestRunMessageValue("value").toString();
		
			// Save object to MongoDB
			runFlowAndGetPayload("save-object-from-map");
			
			// Check whether it was saved			
			DBObject object = (DBObject) runFlowAndGetPayload("find-one-object-using-query-map");
			assertTrue(object.containsField(key));
			assertTrue(object.get(key).equals(value));
			
			// Modify object and save to MongoDB
			upsertOnTestRunMessage("value", "differentValue");
			String differentValue = getTestRunMessageValue("value").toString();
			runFlowAndGetPayload("save-object-from-map");
			
			// Check that modifications were saved
			object = runFlowAndGetPayload("find-one-object-using-query-map");
			assertTrue(object.containsField(key));
			assertFalse(object.get(key).equals(value));
			assertTrue(object.get(key).equals(differentValue));
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
}
