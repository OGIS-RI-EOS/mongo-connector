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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.module.mongo.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

public class DropCollectionTestCases extends MongoTestParent {

	@Before
	public void setUp() throws Exception {
			initializeTestRunMessage("dropCollection");
			runFlowAndGetPayload("create-collection");

	}
	
	@Category({SmokeTests.class, RegressionTests.class})
	@Test
	public void testDropCollection() {
		
		try {
			runFlowAndGetPayload("drop-collection");
			assertFalse((Boolean) runFlowAndGetPayload("exists-collection"));
			
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
	
	
}
