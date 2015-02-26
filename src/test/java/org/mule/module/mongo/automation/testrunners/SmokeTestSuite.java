/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.automation.testrunners;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.module.mongo.automation.SmokeTests;
import org.mule.module.mongo.automation.testcases.*;


@RunWith(Categories.class)
@IncludeCategory(SmokeTests.class)

@SuiteClasses({ 
	AddUserTestCases.class,
	CountObjectsTestCases.class,
	CountObjectsUsingQueryMapTestCases.class,
	CreateCollectionTestCases.class,
	CreateFileFromPayloadTestCases.class,
	CreateIndexTestCases.class,
	DropCollectionTestCases.class,
	DropDatabaseTestCases.class,
	DropIndexTestCases.class,
	DumpTestCases.class,
	ExecuteCommandTestCases.class,
	ExistsCollectionTestCases.class,
	FindFilesTestCases.class,
	FindFilesUsingQueryMapTestCases.class,
	FindObjectsTestCases.class,
	FindObjectsUsingQueryMapTestCases.class,
	FindOneFileTestCases.class,
	FindOneFileUsingQueryMapTestCases.class,
	FindOneObjectTestCases.class,
	FindOneObjectUsingQueryMapTestCases.class,
	GetFileContentTestCases.class,
	GetFileContentUsingQueryMapTestCases.class,
	IncrementalDumpTestCases.class,
	InsertObjectFromMapTestCases.class,
	InsertObjectTestCases.class,
	ListCollectionTestCases.class,
	ListFilesTestCases.class,
	ListFilesUsingQueryMapTestCases.class,
	ListIndicesTestCases.class,
	MapReduceObjectsTestCases.class,
	PoolingTestCases.class,
	RemoveFilesTestCases.class,
	RemoveFilesUsingQueryMapTestCases.class,
	RemoveObjectsTestCases.class,
	RemoveObjectsUsingQueryMapTestCases.class,
	RestoreTestCases.class,
	SaveObjectFromMapTestCases.class,
	SaveObjectTestCases.class,
	UpdateObjectsByFunctionTestCases.class,
	UpdateObjectsByFunctionUsingMapTestCases.class,
	UpdateObjectsTestCases.class,
	UpdateObjectsUsingMapTestCases.class,
	UpdateObjectsUsingQueryMapTestCases.class
		})

public class SmokeTestSuite {
	
}