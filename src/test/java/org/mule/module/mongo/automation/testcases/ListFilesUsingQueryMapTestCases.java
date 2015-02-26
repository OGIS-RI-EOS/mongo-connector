/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleMessage;
import org.mule.module.mongo.api.automation.MongoHelper;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.DBObject;

public class ListFilesUsingQueryMapTestCases extends MongoTestParent {


	@Before
	public void setUp() {
		initializeTestRunMessage("listFilesUsingQueryMap");

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
	public void testListFilesUsingQueryMap_emptyQuery() {
		MuleMessage response = null;
		try {
			response = runFlowAndGetMessage("list-files-using-query-map-empty-query");


		assertNotNull(response);
		assertNotNull(response.getPayload());
		assertTrue(response.getPayload() instanceof Iterable);

		Iterable<DBObject> iterable = (Iterable<DBObject>) response.getPayload();

		assertEquals(
				"An empty query map for the query should list all the files", 3,
				MongoHelper.getIterableSize(iterable));

		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
	}


	@Category({ RegressionTests.class })
	@Test
	public void testListFilesUsingQueryMap_nonemptyQuery() {
		MuleMessage response = null;
		try {
			response = runFlowAndGetMessage("list-files-using-query-map-non-empty-query");


		assertNotNull(response);
		assertNotNull(response.getPayload());
		assertTrue(response.getPayload() instanceof Iterable);

		Iterable<DBObject> iterable = (Iterable<DBObject>) response.getPayload();

		assertEquals(
				"Listing files with a query with key " + getTestRunMessageValue("key")
						+ " and value " + getTestRunMessageValue("value")
						+ " should give 2 results", 2, MongoHelper.getIterableSize(iterable));
		
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
		

	}
}
