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
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.DBObject;

public class FindFilesUsingQueryMapTestCases extends MongoTestParent {


	@Before
	public void setUp() {
		initializeTestRunMessage("findFilesUsingQueryMap");

		createFileFromPayload(getTestRunMessageValue("filename1"));

		// create another file with a different name
		createFileFromPayload(getTestRunMessageValue("filename2"));
	}

	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}


	@Category({ RegressionTests.class })
	@Test
	public void testFindFilesUsingQueryMap() {
		try {
			// queryAttribKey and queryAttribVal in testObjects are used in
			// findFilesUsingQueryMapFlow to query for a file with filename of
			// 'file2'
			// One such file should be found

			Iterable<DBObject> iterable = runFlowAndGetPayload("find-files-using-query-map");
			int filesFoundUsingQueryMap = MongoHelper.getIterableSize(iterable);

			assertEquals(
					"There should be 1 file with the name "
							+ getTestRunMessageValue("filename2"), 1,
					filesFoundUsingQueryMap);
			assertEquals("There should be 2 files in total", 2, findFiles());
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
			

	}

}
