/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleMessage;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

public class GetFileContentUsingQueryMapTestCases extends MongoTestParent {
	

	@Before
	public void setUp() {
		initializeTestRunMessage("getFileContentUsingQueryMap");
		
		createFileFromPayload(getTestRunMessageValue("filename1"));
		createFileFromPayload(getTestRunMessageValue("filename2"));
	}

	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@Category({ RegressionTests.class })
	@Test
	public void testGetFileContentUsingQueryMap() {
		try {
			MuleMessage response = runFlowAndGetMessage("get-file-content-using-query-map");
			
			assertNotNull(response);
			assertNotNull(response.getPayload());
			assertTrue(response.getPayload() instanceof InputStream);
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
		
	}

}
