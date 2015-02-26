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
import org.mule.module.mongo.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

public class InsertObjectTestCases extends MongoTestParent {


	@Before
	public void setUp() throws Exception {
			initializeTestRunMessage("insertObject");
			runFlowAndGetPayload("create-collection");
	}

	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testInsertObject() {
		try {
			String objectID = runFlowAndGetPayload("insert-object");
			
			assertTrue(objectID != null && !objectID.equals("") && !objectID.trim().equals(""));
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
	
	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection");
	}

}
