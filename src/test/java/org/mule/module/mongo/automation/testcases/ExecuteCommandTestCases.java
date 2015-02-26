/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.CommandResult;

public class ExecuteCommandTestCases extends MongoTestParent {

	@Before
	public void setUp() throws Exception {
			// Get the collectionName and create a collection
			initializeTestRunMessage("executeCommand");
			runFlowAndGetPayload("create-collection");

	}
	
	@Category({ RegressionTests.class})
	@Test
	public void testExecuteCommand() {
		try {
			// Drop the collection using command
			CommandResult cmdResult = runFlowAndGetPayload("execute-command");
			assertTrue(cmdResult.ok());
			
			Boolean exists = runFlowAndGetPayload("exists-collection");
			assertFalse(exists);
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
		
}
