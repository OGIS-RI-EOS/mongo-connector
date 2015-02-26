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

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.IndexOrder;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.DBObject;

public class RestoreTestCases extends MongoTestParent {
	

	@Before
	public void setUp() throws Exception {
		initializeTestRunMessage("restore");
		String indexKey = getTestRunMessageValue("field").toString();
		IndexOrder indexOrder = (IndexOrder) getTestRunMessageValue("order");
		
		String indexName = MongoHelper.getIndexName(indexKey, indexOrder);
		
		runFlowAndGetPayload("createIndex_Dump");
		
		// drop index
		upsertOnTestRunMessage("index", indexName);

		runFlowAndGetPayload("drop-index-for-drop-restore");

	}
	
	@After
	public void tearDown() throws Exception {
			File dumpOutputDir = new File("./" + getTestRunMessageValue("outputDirectory"));
			FileUtils.deleteDirectory(dumpOutputDir);
			
			String indexKey = getTestRunMessageValue("field").toString();
			IndexOrder indexOrder = (IndexOrder) getTestRunMessageValue("order");
			
			String indexName = MongoHelper.getIndexName(indexKey, indexOrder);
			
			// drop index
			upsertOnTestRunMessage("index", indexName);			
			runFlowAndGetPayload("drop-index-for-drop-restore");
			// Need to drop the collection becuase creating the index creates the collection
			runFlowAndGetPayload("drop-collection-for-drop-restore");

	}
	

	@Category({RegressionTests.class})
	@Test
	public void testRestore() {
		try {
			runFlowAndGetPayload("restore");
			
			String indexKey = getTestRunMessageValue("field").toString();
			IndexOrder indexOrder = (IndexOrder) getTestRunMessageValue("order");
			
			String indexName = MongoHelper.getIndexName(indexKey, indexOrder);
			
			List<DBObject> payload = runFlowAndGetPayload("list-indices-for-drop-restore");
			
			assertTrue("After restoring the database, the index with index name = " + indexName + " should exist", MongoHelper.indexExistsInList(payload, indexName));
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
	}
	
}
