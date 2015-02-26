/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api.automation;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.module.mongo.automation.SmokeTests;
import org.mule.module.mongo.automation.testcases.CreateCollectionTestCases;
import org.mule.module.mongo.automation.testcases.CreateFileFromPayloadTestCases;
import org.mule.module.mongo.automation.testcases.CreateIndexTestCases;
import org.mule.module.mongo.automation.testcases.DropCollectionTestCases;
import org.mule.module.mongo.automation.testcases.DropIndexTestCases;
import org.mule.module.mongo.automation.testcases.ExistsCollectionTestCases;
import org.mule.module.mongo.automation.testcases.FindObjectsTestCases;
import org.mule.module.mongo.automation.testcases.FindOneObjectUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.InsertObjectTestCases;
import org.mule.module.mongo.automation.testcases.ListIndicesTestCases;
import org.mule.module.mongo.automation.testcases.SaveObjectFromMapTestCases;
import org.mule.module.mongo.automation.testcases.SaveObjectTestCases;

@RunWith(Categories.class)
@IncludeCategory(SmokeTests.class)
@SuiteClasses({ CreateCollectionTestCases.class,
		CreateFileFromPayloadTestCases.class, CreateIndexTestCases.class,
		DropCollectionTestCases.class, DropIndexTestCases.class,
		ExistsCollectionTestCases.class, FindObjectsTestCases.class,
		FindOneObjectUsingQueryMapTestCases.class, ListIndicesTestCases.class,
		InsertObjectTestCases.class, SaveObjectFromMapTestCases.class,
		SaveObjectTestCases.class })
public class SmokeTestSuite {
}
