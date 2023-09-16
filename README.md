<h2><a name="Introduction"></a>Introduction to the IBM IIB version 9 Maven Build Lifecycle (2017)</h2>
The IIB9 Maven Plug-In is a comprehensive rework of the IIB-Maven-Plug-In originally developed by the Swiss SBB.  Its code is derived from the original 
project Snapshot dated to October 2015 and found at <a href="https://github.com/SchweizerischeBundesbahnen/iib-maven-plugin">https://github.com/SchweizerischeBundesbahnen/iib-maven-plugin</a>.  
The rework includes the best pieces of the original work as well as new pieces that make the IIB9 build experience more closely match what developers and architects 
expect of a maven build life-cycle.  The IIB9 Maven Plugin has been updated to support IBM's IIB Version 10 and to provide much better Linux support.  The Version 10 documentation can be found at <a href="http://vadosity.com/maven/">http://vadosity.com/maven/</a>  
<p>The reworked source code for this IIB-Maven-Plugin is available on GitHub at <a href="https://github.com/bretthshelley/Maven-IIB9-Plug-In">https://github.com/bretthshelley/Maven-IIB9-Plug-In</a>.
Questions may be sent directly to <a href="mailto:bshelley585@gmail.com">bshelley585@gmail.com</a>.

<li><a href="#QuickStart">Quick-Start</a></li> 
<ul>
	<li><a href="#InstallMaven">1. Install Maven</a></li>
	<li><a href="#UpdateMavenSettings">2. Update Maven Settings</a></li>
	<li><a href="#GetPlugin">3. Download this Plug-In</a></li>
	<li><a href="#InstallProxy">4. Install the ConfigManagerProxy in your local repository</a></li>
	<li><a href="#InstallPlugin">5. Install this Plug-In in your local repository</a></li>
	<li><a href="#RunningHelp">6. Generate this help file (Optional)</a></li>
	<li><a href="#Mavenize">7. Mavenize an IIB9 workspace</a></li>
	<li><a href="#UploadLibs">8. Upload Common Libraries to a Maven Distribution Repository (Optional)</a></li>
	<li><a href="#DeleteOtherProjects">9. Delete All-but-One Project (Optional)</a></li>
	<li><a href="#VerifyPom">10. Verify Pom.xml</a></li>
	<li><a href="#RunGenerateResources">11. Run Generate Resources</a></li>
	<li><a href="#RunCompile">12. Run Compile</a></li>
	<li><a href="#RunTestCompile">13. Run Test Compile</a></li>
	<li><a href="#RunProcessClasses">14. Run Process Classes</a></li>
	<li><a href="#RunPreIntegrationTest">15. Run Pre-Integration-Test</a></li>
	<li><a href="#RunPreIntegrationTest">16. Run Integration-Test</a></li>
	<li><a href="#RunDeploy">17. Run Deploy</a></li>
</ul>
</li>

<div class="section">
<h3><a name="Overview">IIB9 Maven Plug-In Overview</a></h3>
<p>This document offers many details and tips to use the Maven IIB9 Plug-In.  It needs to since the IIB BAR lifecycle differs significantly from a normal maven artifact like a jar file.  
However, once your project is setup correctly, one should only need to run the command <tt>mvn clean deploy</tt> - or better yet, just check in your code and let a CI server like Jenkins do it for you. So, the extensive information below is presented for your setup assistance with the ultimate objective of <b>keeping it simple</b>. The <a href="#QuickStart">Quick-Start</a> section of this document goes through the full setup process.  The <a href="MavenIIB9PlugIn.pptx">MavenIIB9PlugIn.pptx</a> powerpoint presentation provides an illustrated explanation of the plug-in's capabilities. 
<p><u>The plug-in performs the following:</u>
<ul>
<li>Mavenizes an IIB9 workspace by generating pom.xml files for each project as well as a parent pom with library modules</li>
<li>Deploys versioned IIB9 libraries to a maven repository (Optional)</li>
<li>Downloads a project's Dependent IIB9 libraries from a repository into an IIB9 workspace (Optional)</li>
<li>Downloads a project's Dependent Jars from a repository into an IIB9 application project</li>
<li>Builds a broker archive (BAR) with compiled message flows:</li>
<li>Compiles java-based unit and integration tests
<li>Validates property files containing BAR overrides</li>
<li>Performs BAR overrides on any number of properties files</li>
<li>Deploys BAR files to any number of defined environments</li>
<li>Executes Integration Tests on Deployed BARs</li>
<li>Uploads versioned BAR files to a configured Maven Distribution Repository</li>
</ul>

</div>

<div class="section">
<h3><a name="Basics">IIB9 Maven Plug-In Build Lifecycle Basics</a></h3>
<p>The IIB9 Maven Plug-In is based around the central concept of a build lifecycle. What this means is that the process for building and distributing a particular artifact (library project or broker archive -BAR) is clearly defined.</p>
<p>For the person building a project, this means that it is only necessary to learn a small set of commands to build an IIB9 Maven project, and the POM will ensure they get the results they desired.</p>
<p>This plugin has several build lifecycles:  The main <tt>iib-bar</tt> lifecycle automates IIB9 BAR file creation and deployment to a Broker Node.The <tt>iib-bar</tt> lifecycle will perform most day-to-day operations. The <tt>iib-zip</tt> lifecycle zips up versioned common IIB9 library projects and deploys them to a Maven repository for use in IIB9 application projects.</p>
</div>


<div class="section">
<h4><a name="IibBarLifecycle">The iib-bar Build Lifecycle</a></h4>
<p>The iib-bar lifecycle comprises of the following phases:</p>
<ul>
  <li><tt>clean</tt> - performs the standard cleanup and removal of a project's target directory</li>
  <li><tt>initialize</tt> - performs any additional file cleanup</li>
  <li><tt>generate-resources</tt> - copies jar dependencies into project and downloads any common library dependencies into workspace</li>
  <li><tt>process-resources</tt> - validates that each project in a workspace has correct format (project name = directory name)</li>
  <li><tt>compile</tt> - creates a broker archive using mqsicreatebar command (recommended) or using package bar.</li>
  <li><tt>test-compile</tt> - compiles unit and integration tests in the src/test/java directory.
  <li><tt>process-classes</tt> - applies bar overrides using properties files found in resources folders and validates classloader approach</li>
  <li><tt>pre-integration-test</tt> - deploys a bar file to a configured broker and execution server</li>
<li><tt>integration-test</tt> - executes integration tests against the just-deployed bar on the configured broker</li>
<li><tt>verify</tt> - evaluates integration test results and stops build in event of failure(s)
<li><tt>deploy</tt> - done in an integration or release environment, copies the final bar to the remote repository for sharing with other developers and projects.</li>
</ul>
<p>These lifecycle phases are executed sequentially to complete the <tt>iib-bar</tt> lifecycle. Given the lifecycle phases above, this means that when the iib-bar lifecycle is used, Maven will initialize the project, download dependencies, create a default bar file, apply bar overrides on any and all properties files in the src/main/resources against the default bar file, deploy overridden bar file(s) to defined environments, run integration tests, then deploy the bar file(s) to a maven snapshot or release respository.</p>
<p>To do all this, you only need to call the last build phase to be executed, in this case, <tt>deploy</tt>:</p>
<div>
<pre><b>mvn deploy</b></pre></div>
<p>That is because if you call a build phase in this plug-in, it will execute not only that build phase, but also every build phase prior to the called build phase.  Just like any other Maven plug-in.  Thus, doing</p>
<div>
<pre><b>mvn compile</b></pre></div>
<p>will execute every life cycle phase before it (<tt>initialize</tt>, <tt>generate-resources</tt>, <tt>process-resources</tt>), before executing <tt>compile</tt>.</p>
<p>There are more commands that can be executed with this eMI IIB9 Maven plugin, which will be discussed in the following sections.</p>
</div>

<div class="section">
<h4><a name="IibZipLifecycle">The iib-zip Build Lifecycle</a></h4>
<p>The iib-zip lifecycle makes common libraries available to developers in a read-only fashion. This enables common libraries to be &quot;immutable&quot; and not exposed to accidental developer alterations.  This iib-zip lifecycle should only be periodically executed when versions of common libraries become available. The iib-zip lifecycle is comprised of the following phases:</p>
<ul>
  <li><tt>package</tt> - zips up the contents of an IIB9 library project into a versioned zip file</li>
  <li><tt>install</tt> - installs the versioned, zipped-up IIB9 library project into your local maven repository</li>
  <li><tt>deploy</tt> - copies the zipped-up IIB9 file to the remote repository for sharing with other developers and projects.</li>
</ul>
<p>Given the lifecycle phases above, this means that when the iib-zip lifecycle is used, Maven will zip up the IIB9 library project, install a local version and deploy a versioned zip file to a maven snapshot or release respository.  It's a pretty neat idea - storing common source code projects in a Maven repository.</p>
<p>To do all this, you only need to call the last build phase to be executed, in this case, <tt>deploy</tt>:</p>
<div>
<pre><b>mvn deploy</b></pre></div>

<p>&nbsp;</p>
</div>




<div class="section">
<h3><a name="UsingThePlugIn">Using the IIB9 Maven Plug-In</a></h3>

<div class="section">
<h4><a name="MavenizeYourWorkspace">Mavenize Your Workspace</a></h4>
<p>This plug-in can &quot;mavenize&quot; an entire IIB9 workspace in a few seconds. &nbsp;IIB9 projects get 'mavenized' by adding a pom.xml file in each project's root directory. And the pom.xml files contain maven dependency references that mirror IIB9 project dependencies. &nbsp;Also, mavenizing an IIB9 application project includes adding directories such as &quot;src\main\resources&quot; and &quot;src\test\resources&quot;. &nbsp;These directories facilititate bar overrides and bar deployments with property files and broker files respectively.  The <tt>mavenize</tt> goal in the IIB9 Maven plug-in enables these pom.xml files to be automatically generated with a working configuration. </p>
<p>The simplest way to mavenize a workspace is to run maven at the root of your workspace with the command:
<pre><b>mvn -Doverwrite=true -DgroupId=&lt;enter.your.group.id&gt; -Dversion=1.0-SNAPSHOT  -Dmaven.multiModuleProjectDirectory=%M2_HOME% -Ddistribution.repository=http://www.vadosity.com:8081/nexus/content/repositories/snapshots/ -Dworkspace=&lt;enter\path\to\your\iib9\workspace&gt; ch.sbb.maven.plugins:iib-maven-plugin:9.0-SNAPSHOT:mavenize
</b></pre>

The <tt>mavenize</tt> goal has the following configuration parameters:</p>
<ul>
  <li><tt>overwrite</tt> - true or false value indicating whether existing pom.xml files should be overwritten, defaults to true</li>
  <li><tt>groupId</tt> - the groupId of the maven artifacts, defaults to org.yourorg.yourteam</li>
  <li><tt>distribution.repository</tt> - the url to the repository where the artifacts (bar files, zip files) get deployed, defaults to <a href="http://www.vadosity.com:8081/nexus/content/repositories/snapshots/">http://www.vadosity.com:8081/nexus/content/repositories/snapshots/</a> </li>
  <li><tt>workspace</tt> - the location of the IIB9 workspace, defaults to present working directory</li>
  
</ul>
</div>
<p>&nbsp;</p>

<div class="section">
<h4><a name="WorkspacePom">Workspace Pom.xml with modules</a></h4>

Once a workspace has been mavenized, then a pom.xml file will be present at the root of the workspace.  This workspace pom.xml contains modules that facilitate the automatic deployment of versioned library projects to the configured maven distribution repository.  <b>This workspace pom file enables all of the common libraries to be versioned and uploaded to a maven repository for common use across projects - and the upload process should take less than 30 seconds.</b>  It is also important to note that this feature does not necessarily need to be used.  If developers want to check out common libraries from source control into a workspace, then this activity may be skipped.  The workspace pom.xml file might have the following content: 
<pre>
&lt;project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/maven-v4_0_0.xsd"&gt;
    &lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;

    &lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
    &lt;artifactId&gt;common-libraries-parent-pom&lt;/artifactId&gt;
    &lt;packaging&gt;pom&lt;/packaging&gt;
    &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
    &lt;name&gt;Simple Parent Project&lt;/name&gt;

    &lt;modules&gt;
		&lt;module&gt;EMICommon&lt;/module&gt;
		&lt;module&gt;EMICommonJava&lt;/module&gt;
		&lt;module&gt;HL7v251DFDLLibrary&lt;/module&gt;
		&lt;module&gt;VIECommonLibrary&lt;/module&gt;
		&lt;module&gt;VIEExceptionJava&lt;/module&gt;
    &lt;/modules&gt;

	&lt;distributionManagement&gt;
		&lt;repository&gt;
			&lt;id&gt;distribution.repository&lt;/id&gt;
			&lt;name&gt;distribution.repository&lt;/name&gt;
			&lt;url&gt;http://www.vadosity.com:8081/nexus/content/repositories/snapshots/&lt;/url&gt;
		&lt;/repository&gt;
	&lt;/distributionManagement&gt;
&lt;/project&gt;
</pre>

</div>

<div class="section">
<h4><a name="LibraryPom">Example Library Pom.xml</a></h4>

And each IIB9 application library would have a pom.xml file similar to the following:
<pre>
&lt;project xmlns=&quot;http://maven.apache.org/POM/4.0.0&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
	xsi:schemaLocation=&quot;http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd&quot;&gt;
	&lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
	&lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
	&lt;artifactId&gt;EMICommon&lt;/artifactId&gt;
	&lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
	&lt;packaging&gt;iib-zip&lt;/packaging&gt;
	&lt;build&gt;
		&lt;plugins&gt;
			&lt;plugin&gt;
				&lt;groupId&gt;ch.sbb.maven.plugins&lt;/groupId&gt;
				&lt;artifactId&gt;iib-maven-plugin&lt;/artifactId&gt;
				&lt;version&gt;9.0-SNAPSHOT&lt;/version&gt;
				&lt;extensions&gt;true&lt;/extensions&gt;
				&lt;configuration&gt;
				&lt;/configuration&gt;
			&lt;/plugin&gt;
		&lt;/plugins&gt;		
	&lt;/build&gt;

	&lt;dependencies&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
			&lt;artifactId&gt;EMICommonJava&lt;/artifactId&gt;
			&lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
			&lt;type&gt;zip&lt;/type&gt;
			&lt;scope&gt;compile&lt;/scope&gt;
		&lt;/dependency&gt;
	&lt;/dependencies&gt;

	&lt;distributionManagement&gt;
		&lt;repository&gt;
			&lt;id&gt;distribution.repository&lt;/id&gt;
			&lt;name&gt;distribution.repository&lt;/name&gt;
			&lt;url&gt;http://www.vadosity.com:8081/nexus/content/repositories/snapshots/&lt;/url&gt;
		&lt;/repository&gt;
	&lt;/distributionManagement&gt;
&lt;/project&gt;

</pre>

</div>


<div class="section">
<h4><a name="RunningWorkspacePom">Running the Workspace Pom</a></h4>

Running the 'mvn deploy' command in the workspace root directory is all that needs to be done to publish read-only versions of common libraries across projects.
<pre><b>mvn deploy</b></pre> 
<br>In order to deploy the common libraries to a maven distribution repository, the repository id would also need an entry in the user's .m2/settings.xml file.  For example, the following entry in the settings.xml file would be needed to authenticate with the defined distribution server.  
<pre>
	&lt;server&gt;
	  &lt;id&gt;distribution.repository&lt;/id&gt; 
	  &lt;username&gt;admin&lt;/username&gt;
	  &lt;password&gt;admin123&lt;/password&gt;
    &lt;/server&gt;
 </pre>
</div>

<div class="section">
<h4><a name="BuildingYourIIB9ApplicationProject">Building Your IIB9 Application Project</a></h4>

Mavenizing a workspace also injects a working pom.xml file into each IIB9 application project.   This application project uses the iib-bar lifecycle.  The pom.xml file also contains several configuration parameters to control the build process.  The 'mavenize' goal's objective is to provide a working configuration, but the expectation is that developers and build engineers will change the IIB9 application's pom.xml file configuration to meet team needs.  As an example, after mavenizing a workspace, an IIB9 application project's pom.xml file may appear as follows: 

<pre>
&lt;project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"&gt;
	&lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
	&lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
	&lt;artifactId&gt;C4_HDRVistaInterface&lt;/artifactId&gt;
	&lt;version&gt;1.0-RELEASE&lt;/version&gt;
	&lt;packaging&gt;iib-bar&lt;/packaging&gt;

	&lt;profiles&gt;
		&lt;profile&gt;
			&lt;id&gt;localdev&lt;/id&gt;
			&lt;activation&gt;
      			&lt;activeByDefault&gt;true&lt;/activeByDefault&gt;
    		&lt;/activation&gt;
			&lt;properties&gt;
				&lt;workspace&gt;C:\Users\Brett\IBM\IntegrationToolkit90\4.2\maven-demo-2&lt;/workspace&gt;
				&lt;initialDeletes&gt;**/*.jar&lt;/initialDeletes&gt;
				&lt;unpackIibDependenciesIntoWorkspace&gt;true&lt;/unpackIibDependenciesIntoWorkspace&gt;
				&lt;pathToMqsiProfileScript&gt;C:\Program Files\IBM\MQSI\9.0.0.2\bin\mqsiprofile.cmd&lt;/pathToMqsiProfileScript&gt;
				&lt;failOnInvalidProperties&gt;true&lt;/failOnInvalidProperties&gt;
				&lt;useClassloaders&gt;false&lt;/useClassloaders&gt;
				&lt;failOnInvalidClassloader&gt;true&lt;/failOnInvalidClassloader&gt;
				&lt;createOrPackageBar&gt;package&lt;/createOrPackageBar&gt;
				&lt;completeDeployment&gt;true&lt;/completeDeployment&gt;
				&lt;timeoutSecs&gt;600&lt;/timeoutSecs&gt;
			&lt;/properties&gt;		
		&lt;/profile&gt;	
	&lt;/profiles&gt;
	
	
	&lt;build&gt;
		&lt;plugins&gt;
			&lt;plugin&gt;
				&lt;groupId&gt;ch.sbb.maven.plugins&lt;/groupId&gt;
				&lt;artifactId&gt;iib-maven-plugin&lt;/artifactId&gt;
				&lt;version&gt;9.0-SNAPSHOT&lt;/version&gt;
				&lt;extensions&gt;true&lt;/extensions&gt;
				&lt;configuration&gt;
					&lt;workspace&gt;${workspace}&lt;/workspace&gt;
					&lt;initialDeletes&gt;${initialDeletes}&lt;/initialDeletes&gt;
					&lt;unpackIibDependenciesIntoWorkspace&gt;${unpackIibDependenciesIntoWorkspace}&lt;/unpackIibDependenciesIntoWorkspace&gt;
					&lt;pathToMqsiProfileScript&gt;${pathToMqsiProfileScript}&lt;/pathToMqsiProfileScript&gt;
					&lt;failOnInvalidProperties&gt;${failOnInvalidProperties}&lt;/failOnInvalidProperties&gt;
					&lt;useClassloaders&gt;${useClassloaders}&lt;/useClassloaders&gt;
					&lt;failOnInvalidClassloader&gt;${failOnInvalidClassloader}&lt;/failOnInvalidClassloader&gt;
					&lt;createOrPackageBar&gt;${createOrPackageBar}&lt;/createOrPackageBar&gt;
					&lt;!-- mqsideploy -n b1.broker -e default -a mybar.bar -m -w 600 --&gt;					
					&lt;completeDeployment&gt;${completeDeployment}&lt;/completeDeployment&gt;
					&lt;timeoutSecs&gt;${timeoutSecs}&lt;/timeoutSecs&gt;
					&lt;!-- if timeouts occur on broker, then try command:  mqsichangebroker IB9NODE -g 300 -k 300 --&gt;
					&lt;!-- to enable/disable decision services:  mqsimode IB9Node -x DecisionServices  --&gt;
				&lt;/configuration&gt;
			&lt;/plugin&gt;
		&lt;/plugins&gt;

		&lt;pluginManagement&gt;
			&lt;plugins&gt;
				&lt;!--This plugin's configuration is used to store Eclipse m2e settings only. It has no influence on the Maven build itself.--&gt;
				&lt;plugin&gt;
					&lt;groupId&gt;org.eclipse.m2e&lt;/groupId&gt;
					&lt;artifactId&gt;lifecycle-mapping&lt;/artifactId&gt;
					&lt;version&gt;1.0.0&lt;/version&gt;
					&lt;configuration&gt;
						&lt;lifecycleMappingMetadata&gt;
							&lt;pluginExecutions&gt;
								&lt;pluginExecution&gt;
									&lt;pluginExecutionFilter&gt;
										&lt;groupId&gt;
											ch.sbb.maven.plugins
										&lt;/groupId&gt;
										&lt;artifactId&gt;
											iib-maven-plugin
										&lt;/artifactId&gt;
										&lt;versionRange&gt;
											[9.0-SNAPSHOT,)
										&lt;/versionRange&gt;
										&lt;goals&gt;
											&lt;goal&gt;
												initialize-bar-build-workspace
											&lt;/goal&gt;
										&lt;/goals&gt;
									&lt;/pluginExecutionFilter&gt;
									&lt;action&gt;
										&lt;ignore&gt;&lt;/ignore&gt;
									&lt;/action&gt;
								&lt;/pluginExecution&gt;
							&lt;/pluginExecutions&gt;
						&lt;/lifecycleMappingMetadata&gt;
					&lt;/configuration&gt;
				&lt;/plugin&gt;
			&lt;/plugins&gt;
		&lt;/pluginManagement&gt;
	&lt;/build&gt;

	&lt;dependencies&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
			&lt;artifactId&gt;HL7v251DFDLLibrary&lt;/artifactId&gt;
			&lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
			&lt;type&gt;zip&lt;/type&gt;
			&lt;scope&gt;compile&lt;/scope&gt;
		&lt;/dependency&gt;


		&lt;dependency&gt;
			&lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
			&lt;artifactId&gt;VIECommonLibrary&lt;/artifactId&gt;
			&lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
			&lt;type&gt;zip&lt;/type&gt;
			&lt;scope&gt;compile&lt;/scope&gt;
		&lt;/dependency&gt;


		&lt;dependency&gt;
			&lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
			&lt;artifactId&gt;EMICommon&lt;/artifactId&gt;
			&lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
			&lt;type&gt;zip&lt;/type&gt;
			&lt;scope&gt;compile&lt;/scope&gt;
		&lt;/dependency&gt;

		&lt;dependency&gt;
			&lt;groupId&gt;junit&lt;/groupId&gt;
			&lt;artifactId&gt;junit&lt;/artifactId&gt;
			&lt;version&gt;4.11&lt;/version&gt;
			&lt;scope&gt;test&lt;/scope&gt;
		&lt;/dependency&gt; 
	&lt;/dependencies&gt;
		
		&lt;!-- NOTE:  The Integration Test FailSafe plugin looks for Test classes that follow the **/*IT.java, **/*ITCase.java, or **/IT*.java pattern --&gt;

	&lt;distributionManagement&gt;
		&lt;repository&gt;
			&lt;id&gt;distribution.repository&lt;/id&gt;
			&lt;name&gt;distribution.repository&lt;/name&gt;
			&lt;url&gt;http://www.vadosity.com:8081/nexus/content/repositories/releases/&lt;/url&gt;
		&lt;/repository&gt;
	&lt;/distributionManagement&gt;
&lt;/project&gt;


</pre>

<div class="section">
<h4><a name="ConfigParams">IIB9 Application Configuration Parameters</a></h4>

The pom.xml file's configuration parameters are key to executing the iib-bar lifecycle of the eMI IIB9 Maven plugin.  These parameters control the behavior of the iib-bar lifecycle.  
<ul>
<li><tt>workspace</tt> - the location of the IIB9 workspace directory</li>
<li><tt>initialDeletes</tt> - files to delete/clean from a project ; the filenames can be absolute or follow the ant file patterns</li>
<li><tt>unpackIibDependenciesIntoWorkspace</tt> - indicates whether library projects should be downloaded and unpacked into the workspace;  
For example, if set to true, then the example HDR pom.xml file will download and unzip the HDRJava, EmiCommon, and HL7v251DFDLLibrary into the workspace as part of the build process.  </li>
<li><tt>copyDependentJarsLocation</tt> - location where dependent jars are downloaded to; defaults to project's base directory</li>
<li><tt>createOrPackageBar</tt> - value of 'create' launches mqsicreatebar whereas 'package' packages bar with uncompiled resources</li>
<li><tt>pathToMqsiProfileScript</tt> - the path to the local mqsiprofile script </li>
<li><tt>failOnInvalidProperties</tt> - indicates whether the build should fail if invalid bar override properties are found in a property file which is not a default property for an non-overridden bar file;  During the &quot;process-classes&quot; phase, if a file, like xyz.properties, is found in the src/main/resources directory or the src/test/resources, then the file is used to generate an overridden bar file named xyz.bar. </li>
<li><tt>useClassloaders</tt> - indicates whether class loaders are used; defaults to &quot;false&quot;</li>
<li><tt>failOnInvalidClassloader</tt> - indicates whether build fails if useClassLoader entry differs in overridden property(ies) file(s)</li>
<li><tt>brokerFileName></tt> - the broker file that containing the connection information to use to deploy a bar file to it</li>
<li><tt>barFileName</tt> - the bar file to deploy to the designated broker. </li>
<li><tt>completeDeployment</tt> - indicates whether the bar file deployment should be a full/complete deployment or a partial deployment</li>
<li><tt>timeoutSecs</tt> - the timeout in seconds to wait on a bar file deployment</li>
<li><tt>integrationServerName</tt> - the name of the integration server to deploy a bar file to</li>
<li><tt>mqsiPrefixCommands</tt> -  comma-separated list of commands that will be issued to the underlying operating system before launching the mqsi* command
<li><tt>mqsiCreateBarReplacementCommand</tt> - an advanced parameter that allows you to replace the generated mqsiCreateBar command arguments with your 'tweaked' arguments
<li><tt>mqsiCreateBarCompileOnlyReplacementCommand</tt> - an advanced parameter that allows you to replace the generated mqsiCreateBar -compileOnly command arguments with your 'tweaked' arguments
<li><tt>mqsiDeployReplacementCommand</tt> - an advanced parameter that allows you to replace the generated mqsideploy command arguments with your 'tweaked' arguments
</ul>

</div>

<div class="section">
<h4><a name="RunIIBBAR">Running the IIB-BAR Lifecycle</a></h4>
<p>&nbsp;</p>
<p>Again, to run thru the full iib-bar lifecycle, one need only execute maven in the IIB9 project's base directory. 
<pre>
<b>mvn deploy</b>
</pre>
<p>
Since the iib-bar plug-in's lifecycle is not identical to a typical maven lifecycle (jar, ear, war), it is helpful to step through the 
iib-bar lifecycle's phases to see what happens.  For example:
<ul>
<li>To delete jar files or other defined files in a project's workspace, run: <pre><b>mvn initialize</b></pre></li>
<li>To see jar files copied into a project's base directory and library projects get copied into a workspace, run: <pre><b>mvn generate-resources</b></pre></li>
<li>To validate the projects in an IIB9 workspace, run:<pre><b>mvn process-resources</b></pre></li>
<li>To execute the mqsicreatebar command that generates a bar with compiled message flows for an IIB9 application project, run: <pre><b>mvn compile</b></pre></li>
<li>To compile integration tests to execute during the integration-test phase, run: <pre><b>mvn test-compile</b></pre></li>
<li>To create overridden bar files for each properties file in a projects' src/main/resources and src/test/resources directory, run:<pre><b>mvn process-classses</b></pre></li>
<li>To deploy an overridden bar to a broker node and execution server, run:<pre><b>mvn pre-integration-test</b></pre></li>
<li>To execute integration tests against a just deployed BAR on a broker node, run: <pre><b>mvn integration-test</b></pre></li>
<li>To deploy a bar file to a maven distribution repository, run: <pre><b>mvn deploy</b></pre></li>
</ul>

</p>
</p>
</div>

<div class="section">
<h3><a name="AdditionalNotes">Additional Notes:</a></h3>
<h4><a name="SolvingThePomXmlProblem"><b>Solving the IIB9 pom.xml problem</b></a></h4>
When working with the IIB9 Integration Toolkit's User Interface, the 'CreateBar' command will fail because a pom.xml file is in the project's base directory.  The IDE will generate an error message indicating something to the effect that pom.xml has already been added to the bar file.   To fix this problem, simply rename the pom.xml file to pom.xml.txt file or any file name that does not end with the '.xml' extension.  Then, running maven can be accomplished using the command: <pre>mvn -f pom.xml.txt deploy</pre>.
 
<h4><a name="skipFeature"><b>'skip' and 'skipTo' Feature</b></a></h4>
Executing the entire iib-bar lifecycle can be time consuming.  To save developer/tester time, one can define <tt>skip</tt> and <tt>skipTo</tt> arguments when running the iib-bar lifecycle with maven.  For example, if one wants to skip directly to integration tests after performing a full-deploy, one can execute:  <br>
<pre>mvn -f pom.xml.txt -DskipTo=integration-test verify</pre>
Likewise, the <tt>skip</tt> JVM argument may also be used in a similar fashion with the caveat that one can define multiple 'skip' steps.  The following script would have the same effect as the above <tt>skipTo</tt> command:<br>
<pre>mvn -f pom.xml.txt -Dskip=initialize,generate-resources,process-resources,compile,process-classes,pre-integration-test verify</pre>
Please note that the <tt>skipTo</tt> and <tt>skip</tt> feature are just for developer and tester convenience. The goal of these non-standardized features is to save development and testing time - and not to replace the value of executing the entire iib-bar lifecycle.  

<h4><a name="mqsiPrefixCommands"><b>mqsiPrefixCommands</b> configuration parameter</a></h4>
This plugin's <tt>compile</tt> and <tt>pre-integration-test</tt> goals execute mqsi commands such as mqsiprofile, mqsicreatebar, and mqsideploy.  These commands require that the IBM IIB MQSI software and 
related executables are installed on the system where maven is executed.  And the commands are executed using the java Runtime.getRuntime().exec(String[] args) approach. That is, the commands are executed by 
sending an array of commands directly to the underlying operating system.  In the case of Windows, these OS specific commands are already defined.  However, with other operating systems, the 'mqsiPrefixCommands' commands need to be 
defined using a comma-separated list of commands.  An example set of mqsiPrefix commands might appear like:
<pre>&lt;mqsiPrefixCommands&gt;cmd,/c,"C:\Program Files\IBM\MQSI\9.0.0.2\bin\mqsiprofile.cmd",&,cmd,/c&lt;/mqsiPrefixCommands&gt;</pre><br>
The key is to get the plug-in to execute the desired mqsicommand with the mqsiprofile environment correctly setup.
</div>

<div class="section">
<h3><a name="QuickStart">Quick Start</a></h3>
<h4><a name="InstallMaven">1. Install Maven  (Do this once)</a></h4>
Download and Install Maven.  For installation instructions, go to <a href="https://maven.apache.org/install.html">https://maven.apache.org/install.html</a>.  If you just installed maven, 
then execute Step 3 of this quick start.  The step will fail, but this step will create the directory structure needed to execute step 2.  Then, you can successfully execute step 3 again.
</div>

<div class="section">
<h4><a name="UpdateMavenSettings">2. Update Maven Settings  (Do this once)</a></h4>
In order for maven to upload artifacts to a maven distribution repository such as nexus, it needs to have the repository, admin name and admin password defined in the maven settings.xml file.  On Windows operating systems, this file is normally located at C:\Users\&lt;YourUserName&gt;\.m2\settings.xml.  The Maven settings.xml file can contain user-level maven settings for many projects - so it should not be "lightly" overwritten.  However, if you have just installed maven, then you can safely overwrite your settings.xml file with this linked <a href="settings.xml">settings.xml</a> file.  If you have an existing maven settings.xml file in your user directory, then use <a href="settings.xml">this example</a> to add entries to correctly point at the team's maven repository (nexus).  The maven settings in this example settings.xml file point to www.vadosity.com's nexus server - feel free to use it for testing purposes. 

</div>

<div class="section">
<h4><a name="GetPlugin"><b>3. Download this Plug-In (Do this once)</b></a></h4>
Open up a command prompt and navigate to a directory such as c:\temp.  Execute the following command to get a copy of this plugin:
<pre><b>mvn org.apache.maven.plugins:maven-dependency-plugin:2.8:get -DrepoUrl=http://www.vadosity.com:8081/nexus/ -Dartifact=ch.sbb.maven.plugins:iib-maven-plugin:9.0-SNAPSHOT -Ddest=iib-maven-plugin-9.0-SNAPSHOT.jar</b></pre>
</div>

<div class="section">
<h4><a name="InstallProxy"><b>4. Install the IBM ConfigManagerProxy.jar in your local repository (Do this once)</b></a></h4>
In the same command prompt window, install a copy of the IBM ConfigManagerProxy.jar in your local repository with the command: 
<pre><b>mvn install:install-file -DgroupId=com.ibm.etools.mft.config -DartifactId=ConfigManagerProxy -Dversion=9.0.300.v20150305-1357 -Dpackaging=jar -Dfile="C:\Program Files\IBM\MQSI\9.0.0.2\classes\ConfigManagerProxy.jar"</b></pre>
* Note: The maven-plugin uses an IBM java archive to perform IIB9 bar packaging operations. It is assumed that the platform running maven will also have IBM IIB9 components installed on the system - so one need only locate the path to the ConfigManagerProxy.jar.  
</div>


<div class="section">
<h4><a name="InstallPlugin"><b>5. Install this Plug-In in your local repository (Do this once)</b></a></h4>
In the same command prompt window, install the copy of this plugin in your local repository with the command: 
<pre><b>mvn install:install-file -Dfile=iib-maven-plugin-9.0-SNAPSHOT.jar -DgroupId=ch.sbb.maven.plugins -DartifactId=iib-maven-plugin -Dversion=9.0-SNAPSHOT -Dpackaging=jar</b></pre>

</div>
<div class="section">
<h4><a name="RunningHelp"><b>6. Generate this help file (Optional)</b></a></h4>
For a local copy of these instructions, run the plugin's 'morehelp' goal. This html file's contents, its associated images, and related files will be saved in the directory where the following command is run:
<pre><b>mvn ch.sbb.maven.plugins:iib-maven-plugin:9.0-SNAPSHOT:morehelp</b></pre>.
</div>
<div class="section">
<h4><a name="Mavenize"><b>7. Mavenize an IIB9 workspace (Do this just per IIB9 workspace)</b></a></h4>
With a command window, navigate to the root of an IIB9 workspace. Ideally the workspace will contain both IIB9 application and IIB9 library projects.  Before executing the following commands, a developer should verify that only the projects of interest in the workspace. Otherwise, extraneous projects will be zipped up and deployed on a maven distribution repository such as nexus.  To Mavenize a workspace, modify and execute this example command: 
<pre><b>mvn -Doverwrite=true -DgroupId=com.vadosity.esbteam -Dversion=1.0-SNAPSHOT  -Dmaven.multiModuleProjectDirectory=%M2_HOME% -Ddistribution.repository=http://www.vadosity.com:8081/nexus/content/repositories/snapshots/ ch.sbb.maven.plugins:iib-maven-plugin:9.0-SNAPSHOT:mavenize</b></pre>

Verify that your workspace has been 'mavenized' by searching for pom.xml files in your workspace. A search for pom.xml files in your IIB9 workspace should produce results similar to 'Figure 1' below.
<br>
<center><span style="align-content:center"><b>Figure 1</b></span></center>
<center><img src="maven9/pomfiles.png"></center>
</div>
<div class="section">
<h4><a name="UploadLibs"><b>8. Upload Common Libraries to a Maven Distribution Repository (Do this once per Common Library Version IF involved in developing common libraries)</b></a></h4>
With a command prompt situated at the root of the IIB9 workspace, execute:
<pre><b>mvn deploy</b></pre>
Verify that the build successfully completed.  Navigate to the distribution repository and check the appropriate repository for your IIB9 library artifacts.  For example, navigate to <a href="http://www.vadosity.com:8081/nexus/#view-repositories;snapshots~browsestorage">http://www.vadosity.com:8081/nexus/#view-repositories;snapshots~browsestorage</a> to view uploaded IIB9 library projects as seen in Figure 2.
<br>
<center><span style="align-content:center"><b>Figure 2</b></span></center>
<center><img src="maven9/nexus-libs.png"></center>
</div>
<div class="section">
<h4><a name="DeleteOtherProjects"><b>9. Delete Common Library Projects (Optionally Do this once)</b></a></h4>
Step 8 uploaded IIB9 libraries and other dependent project to a maven distribution repository.  To fully understand how the plugin works, delete the IIB9 dependendent libraries and projects in your workspace (if present).  For example, delete all IIB9 projects in an IIB9 workspace except the HDR project.  After deletion, your workspace should appear as seen in Figure 3.
<br>
<center><span style="align-content:center"><b>Figure 3</b></span></center>
<center><img src="maven9/deleted-projects.png"></center>
</div>

<div class="section">
<h4><a name="VerifyPom"><b>10. Verify that Pom.xml include common libraries (Do this just per IIB9 workspace)</b></a></h4>
<br>&nbsp;<br>
With a command prompt navigate from the iib9 project workspace directory into the IIB9 bar application project you are working on. For example, navigate to the HDR project. 
<pre><b>cd HDR</b></pre>
When your project includes common libraries, then common libraries need to be added as dependencies in your project pom.xml file.  The mavenize project step may have already added common library dependencies.  If it has not, then your common libraries must be added.  The common libraries are added as normal maven dependencies are - and absolutely must have the 'compile' scope defined.  For example, your bar application's pom.xml dependencies section would appear similar to the following:

<pre>
	&lt;dependencies&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
			&lt;artifactId&gt;HL7v251DFDLLibrary&lt;/artifactId&gt;
			&lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
			&lt;type&gt;zip&lt;/type&gt;
			&lt;scope&gt;compile&lt;/scope&gt;
		&lt;/dependency&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
			&lt;artifactId&gt;VIECommonLibrary&lt;/artifactId&gt;
			&lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
			&lt;type&gt;zip&lt;/type&gt;
			&lt;scope&gt;compile&lt;/scope&gt;
		&lt;/dependency&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;com.vadosity.esbteam&lt;/groupId&gt;
			&lt;artifactId&gt;EMICommon&lt;/artifactId&gt;
			&lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
			&lt;type&gt;zip&lt;/type&gt;
			&lt;scope&gt;compile&lt;/scope&gt;
		&lt;/dependency&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;junit&lt;/groupId&gt;
			&lt;artifactId&gt;junit&lt;/artifactId&gt;
			&lt;version&gt;4.11&lt;/version&gt;
			&lt;scope&gt;test&lt;/scope&gt;
		&lt;/dependency&gt; 
	&lt;/dependencies&gt;
</pre>

If your project's pom.xml file does not include any entries for common libraries, then you can look up your common libraries in your maven distribution repository.   The distribution repository will provide the dependency entries for any given common library available.  Add dependencies for common libraries in your pom.xml just as any other normal dependencies are added. Also note that the junit dependency is necessary to execute integration tests in later steps.  Save your pom.xml file when complete.  

</div>

<div class="section">
<h4><a name="RunGenerateResources"><b>11. Run Generate Resources (Perform this and the following steps for each Project Build cycle)</b></a></h4>
Execute the following maven command:
<pre><b>mvn generate-resources</b></pre>
Open up Windows Explorer or a similar program to view the IIB9 workspace.   Note that the directory contains not only your project, but also the dependent projects.   For example, Figure 4 shows the HDR project as well as its dependent library projects.  Note that Maven has downloaded the dependent source libraries into the workspace.  The dependent library projects need not be checked out directly from source control. Thus, the downloaded IIB9 libraries are safe from accidental alteration. Also note that this dependent project download functionality can be turned off by setting the plug-in's 'unpackIibDependenciesIntoWorkspace' configuration parameter to false. You decide.  
<pre><b>&lt;unpackIibDependenciesIntoWorkspace&gt;false&lt;/unpackIibDependenciesIntoWorkspace&gt;</b></pre>
<br>
<center><span style="align-content:center"><b>Figure 4</b></span></center>
<center><img src="maven9/downloaded-projects.png"></center>
</div>

<div class="section">
<h4><a name="RunCompile"><b>12. Run Compile </b></a></h4>
To create a bar file, run the command:
<pre><b>mvn compile</b></pre>
Verify that a bar file has been successfully generated by checking the "target" directory in your IIB9 application project's workspace.  For example, Figure 5 shows the generated bar file in the HDR project's target directory. 
<center><span style="align-content:center"><b>Figure 5</b></span></center>
<center><img src="maven9/bar.png"></center>
</div>

<div class="section">
<h4><a name="RunTestCompile"><b>13. Run Test Compile</b></a></h4>
To compile unit and integration tests, run the command:
<pre><b>mvn test-compile</b></pre>
Verify that test classes have been compiled by viewing the "target/test-classes" directory in your IIB9 application project's workspace. 
</div>

<div class="section">
<h4><a name="RunProcessClasses"><b>14. Run Process Classes</b></a></h4>
To generate a default.properties file containing all possible bar override values and to override the bar file, run the command:
<pre><b>mvn process-classes</b></pre>
This step creates a <tt><b>default.properties</b></tt> file in your projects's target directory.  It also creates a <tt><b>defined-default.properties</b></tt> file in the same directory. The default.properties file lists all possible properties that can be overridden in a bar.  The defined-default.properties file only lists those that have been set during the development process.  It is recommended to use defined-default.properties as a foundation for creating overridden properties files for deployment environments. 
<br>&nbsp;<br>
The plug-in then searches your project's src/main/resources directories for environments defined by naming convention. A single environment's files consist of a <tt>properties</tt> file, an optional <tt>broker</tt> file and a <tt>deploy-config</tt> file.  For example, the files INT1.broker, INT1.deploy-config, INT1.properties define the 'INT1' environment.  The broker file contains the <i>WHERE</i> information defining the 'INT1' broker node's location.  To create a broker file, see 
<a href="https://www-01.ibm.com/support/knowledgecenter/SSMKHH_9.0.0/com.ibm.etools.mft.doc/be10460_.htm">IBM Documentation</a>.
The deploy-config defines the mqsideploy configuration values. The deploy-config file is also in properties file format and contains the config parameters specified in 
<a href="https://www-01.ibm.com/support/knowledgecenter/SSMKHH_9.0.0/com.ibm.etools.mft.doc/an28520_.htm">mqsideploy documentation.</a> For example, the deploy-config file will define the integration server name to mqsideploy the bar file to for this environment.  Finally, the properties file for an environment defines the bar override values for the named environment. 
<br>&nbsp;<br>
Thus, the plug-in perform bar overrides for as many environments as are specified in the src/main/resources directory.  
Figure 6 shows the overridden bar files in the C5_HDR_VistaInterface project's target/iib-overrides directory. This Figure shows environments 'INT1' and 'INT2' and the resulting overridden bar files. Note that an environment classifier is added to each overridden bar file name.  
<center><span style="align-content:center"><b>Figure 6</b></span></center>
<center><img src="maven9/bar-overrides.png"></center>
</div>

<div class="section">
<h4><a name="RunPreIntegrationTest"><b>15. Run Pre-Integration-Test</b></a></h4>
Running the pre-integration-test maven command deploy overridden bar file to each defined environment.  Before executing this command, it 
makes sense to verify your IIB9 Maven plug-in's configuration.  For example, the following configuration is used to deploy a bar file to a local developer's environment.
<b><pre>&lt;completeDeployment&gt;true&lt;/completeDeployment&gt;<br>&lt;timeoutSecs&gt;600&lt;/timeoutSecs&gt;</pre></b>


During deployments, timeout failures are common.  To configure a Broker Node and execution server for extended timeouts, see: <a href="http://www-01.ibm.com/support/knowledgecenter/SSKM8N_8.0.0/com.ibm.etools.mft.doc/ae18065_.htm">http://www-01.ibm.com/support/knowledgecenter/SSKM8N_8.0.0/com.ibm.etools.mft.doc/ae18065_.htm</a>.
<br>
Once ready, run the mqsideploy with the following command:
<pre><b>mvn pre-integration-test</b></pre>
<br>
To skip directly to the mqsideploy step in the pre-integration-test phase without having to run through the full iib-bar lifecycle:
<pre><b>mvn -DskipTo=pre-integration-test pre-integration-test</b></pre>
<br>
* If a decision services error occurs at mqsi deployment, then the following command enables and disables Decision services.  
<pre><b>mqsimode <i>&lt;YourNode&gt;</i> -x DecisionServices</b></pre>
For example:
<pre><b>mqsimode IB9NODE -x DecisionServices</b></pre>

</div>


<div class="section">
<h4><a name="RunPreIntegrationTest"><b>16. Run Integration-Test</b></a></h4>
Run Integration Tests with the standard maven plugin with the command:
<pre>mvn integration-test</pre>
<br>
</div>

<div class="section">
<h4><a name="RunDeploy"><strong>17. Run Deploy</strong></a></h4>
The Maven deploy command is not the same as the mqsideploy command executed in the iib-bar lifecycle's pre-integration-test step.  The maven deploy just deploys the non-overridden bar to the Maven Distribution Repository.  To execute the full iib-bar lifecycle:
<pre><b>mvn deploy</b></pre>
<br>
To skip mqsideploy that occurrs in the pre-integration-test goal, execute:
<pre><b>mvn -DskipTo=integration-test deploy</b></pre>
<br>
Verify the 'maven deploy' success by finding the iib-bar artifact in the maven distribution repository.  
For example, Figure 7 shows a deployed IIB9 bar file in a Releases repository.

<center><span style="align-content:center"><b>Figure 7</b></span></center>
<center><img src="maven9/deployed-bar.png"></center>
</div>
</div>

    <footer>
            <div class="container-fluid">
                      <div class="row-fluid">
                                      <p >Copyright &copy;                    2002&#x2013;2015
                        <a href="http://www.apache.org/">The Apache Software Foundation</a>.  Contact Brett Shelley at <a mailto:bshelley585@gmail.com>bshelley585@gmail.com</a> for further assistance with this plugin.  
            All rights reserved.      
                    
      </p>
                </div>

        
                </div>
    </footer>



