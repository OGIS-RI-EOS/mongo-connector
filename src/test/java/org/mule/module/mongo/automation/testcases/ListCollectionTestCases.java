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

import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

public class ListCollectionTestCases extends MongoTestParent {

	private List<String> collectionNames;
	

	@Before
	public void setUp() throws Exception {
			initializeTestRunMessage("collection","collectionName");
			collectionNames = getBeanFromContext("listCollections");

			for (String collectionName : collectionNames) {
				upsertOnTestRunMessage("collection", collectionName);
				runFlowAndGetPayload("create-collection");
			}

	}


	@Category({ RegressionTests.class})
	@Test
	public void testListCollections() {
		try {
			Collection<String> payload = runFlowAndGetPayload("list-collections");
			
			for (String collectionName : collectionNames) {
				assertTrue(payload.contains(collectionName));
			}
			
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
			

	}
	
	@After
	public void tearDown() throws Exception {
			for (String collectionName : collectionNames) {
				upsertOnTestRunMessage("collection", collectionName);
				runFlowAndGetPayload("drop-collection");
			}

	}

}
