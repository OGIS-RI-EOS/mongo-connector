/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertNotNull;
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

public class FindOneObjectTestCases extends MongoTestParent {

	@Before
	public void setUp() throws Exception {
			// create the collection
			initializeTestRunMessage("findOneObject");
			runFlowAndGetPayload("create-collection");
			
			// create the object
			// dbObject is modified in the insert-object flow
			runFlowAndGetPayload("insert-object");
			

	}

	@Category({ RegressionTests.class })
	@Test
	public void testFindOneObject() {
		try {
			DBObject dbObject = (DBObject) getTestRunMessageValue("dbObjectRef");
			
			// Get the retrieved DBObject
			// No MongoException means that it found a match (we are matching using ID)
			DBObject payload = runFlowAndGetPayload("find-one-object");
			assertNotNull(payload);
			assertTrue(payload.equals(dbObject));
			
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}

	@After
	public void tearDown() throws Exception {
			// drop the collection
			runFlowAndGetPayload("drop-collection");


	}

}
