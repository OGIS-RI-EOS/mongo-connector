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
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.module.mongo.automation.testcases.AddUserTestCases;
import org.mule.module.mongo.automation.testcases.CountObjectsTestCases;
import org.mule.module.mongo.automation.testcases.CountObjectsUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.CreateCollectionTestCases;
import org.mule.module.mongo.automation.testcases.CreateFileFromPayloadTestCases;
import org.mule.module.mongo.automation.testcases.CreateIndexTestCases;
import org.mule.module.mongo.automation.testcases.DropCollectionTestCases;
import org.mule.module.mongo.automation.testcases.DropDatabaseTestCases;
import org.mule.module.mongo.automation.testcases.DropIndexTestCases;
import org.mule.module.mongo.automation.testcases.DumpTestCases;
import org.mule.module.mongo.automation.testcases.ExecuteCommandTestCases;
import org.mule.module.mongo.automation.testcases.ExistsCollectionTestCases;
import org.mule.module.mongo.automation.testcases.FindFilesTestCases;
import org.mule.module.mongo.automation.testcases.FindFilesUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.FindObjectsTestCases;
import org.mule.module.mongo.automation.testcases.FindObjectsUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.FindOneFileTestCases;
import org.mule.module.mongo.automation.testcases.FindOneFileUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.FindOneObjectTestCases;
import org.mule.module.mongo.automation.testcases.FindOneObjectUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.GetFileContentTestCases;
import org.mule.module.mongo.automation.testcases.GetFileContentUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.IncrementalDumpTestCases;
import org.mule.module.mongo.automation.testcases.InsertObjectFromMapTestCases;
import org.mule.module.mongo.automation.testcases.InsertObjectTestCases;
import org.mule.module.mongo.automation.testcases.ListCollectionTestCases;
import org.mule.module.mongo.automation.testcases.ListFilesTestCases;
import org.mule.module.mongo.automation.testcases.ListFilesUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.ListIndicesTestCases;
import org.mule.module.mongo.automation.testcases.MapReduceObjectsTestCases;
import org.mule.module.mongo.automation.testcases.PoolingTestCases;
import org.mule.module.mongo.automation.testcases.RemoveFilesTestCases;
import org.mule.module.mongo.automation.testcases.RemoveFilesUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.RemoveObjectsTestCases;
import org.mule.module.mongo.automation.testcases.RemoveObjectsUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.RestoreTestCases;
import org.mule.module.mongo.automation.testcases.SaveObjectFromMapTestCases;
import org.mule.module.mongo.automation.testcases.SaveObjectTestCases;
import org.mule.module.mongo.automation.testcases.UpdateObjectsByFunctionTestCases;
import org.mule.module.mongo.automation.testcases.UpdateObjectsByFunctionUsingMapTestCases;
import org.mule.module.mongo.automation.testcases.UpdateObjectsTestCases;
import org.mule.module.mongo.automation.testcases.UpdateObjectsUsingMapTestCases;
import org.mule.module.mongo.automation.testcases.UpdateObjectsUsingQueryMapTestCases;


@RunWith(Categories.class)
@IncludeCategory(RegressionTests.class)

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

public class RegressionTestSuite {
	
}