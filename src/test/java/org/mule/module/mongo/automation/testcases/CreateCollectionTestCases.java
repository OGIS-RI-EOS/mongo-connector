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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.module.mongo.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

public class CreateCollectionTestCases extends MongoTestParent {

	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection");
	

	}
	
	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testCreateCollection() {	
		try {
			initializeTestRunMessage("createCollection");
			runFlowAndGetPayload("create-collection");			
			
			assertTrue((Boolean) runFlowAndGetPayload("exists-collection"));
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
		
}
