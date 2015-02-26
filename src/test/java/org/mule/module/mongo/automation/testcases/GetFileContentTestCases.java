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

import com.mongodb.DBObject;

public class GetFileContentTestCases extends MongoTestParent {
	

	@Before
	public void setUp() {
		initializeTestRunMessage("getFileContent");
		
		createFileFromPayload(getTestRunMessageValue("filename1"));
		createFileFromPayload(getTestRunMessageValue("filename2"));
	}

	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@Category({ RegressionTests.class })
	@Test
	public void testGetFileContent() {
		try {
			DBObject queryRef = (DBObject) getTestRunMessageValue("queryRef");
			queryRef.put("filename", getTestRunMessageValue("filename1"));
			
			Object response = runFlowAndGetPayload("get-file-content");
			
			assertNotNull(response);
			assertTrue(response instanceof InputStream);
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
		
	}

}
