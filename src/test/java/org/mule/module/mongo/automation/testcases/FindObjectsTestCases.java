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

public class FindObjectsTestCases extends MongoTestParent {

	private List<String> objectIDs = new ArrayList<String>();
	

	@Before
	public void setUp() throws Exception {
			// create collection
			initializeTestRunMessage("findObjects");
			runFlowAndGetPayload("create-collection");

			int numberOfObjects = (Integer) getTestRunMessageValue("numberOfObjects");
			
			for (int i = 0; i < numberOfObjects; i++) {
				BasicDBObject dbObject = new BasicDBObject();
				upsertOnTestRunMessage("dbObjectRef", dbObject);
				
				String payload = runFlowAndGetPayload("insert-object");
				objectIDs.add(payload);
			}
			

	}

	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testFindObjects() {
		try {
			MongoCollection payload = runFlowAndGetPayload("find-objects");
			
			assertTrue(objectIDs.size() == payload.size());
			for (DBObject obj : payload) { 
				String dbObjectID = obj.get("_id").toString();
				assertTrue(objectIDs.contains(dbObjectID));
			}
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
	
	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection");

	}
	
	
}
