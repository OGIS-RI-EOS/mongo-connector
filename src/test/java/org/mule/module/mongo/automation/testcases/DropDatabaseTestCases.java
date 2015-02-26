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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

public class DropDatabaseTestCases extends MongoTestParent {

	@Before
	public void setUp() throws Exception {
			initializeTestRunMessage("dropDatabase");
			runFlowAndGetPayload("save-object-for-drop-restore");
	}
	
	@After
	public void tearDown() throws Exception {
			runFlowAndGetPayload("drop-collection-for-drop-restore");
	}	
	
	
	@Category({ RegressionTests.class })
	@Test
	public void testDropDatabase() {
		try {
			runFlowAndGetPayload("drop-database");
			assertFalse("After dropping the database, the collection " + getTestRunMessageValue("collection") + " should not exist", 
					(Boolean) runFlowAndGetPayload("exists-collection-for-drop-restore"));

		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
			
	}
	
}
