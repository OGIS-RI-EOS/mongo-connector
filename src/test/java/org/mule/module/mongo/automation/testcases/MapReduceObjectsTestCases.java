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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.api.MongoCollection;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MapReduceObjectsTestCases extends MongoTestParent {



	@Before
	public void setUp() throws Exception {
			// Create the collection
			initializeTestRunMessage("mapReduceObjects");
			runFlowAndGetPayload("create-collection");


			int numApples = (Integer) getTestRunMessageValue("numApples");
			int numOranges = (Integer) getTestRunMessageValue("numOranges");
			
			// Create sample objects with which we can map reduce
			List<DBObject> objects = new ArrayList<DBObject>();
			for (int i = 0; i < numApples; i++) {
				DBObject obj = new BasicDBObject("item", "apple");
				objects.add(obj);
			}
			
			for (int i = 0; i < numOranges; i++) {
				DBObject obj = new BasicDBObject("item", "orange");
				objects.add(obj);
			}
			
			// Insert the objects into the collection
			insertObjects(objects);
			

	}

	@Category({RegressionTests.class})
	@Test
	public void testMapReduceObjects() {
		try {

			int numApples = (Integer) getTestRunMessageValue("numApples");
			int numOranges = (Integer) getTestRunMessageValue("numOranges");
			
			MongoCollection resultCollection = runFlowAndGetPayload("map-reduce-objects");
			assertTrue(resultCollection != null);
			assertTrue(resultCollection.size() == 2); // We only have apples and oranges
								
			for (DBObject obj : resultCollection) {
				DBObject valueObject = (DBObject) obj.get("value");
				assertNotNull(valueObject);
				if (obj.get("_id").equals("apple")) {
					assertTrue(valueObject.containsField("count"));
					assertTrue((Double)valueObject.get("count") == numApples); // map reduce returns doubles, typecast to Double and compare
				}
				else {
					if (obj.get("_id").equals("orange")) {
						assertTrue(valueObject.containsField("count"));
						assertTrue((Double)valueObject.get("count") == numOranges); // map reduce returns doubles, typecast to Double and compare
					}
					else fail();
				}
			}
		
		} catch (Exception e) {
	         fail(ConnectorTestUtils.getStackTrace(e));
	    }

	}
	
	@After
	public void tearDown() throws Exception {
			String outputCollection = getTestRunMessageValue("outputCollection").toString();
			
			// drop the collection
			runFlowAndGetPayload("drop-collection");

			// drop the output collection
			// replace the "collection" entry so that the drop-collection flow drops the correct collection
			upsertOnTestRunMessage("collection", outputCollection);
			runFlowAndGetPayload("drop-collection");

	}

	
}
