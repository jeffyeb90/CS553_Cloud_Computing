1. Manual for source_workload_generator

The 3 source files all use Java standard libraries.
User can use IDE to import them under same package.
Then compile and run. The 411 dataset will be generated under the project folder.



2. Manual for source_deploy_maven

Firstly, please make sure that you have jdk 1.7 and maven 3.1.0+ properly installed.

Then, please go under ./source_deploy_maven and run:

	mvn clean install

to set everything ready.

Run:
	mvn appengine:devserver

to run this application locally.

This program is also deployed on GAE. User can access it using the link below:
 
	http://qualified-cacao-745.appspot.com/ 
