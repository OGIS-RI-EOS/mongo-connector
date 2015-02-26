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

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class CountObjectsUsingQueryMapTestCases extends MongoTestParent {


	@Before
	public void setUp() throws Exception {
			// Create collection
			initializeTestRunMessage("countObjectsUsingQueryMap");
			runFlowAndGetPayload("create-collection");

	}

	@After
	public void tearDown() throws Exception {
			// Delete collection
			runFlowAndGetPayload("drop-collection");

	}
	
	@Category({ RegressionTests.class })
	@Test
	public void testCountObjectsUsingQueryMap_without_map() {
		int numObjects = (Integer) getTestRunMessageValue("numObjects");
		insertObjects(getEmptyDBObjects(numObjects));
		
		try {
			assertEquals(new Long(numObjects), (Long) runFlowAndGetPayload("count-objects-using-query-map-without-query"));
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }	


	}

	@Category({ RegressionTests.class })
	@Test
	public void testCountObjectsUsingQueryMap_with_map() {
		List<DBObject> list = getEmptyDBObjects(2);

		String queryAttribKey = getTestRunMessageValue("queryAttribKey").toString();
		String queryAttribVal = getTestRunMessageValue("queryAttribVal").toString();
		
		DBObject dbObj = new BasicDBObject();
		dbObj.put(queryAttribKey, queryAttribVal);
		list.add(dbObj);

		insertObjects(list);

		try {
			assertEquals(new Long(1), (Long) runFlowAndGetPayload("count-objects-using-query-map-with-query"));
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }
		
	}

}
