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

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.module.mongo.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.DBObject;

public class CreateIndexTestCases extends MongoTestParent {

	@Before
	public void setUp() throws Exception {
			initializeTestRunMessage("createIndex");
			runFlowAndGetPayload("create-collection");

	}
	
	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testCreateIndex() {
		try {

			String indexKey = getTestRunMessageValue("field").toString();
			IndexOrder indexOrder = (IndexOrder) getTestRunMessageValue("order");
			String indexName = indexKey + "_" + indexOrder.getValue();
	
			runFlowAndGetPayload("create-index");
		
			List<DBObject> payload = runFlowAndGetPayload("list-indices");
								
			assertTrue(MongoHelper.indexExistsInList(payload, indexName));

		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
			
	}
	
	@After
	public void tearDown() throws Exception {
			// Drop the created index
			String indexKey = getTestRunMessageValue("field").toString();
			IndexOrder indexOrder = (IndexOrder) getTestRunMessageValue("order");
			
			String indexName = indexKey + "_" + indexOrder.getValue();
			upsertOnTestRunMessage("index", indexName);
			
			runFlowAndGetPayload("drop-index");
			
			// Drop the collection
			runFlowAndGetPayload("drop-collection");

	}

}
