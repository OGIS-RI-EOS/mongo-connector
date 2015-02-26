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

import com.mongodb.DBObject;

public class InsertObjectFromMapTestCases extends MongoTestParent {


	@Before
	public void setUp() throws Exception {
			initializeTestRunMessage("insertObjectFromMap");
			runFlowAndGetPayload("create-collection");


	}

	@Category({ RegressionTests.class})
	@Test
	public void testInsertObjectFromMap() {
		try {

			String key = getTestRunMessageValue("key").toString();
			String value = getTestRunMessageValue("value").toString();
			
			String objectID = runFlowAndGetPayload("insert-object-from-map");
			
			assertTrue(objectID != null && !objectID.equals("") && !objectID.trim().equals(""));
			
			DBObject object = runFlowAndGetPayload("find-one-object-using-query-map");
			assertTrue(object.containsField("_id"));
			assertTrue(object.containsField(key));
			
			assertTrue(object.get("_id").equals(objectID));
			assertTrue(object.get(key).equals(value));

		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
	}
	
	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection");


	}
}
