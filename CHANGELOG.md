MongoDB Connector Release Notes
=================================

Date: 12-MAY-2014

Version: 3.4.5

Supported API versions
------------------------
Mongo 2.11.4 API. Please visit this link for detail http://api.mongodb.org/java/2.11.4

Supported Mule Runtime Versions
--------------------------------
3.5.0

New Features and Functionality
------------------------------
- Migrated to Devkit 3.5.0
- Removed @Default from database in @Connect
- Upgraded test framework version to 2.0.6
- Fixed test cases
	- Tests working on files where set to use the payloadContent attribute
- Supported MongoDB version: 2.6.1
- Supported operations:
	- addUser
	- countObjects
	- countObjectsUsingQueryMap
	- createCollection
	- createFileFromPayload
	- createIndex
	- dropCollection
	- dropDatabase
	- dropIndex
	- dump
	- executeCommand
	- existsCollection
	- findFiles
	- findFilesUsingQueryMap
	- findObjects
	- findObjectsUsingQueryMap
	- findOneFile
	- findOneFileUsingQueryMap
	- findOneObject
	- findOneObjectUsingQueryMap
	- getFileContent
	- getFileContentUsingQueryMap
	- incrementDump
	- insertObjectFromMap
	- insertObject
	- listCollection
	- listFiles
	- listFilesUsingQueryMap
	- listIndices
	- MapReduceObjects
	- RemoveFiles
	- RemoveFilesUsingQueryMap,
	- RemoveObjects
	- RemoveObjectsUsingQueryMap,
	- Restore
	- SaveObjectFromMap
	- SaveObject
	- UpdateObjectsByFunction
	- UpdateObjectsByFunctionUsingMap
	- UpdateObjects
	- UpdateObjectsUsingMap
	- UpdateObjectsUsingQueryMap

Closed Issues in this release
------------------------------
N/A

Known Issues in this release
------------------------------
N/A

Earlier Release Notes
----------------------
2.2.0
=====

- Improves Mule Studio support
- New Studio demo
- Adds query pagination
- Adds support for connecting to Mongo replica set
- Enhancements for MongoDB connection options
- Bug fixing and unit tests

