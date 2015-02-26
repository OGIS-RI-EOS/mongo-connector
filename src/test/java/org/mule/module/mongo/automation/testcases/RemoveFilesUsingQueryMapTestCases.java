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

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

public class RemoveFilesUsingQueryMapTestCases extends MongoTestParent {


	@Before
	public void setUp() {
		initializeTestRunMessage("removeFilesUsingQueryMap");
		
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
	public void testRemoveFilesUsingQueryMap_emptyQuery() {
		try {
			runFlowAndGetPayload("remove-files-using-query-map-empty-query");
			assertEquals("There should be 0 files found after remove-files-using-query-map with an empty query", 0,
					findFiles());
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }


	}

	@Category({ RegressionTests.class })
	@Test
	public void testRemoveFilesUsingQueryMap_nonemptyQuery() {
		try {
			runFlowAndGetPayload("remove-files-using-query-map-non-empty-query");
			assertEquals("There should be 1 files found after removing files with a filename of " + getTestRunMessageValue("value"), 1,
					findFiles());
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }


	}

}
