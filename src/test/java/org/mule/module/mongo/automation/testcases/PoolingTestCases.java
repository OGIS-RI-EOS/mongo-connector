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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.mongo.automation.MongoTestParent;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import com.mongodb.BasicDBObject;

public class PoolingTestCases extends MongoTestParent {

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
            // Create collection
            initializeTestRunMessage("countObjects");
            runFlowAndGetPayload("create-collection");

    }

    @After
    public void tearDown() throws Exception {
            // Delete collection
            runFlowAndGetPayload("drop-collection");

    }

    @Category({ RegressionTests.class })
    @Test
    public void testPoolSizeDoesNotExceedConfiguration() throws Exception {

        int numObjects = (Integer) getTestRunMessageValue("numObjects");

        insertObjects(getEmptyDBObjects(numObjects));

        Integer startingConnections = Integer.parseInt((String) runFlowAndGetPayload("count-open-connections"));
        
        upsertOnTestRunMessage("queryRef", new BasicDBObject());

        for (int i = 0; i < 32; i++) {
            try {
            	runFlowAndGetPayload("count-objects");
    		} catch (Exception e) {
   	         fail(ConnectorTestUtils.getStackTrace(e));
   	    }

        }

        int newConnections = Integer.parseInt((String) runFlowAndGetPayload("count-open-connections")) - startingConnections;
        assertTrue("Too many new connections (" + newConnections + ", ", newConnections <= 2);
    }}
