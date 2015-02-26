/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.DBObject;

public class FindOneFileUsingQueryMapTestCases extends MongoTestParent {


	@Before
	public void setUp() {
		initializeTestRunMessage("findOneFileUsingQueryMap");

		createFileFromPayload(getTestRunMessageValue("filename1"));
		createFileFromPayload(getTestRunMessageValue("filename1"));
		createFileFromPayload(getTestRunMessageValue("filename2"));
	}

	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}

	@Category({ RegressionTests.class })
	@Test
	public void testFindOneFileUsingQueryMap() {
		try {
			DBObject dbObj = runFlowAndGetPayload("find-one-file-using-query-map");
			
			assertEquals("The file found should have the name " + getTestRunMessageValue("filename1"), getTestRunMessageValue("filename1"), dbObj.get("filename"));
			assertEquals("There should be 3 files in total", 3, findFiles());

		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
		
	}

}
