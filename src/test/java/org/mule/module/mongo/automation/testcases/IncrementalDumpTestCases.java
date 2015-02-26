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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

public class IncrementalDumpTestCases extends MongoTestParent {
	

	@Before
	public void setUp() {
		initializeTestRunMessage("dump");
	}

	@After
	public void tearDown() throws Exception {
			FileUtils.deleteDirectory(new File("./" + getTestRunMessageValue("outputDirectory")));

	}

	@Category({ RegressionTests.class })
	@Test
	public void testIncrementalDump() {
		try {
			runFlowAndGetPayload("dump");
			File dumpOutputDir = new File("./" + getTestRunMessageValue("outputDirectory"));
			assertTrue("dump directory should exist after test runs", dumpOutputDir.exists());
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
		
	}

}
