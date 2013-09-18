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

public class RemoveFilesUsingQueryMapTestCases extends MongoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		testObjects = (HashMap<String, Object>) context.getBean("removeFilesUsingQueryMap");
		
		createFileFromPayload(testObjects.get("filename1"));
		createFileFromPayload(testObjects.get("filename1"));
		createFileFromPayload(testObjects.get("filename2"));
	}
	
	@After
	public void tearDown() {
		deleteFilesCreatedByCreateFileFromPayload();
	}
	
	@Category({ RegressionTests.class })
	@Test
	public void testRemoveFilesUsingQueryMap_emptyQuery() {
		try {
			MessageProcessor removeFilesFlow = lookupMessageProcessorConstruct("remove-files-using-query-map-empty-query");
			MuleEvent event = getTestEvent(testObjects);
			removeFilesFlow.process(event);

			assertEquals("There should be 0 files found after remove-files-using-query-map with an empty query", 0,
					findFiles());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Category({ RegressionTests.class })
	@Test
	public void testRemoveFilesUsingQueryMap_nonemptyQuery() {
		try {
			MessageProcessor removeFilesFlow = lookupMessageProcessorConstruct("remove-files-using-query-map-non-empty-query");
			MuleEvent event = getTestEvent(testObjects);
			removeFilesFlow.process(event);

			assertEquals("There should be 1 files found after removing files with a filename of " + testObjects.get("value"), 1,
					findFiles());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

}
