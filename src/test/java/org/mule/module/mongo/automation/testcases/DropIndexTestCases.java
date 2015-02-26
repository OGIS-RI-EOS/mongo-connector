/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
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

public class DropIndexTestCases extends MongoTestParent {


	@Before
	public void setUp() throws Exception {
			// Create the collection
			initializeTestRunMessage("dropIndex");
			runFlowAndGetPayload("create-collection");
			
			// Create the index
			runFlowAndGetPayload("create-index");

	}
	

	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testDropIndexByName() {
		try {

			String indexKey = getTestRunMessageValue("field").toString();
			IndexOrder indexOrder = (IndexOrder) getTestRunMessageValue("order");
			
			String indexName = indexKey + "_" + indexOrder.getValue();
			
			upsertOnTestRunMessage("index", indexName);
			
			runFlowAndGetPayload("drop-index");
			
			List<DBObject> payload = runFlowAndGetPayload("list-indices");
						
			assertFalse(MongoHelper.indexExistsInList(payload, indexName));
			
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
	
	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection");

	}

}
