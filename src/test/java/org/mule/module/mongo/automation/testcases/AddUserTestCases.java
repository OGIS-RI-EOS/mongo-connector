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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.WriteResult;

public class AddUserTestCases extends MongoTestParent {
	
	@Category({RegressionTests.class})
	@Test
	public void testAddUser() {
		try {
			initializeTestRunMessage("addUser");
			
			WriteResult result = runFlowAndGetPayload("add-user");
			assertTrue(result.getLastError().ok());
			assertTrue(result.getError() == null);
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
		
	}
	
}
