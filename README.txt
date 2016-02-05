iib9-plugin notes:
------------------

**************************
- initialize - executes InitializeBarBuildWorkspace
  - verifies that the configured workspace directory exists.  If it doesn not exist, then the workspace directory structure will be created. 
  - cleanDirectories:  This allows one to enter a comma-separated-list of directories and files to "clean" (ie: "delete") at build startup.
  
  
**************************  
- generate-resources - executes PrepareBarBuildWorkspace
  - regarding useClassloaders setting:
    - setting the useClassloaders value to false causes this goal to break when a pom.xml cannot be found in a dependent library.
    - setting the useClassloaders value to true allows the goal to work if a pom.xml is not present in a dependent library.
	- if useClassloaders is set to false, then the goal breaks due to "META-INF\pom.xml (The system cannot find the file specified)"

  - getDependencyDirectories - just gets a list of directories under the defined workspace directory, it excludes directories that start with a "*."	
  
  - copyJarDepencies breaks - because it makes the assumption that each directory under the defined workspace directory will have a pom.xml file. 
  
  - I updated the code to not break if no pom.xml file is found.  
	
  - The "unpackDependencyLocation" allows one to define where the pom's dependencies are unpacked.  Previously, this was defaulting to the workspace.
  
  - The "dependenciesDirectoryParent" can also be defined.  It defaults to the workspace when not defined. This strips out pom.xml files from dependency
    directories.  It also determines the location of jar files.  If this "dependenciesDirectoryParent" value is set to the workspace directory (..) above 
	the project file containing a pom, then the jar files will be unpacked to the root of the project.  
	
PrepareBarBuildWorkspace - What does it do?  	
  1. It goes out and gets dependencies of a certain type.  You define the dependencies in pom.xml as you normally would.  And you restrict what gets downloaded and unpacked using the unpackIibDependencyTypes configuration parameter. For example, use the following configuration to download and unpack zip and jar file dependencies: <unpackIibDependencyTypes>zip,jar</unpackIibDependencyTypes>. It does the downloading and unpacking using the maven plugin unpack-dependencies mojo.  
  
  2.  Also, it only unpacks dependencies that are of scope 'compile'.   Dependencies that are not of scope compile do not get downloaded and unpacked. 
  Thus, an existing project in the workspace can be a maven dependency and does not have to be uploaded as a project to a maven repository.  
    

  3. It uses another mechanism to get dependencies by looking for Pom.xml in the existing runtime jar files.  In this case, the dependent jar itself gets installed in the META-INF directory under the workspace directory. Settign the useClassloaders to false will cause this to occur.  
  
Further Notes: 

   If the sibling projects also have a pom.xml and the dependenciesDirectoryParent is defined as the workspace, then the plugin will attempt to strip out the other projects' pom.xml files.   As a temporary workaround, let the dependenciesDirectoryParent be the target directory in the project (\HDR\target) for example.  Stripping out the pom.xml from sibling projects won't make sense if the other projects in a workspace have been mavenized.  

   
  
  
  
**************************   
  - process-resources - executes ValidateBarBuildWorkspace
    - this lists the files in the workspace directory and throws an error if the file is not a directory containing a .project file with a name matching the directory name.
	- introduced code that switches on the enforceProjectSubdirectories configuration parameter. 
	- code now only checks directories under the workspace directory when the enforceProjectSubdirectories parameter is set to true.
	- if enforceProjectSubdirectories is set to false, then this code basically does nothing
	- by default this configuration value is set to false.
	
************************** 	
  - compile - executes PackageBarMojo
  
    - the barName has to be specified (or it uses the default value)
	  - where you place the bar is open to the configuration value
	  - so if the bar's location is specified to be in a non-existent directory, then this Mojo's execute method will create the path to the bar
	  
  	- the mojo then has two routes (with mqsicommands and without mqsicommands)

	- without mqsi commands - is also known as execute mqsi package bar
	  - executes constructParams
		- construct commands adds:
		  - -a command with the barName
		  - -w command with the workspace
		  - calls addObjectsAppLib
		    - addObjectsAppLib does the following:
			  - it gets a list of the direct PROJECT dependencies ( so, in the project dependencies in the demo project, 
			    it should find direct project dependency projectA )
				- for each of the direct project dependencies: 
				  - it checks the direct project dependency's .project files nature to determine if the dependency is an 
				  app or library.  
				    - *** this will only work if the direct project dependency file were extracted to the workspace directory and not into the project directory. 
					
					  - example structure needed:
					  
					     /ibm/workspace/   (defined as workspace in pom.xml configuration)
						 /ibm/workspace/myproject
						 /ibm/workspace/myproject/*.*    (all the artifacts in my project)
						 /ibm/workspace/dependentAppOrLibA/   ( a dependency specified in myproject's pom.xml file)
						 /ibm/workspace/dependentAppOrLibA/.project  ( the eclipse project file for the the dependent app or lib )
						 
					- problem: the dependency project needs to be zipped up in nexus (maven repository) with the right structure
						
				      - example correct structure within zip (zip,bar,jar, whatever):
					  
					      /dependencyAppOrLibA/  (the root directory within the zip)
					      /dependencyAppOrLibA/.project   (the dependent project name in the .project file needs to match the zipped up root directory name)
						  /dependencyAppOrLibA/*.*     contents....
										
				    - SO, to add a dependent library or app:
					  - the workspace has to be a level above the project
					  - the dependent project need to have the correct structure and be available in the maven repository (nexus)
					  - the dependent artifacts need to be extracted to a directory in the workspace
					  
				  - brett added a fix so that the build does not fail if the directory structure is not correct.  just a warning message gets produced.  

		      - addObjectLibs adds a '-k' parameter and each app's project name
			  - addObjectLibs adds a '-y' parameter and each lib's project name
              - if no apps and no lib's, then adds an '-o' parameter along with the results of 'getObjectNames' method
			    - 'getObjectNames' gets the names of files under the workspace directory, matching includeFlowPatterns, not matching anything in a
                  directory called "tempfiles", excluding the base directory. 


************************** 	
  - process-classes - executes ApplyBarOverridesMojo

	ApplyBarOverridesMojo:
	
	- executes copyAndFilterResources()
	    - validates that the default maven resource directories are present, if not, then log warning is issued.  Warnings are also issued if no resources are found. 
		- copies the resources in the src/main/resources directory to [project build directory + "/iib"] using the copy-resources goal of the maven-resources-plugin
		- copies the test resources in the src/test/resources directory to [project build directory + "/iib-test"] using the copy-resources goal of the maven-resources-plugin
	- executes getOverridable Properties
	    - Reads the just-generated bar file and gets a list of overridable properties.
		- Writes the overridable properties to a default properties file
		- validates that any properties file resources in the src/main/resources and src/test/resources have the same properties that can be overridden in the default properties file
	- executes executeApplyBarOverrides
		- generates an overriden bar file for each property file in the src/main/resources and src/test/resources directories.
		- each overridden property file is written to its respective target/iib/ or target/iib-test/ directory.

************************** 			
  - process-classes - also executes CleanBarBuildWorkspaceMojo
	- deleted entire workspace if "wipeoutWorkspace" argument is set to true (this would only make sense on a build server)
	
************************** 	
  - process-classes - also executes ValidateClassloaderApproachMojo

	- goes through the default properties file from "readbar" step of ApplyBarOverridesMojo
		- finds the properties associated with classloaders (every property name that ends with ".javaClassLoader")
	- validates that the classLoader value matches the "useClassloaders" configuration in the pom.xml.
	- if an inconsistency exists and the "failOnInvalidClassloader" is set to true, then the build will fail
				  
iib9-maven-plugin configuration parameters:
-------------------------------------------

* workspace:  The path of the workspace in which the projects are extracted to be built. defaults to ${project.build.directory}/iib/workspace. it will be created if it doesn't exist.  

* initialDeletes:  a comma-separated-list of files, directories, or regular expressions for files/directories in the workspace to delete during initialization phase. 

* useClassloaders:  indicates whether classloaders are in use with this bar ; defaults to false; false adds dependent jars into workspace META-INF dir; true does not; both true and false will unpack dependent resource
     
* unpackIibDependencyTypes: a comma-separated-list of dependency types to be unpacked.  defaults to zip;  the dependency types must have compile scope.

    example:   <unpackIibDependencyTypes>zip</unpackIibDependencyTypes>
	
	    
* unpackDependencyLocation: unpacks dependencies of compile scope to the specified directory.  If not defined, then the workspace directory will be used. 	    
	
	note:  past uses of this plugin used the workspace as the unpackDependencyLocation where zipped up dependent projects complete with .project and pom.xml files were uploaded to the maven repository, specified as a dependency in the target project's pom.xml, then unzipped as siblings to the project in the workspace.  
	
* dependencyDirectoriesParent: defaults to the workspace when not defined. This strips out pom.xml files from dependency
    directories.  It also determines the location of jar files.  If this "dependenciesDirectoryParent" value is set to the workspace directory (..) above 
	the project file containing a pom, then the jar files will be unpacked to the root of the project.
	  
* enforceProjectSubdirectories: defaults to false;  if set to true, then throws an error if each directory directly under the workspace directory does not have a .project file. 

	example:  If workspaceX directory has three child directories: ChildA, ChildB and ChildC, then each Child directory must have a .project file where the defined project name matches the directory name. 

* useMqsiCommands: indicates whether mqsicommands are used to create bar; defaults to false;	

* pathToMqsiProfileScript: The absolute path to the mqsiprofile script;  This is necessary if useMqsiCommands is set to true.  		

* barName:  The name of the BAR (compressed file format) archive file where the result is stored. defaults to: ${project.build.directory}/iib/${project.artifactId}-${project.version}.bar
	
	example:  <barName>${project.build.directory}/${project.artifactId}-${project.version}.bar</barName>
		
* packageBarTraceFile: The name of the trace file to use when packaging bar files; defaults to ${project.build.directory}/packagebartrace.txt		
		
* excludeArtifactsPattern:  comma-separated-list of artifacts' patterns' to exclude from the bar file. defaults to **/pom.xml

	example: <excludeArtifactsPattern>**/pom.xml,**/.metadata/**</excludeArtifactsPattern> 

* includeArtifactsPattern: comma-separated-list of artifacts' patterns' to include in the bar file. 

	defaults to: **/*.xsdzip,**/*.tblxmi,**/*.xsd,**/*.wsdl,**/*.dictionary,**/*.xsl,**/*.xslt,**/*.xml,**/*.jar,**/*.inadapter,**/*.outadapter,**/*.insca,**/*.outsca,**/*.descriptor,**/*.php,**/*.idl,**/*.map,**/*.msgflow
	
	example: <includeArtifactsPattern>**/*.xsdzip,**/*.tblxmi,**/*.xsd,**/*.wsdl,**/*.dictionary,**/*.xsl,**/*.xslt,**/*.xml,**/*.jar,**/*.inadapter,**/*.outadapter,**/*.insca,**/*.outsca,**/*.descriptor,**/*.php,**/*.idl,**/*.map,**/*.msgflow</includeArtifactsPattern>
	
	note: If 'useMqsiCommands' is set to false, then the bar file will be generated without compiling.  In this case, it is recommended to include patterns: '**/*.esql' and '**/*.subflow'.

* addIndividualObjects: boolean that indicates whether or not individual objects are added in mqsi mode.  If false, then objects will be added using the mqsicreatebar -a application approach. If true, then the objects will be added using the mqsicreatebar... -o objects... approach.  

	


					
					
	
	
	