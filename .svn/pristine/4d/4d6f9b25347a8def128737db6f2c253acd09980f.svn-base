package ch.sbb.maven.plugins.iib.mojos;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import ch.sbb.maven.plugins.iib.utils.ApplyBarOverride;
import ch.sbb.maven.plugins.iib.utils.ConfigurableProperties;
import ch.sbb.maven.plugins.iib.utils.ConfigurationValidator;
import ch.sbb.maven.plugins.iib.utils.ReadBar;
import ch.sbb.maven.plugins.iib.utils.SkipUtil;

import com.ibm.broker.config.proxy.LogEntry;

/**
 * Validates override .properties files and (optionally) applies them to the default .bar file.
 */
@Mojo(name = "apply-bar-overrides", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class ApplyBarOverridesMojo extends AbstractMojo {


    /**
     * Whether the applybaroverride command should be executed or not
     */
    @Parameter(property = "applyBarOverrides", defaultValue = "true", required = true)
    protected Boolean applyBarOverrides;

    /**
     * The basename of the trace file to use when applybaroverriding bar files
     */
    @Parameter(property = "applyBarOverrideTraceFile", defaultValue = "${project.build.directory}/applybaroverridetrace.txt", required = true)
    protected File applyBarOverrideTraceFile;

    /**
     * The name of the BAR (compressed file format) archive file where the result is stored.
     * 
     */
    @Parameter(property = "barName", defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.bar", required = true)
    protected File barName;

    /**
     * The name of the default properties file to be generated from the bar file.
     * 
     */
    @Parameter(property = "defaultPropertiesFile", defaultValue = "${project.build.directory}/default.properties", required = true)
    protected File defaultPropertiesFile;

    /**
     * Whether or not to fail the build if properties are found to be invalid.
     */
    @Parameter(property = "failOnInvalidProperties", defaultValue = "true", required = true)
    protected Boolean failOnInvalidProperties;


    /**
     * The Maven Project Object
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    /**
     * The Maven Session Object
     */
    @Parameter(property = "session", required = true, readonly = true)
    protected MavenSession session;

    /**
     * The Maven PluginManager Object
     */
    @Component
    protected BuildPluginManager buildPluginManager;


    public void execute() throws MojoFailureException {
        if (new SkipUtil().isSkip(this.getClass())) {
            return;
        }

        copyAndFilterResources();

        getLog().info("Reading bar file: " + barName);

        // / gets the overridable properties from readbar
        ConfigurableProperties overridableProperties;
        try {
            overridableProperties = getOverridableProperties();
        } catch (IOException e) {
            throw new MojoFailureException("Error extracting configurable properties from bar file: " + barName.getAbsolutePath(), e);
        }
        // / writes the overridable properties to the default properties file
        writeToFile(overridableProperties, defaultPropertiesFile);

        // / create a defined-default.properties file
        writeDefinedDefaultProperties(overridableProperties);


        // / copy the default properties file to iib-overrides\<<artifactId>>.properties
        File defaultBarFilePropsDirectory = new File(project.getBuild().getDirectory(), "iib-overrides");
        defaultBarFilePropsDirectory.mkdirs();

        validatePropertiesFiles(overridableProperties);

        if (applyBarOverrides) {
            executeApplyBarOverrides();
        }
    }


    private void writeDefinedDefaultProperties(ConfigurableProperties overridableProperties) {

        File definedPropertiesFile = new File(defaultPropertiesFile.getParentFile(), "defined-default.properties");
        Properties definedProperties = new Properties();
        for (String key : overridableProperties.keySet())
        {
            String value = overridableProperties.get(key);
            boolean isDefined = value != null && !value.trim().isEmpty();
            if (isDefined)
            {
                definedProperties.setProperty(key, value);
            }
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(definedPropertiesFile);
            String message = "This contains only the defined values of the default.properties produced by mqsiread of bar file.";

            definedProperties.store(fos, message);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            getLog().warn("unable to write defined-default.properties file to " + definedPropertiesFile.getAbsolutePath());
            getLog().warn(e.toString());
        }
    }


    private void copyAndFilterResources() throws MojoFailureException {

        getLog().debug("Project Build Resources: " + project.getBuild().getResources().toString());

        try {
            validateResourceDirectories();


            // copy the main resources
            Plugin copyResourcesPlugin = plugin(groupId("org.apache.maven.plugins"), artifactId("maven-resources-plugin"), version("2.6"));
            String goal = goal("copy-resources");
            Xpp3Dom resourcesConfiguration = configuration(
                    element(name("outputDirectory"), "${project.build.directory}/iib-overrides"), // The output directory into which to copy the resources.
                    element(name("resources"), element(name("resource"), // The list of resources we want to transfer. See the Maven Model for a description of how to code the resources element.
                            // TODO hard-coding this isn't great form
                            // see also ValidateConfigurablePropertiesMojo.java
                            element(name("directory"), "src/main/resources"), // right now, this value is hardcoded to default value
                            element(name("filtering"), "true"))) // If false, don't use the filters specified in the build/filters section of the POM when processing resources in this mojo execution.

            );
            ExecutionEnvironment executionEnvironment = executionEnvironment(project, session, buildPluginManager);

            executeMojo(copyResourcesPlugin, goal, resourcesConfiguration, executionEnvironment);


        } catch (MojoExecutionException e) {
            throw new MojoFailureException("Error while copying and filtering resources", e);
        }
    }

    /**
     * 
     */
    private void validateResourceDirectories()
    {
        String warning = null;
        String resourceDirectoryPath = project.getBasedir().getAbsolutePath();
        if (!resourceDirectoryPath.endsWith(File.separator))
        {
            resourceDirectoryPath += File.separator;
        }
        resourceDirectoryPath += "src" + File.separator + "main" + File.separator + "resources";
        File resourceDirectory = new File(resourceDirectoryPath);
        if (!resourceDirectory.exists() || !resourceDirectory.isDirectory())
        {
            warning = "No resource directory found at " + resourceDirectoryPath;
            getLog().warn(warning);
        }
        else
        {
            File[] resourceFiles = resourceDirectory.listFiles();
            if (resourceFiles == null || resourceFiles.length == 0)
            {
                warning = "No resources found in resource directory at " + resourceDirectoryPath;
                getLog().warn(warning);
            }
        }

        if (warning != null)
        {
            String projectBaseDir = project.getBasedir().getAbsolutePath();
            ConfigurationValidator.warnOnApplyBarOverrides(projectBaseDir, resourceDirectory, getLog(), warning);
        }

    }


    private void executeApplyBarOverrides() throws MojoFailureException {


        for (File propFile : getTargetPropertiesFiles())
        {
            // / if ENVIRONMENT1.properties in the src\main\resources\ directory is a prop file,
            // / then ENVIRONMENT1.properties gets written to /target/iib-overrides/ENVIRONMENT1.properties
            // / the resulting target bar file name should be


            // / then the targetBarFilename should be target\iib\xyz.bar
            // / then the targetBarFile gets populated with the combination of the output bar and the propFile
            String baseFilename = project.getArtifactId() + "-" + project.getVersion() + "-" + FilenameUtils.getBaseName(propFile.getName()); // / HDR-6.0-ENVIRONMENT1
            String barFileName = baseFilename + ".bar"; // / HDR-6.0-ENVIRONMENT1.bar
            String brokerFileName = FilenameUtils.getBaseName(propFile.getName()) + ".broker"; // ENVIRONMENT1.properties
            String serverFileName = FilenameUtils.getBaseName(propFile.getName()) + ".config"; // ENVIRONMENT1.config


            String targetBarFilename = (new File(propFile.getParent(), barFileName)).getAbsolutePath(); // ...target/iib-overrides/HDR-6.0-ENVIRONMENT1.bar
            String targetBrokerFilename = (new File(propFile.getParent(), brokerFileName)).getAbsolutePath(); // ...target/iib-overrides/ENVIRONMENT1.broker
            String targetConfigFilename = (new File(propFile.getParent(), serverFileName)).getAbsolutePath(); // ...target/iib-overrides/ENVIRONMENT1.config

            checkTargetBrokerFilePresent(targetBarFilename, targetBrokerFilename);

            checkTargetConfigFilePresent(targetConfigFilename);


            try {
                getLog().info("applybaroverrides being executed against " + barName + " with properties " + propFile);
                Enumeration<LogEntry> logEntries = ApplyBarOverride.applyBarOverride(barName.getAbsolutePath(), propFile.getAbsolutePath(), targetBarFilename);
                getLog().info("applybaroverrides successfully created " + targetBarFilename);

                writeTraceFile(logEntries, propFile);


            } catch (IOException e) {
                // TODO handle exception
                throw new MojoFailureException("Error applying properties file " + propFile.getAbsolutePath(), e);
            }
        }
    }


    private void checkTargetConfigFilePresent(String targetConfigFilename) {
        // / check for the presence of the targetConfigFilename and warn if it not present or not populated
        File targetConfigFile = new File(targetConfigFilename);
        if (!targetConfigFile.exists())
        {
            String message = "No server file found with name " + targetConfigFile.getName();
            getLog().warn(message);
        }
    }


    private void checkTargetBrokerFilePresent(String targetBarFilename, String targetBrokerFilename) {
        // / check for the presence of the targetBrokerFilename and warn if it not present
        File targetBrokerFile = new File(targetBrokerFilename);
        if (!targetBrokerFile.exists())
        {
            String message = "No broker file found with name " + targetBrokerFile.getName() + "; The bar file at '" + targetBarFilename + "' will not be deployed";
            getLog().warn(message);
            message = "For instructions on creating a broker file - See https://www-01.ibm.com/support/knowledgecenter/SSMKHH_9.0.0/com.ibm.etools.mft.doc/be10460_.htm";
            getLog().warn(message);
        }
    }

    /**
     * @param logEntries
     * @param propFile
     */
    private void writeTraceFile(Enumeration<LogEntry> logEntries, File propFile)
    {
        String traceFilePath = getTraceFileParameter(propFile);
        if (logEntries == null)
        {
            getLog().info("logEntries are null; no entries to write to " + traceFilePath);
            new File(traceFilePath).delete();
            return;
        }
        if (!logEntries.hasMoreElements())
        {
            getLog().info("no logEntries to write to " + traceFilePath);
            new File(traceFilePath).delete();
            return;
        }


        try {

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(traceFilePath)));

            while (logEntries.hasMoreElements())
            {
                LogEntry logEntry = logEntries.nextElement();
                writer.write(logEntry.toString());
            }
            writer.flush();
            writer.close();


        } catch (IOException e)
        {
            // TODO handle exception
            throw new RuntimeException(e);
        }


    }


    /**
     * @param propFile the name of the apply bar override property file
     * @return the value to be passed to the (-v) Trace parameter on the command line
     */
    protected String getTraceFileParameter(File propFile) {
        String filename = FilenameUtils.getBaseName(applyBarOverrideTraceFile.getAbsolutePath()) + "-" + FilenameUtils.getBaseName(propFile.getName()) + ".txt";
        String directory = propFile.getParent();
        return new File(directory, filename).getAbsolutePath();
    }

    /**
     * This finds all of the properties files from the resource directories (except for the default.properties).
     * It checks to see if the resource and test resource property files don't have properties that
     * are not discoverable in the bar file using readbar command.
     * 
     * 
     * @param overrideableProperties
     * @throws MojoFailureException
     */
    private void validatePropertiesFiles(ConfigurableProperties overrideableProperties) throws MojoFailureException {

        boolean invalidPropertiesFound = false;

        List<File> propFiles = null;
        propFiles = getTargetPropertiesFiles();
        getLog().info("Validating properties files");
        for (File file : propFiles) {
            getLog().info("  " + file.getAbsolutePath());
            try {
                ConfigurableProperties definedProps = new ConfigurableProperties();
                definedProps.load(defaultPropertiesFile);

                // check if all the defined properties are valid
                if (!overrideableProperties.keySet().containsAll(definedProps.keySet())) {

                    getLog().error("Invalid properties found in " + file.getAbsolutePath());
                    invalidPropertiesFound = true;

                    // list the invalid properties in this file
                    for (Object definedProp : definedProps.keySet()) {
                        if (!overrideableProperties.containsKey(ConfigurableProperties.getPropName((String) definedProp))) {
                            getLog().error("  " + definedProp);
                        }
                    }
                }

            } catch (IOException e) {
                throw new MojoFailureException("Error loading properties file: " + file.getAbsolutePath(), e);
            }
        }

        if (failOnInvalidProperties && invalidPropertiesFound) {
            throw new MojoFailureException("Invalid properties were found");
        }
    }

    private void writeToFile(ConfigurableProperties configurableProperties, File file) throws MojoFailureException {

        getLog().info("Writing overridable properties to: " + defaultPropertiesFile.getAbsolutePath());

        try {
            configurableProperties.save(defaultPropertiesFile);
        } catch (IOException e) {
            throw new MojoFailureException("Error writing properties file: " + file.getAbsolutePath(), e);
        }


    }

    /**
     * @return a sorted list of properties that can be overridden for a given bar file
     * @throws IOException
     */
    protected ConfigurableProperties getOverridableProperties() throws IOException {

        return ReadBar.getOverridableProperties(barName.getAbsolutePath());
    }

    /**
     * gets a list of all property files in the iib and iib-test directories. A copy of the default
     * properties file will be copied into the iib-overrides directory if both iib directories are empty.
     * 
     * @return
     * @throws MojoFailureException
     */
    @SuppressWarnings("unchecked")
    private List<File> getTargetPropertiesFiles() throws MojoFailureException {
        List<File> propFiles = null;

        File targetIibDirectory = new File(project.getBuild().getDirectory(), "iib-overrides");

        try {
            // see also PrepareIibBarPackagingMojo.java
            // String excludedDefaultPropertyFileName = defaultPropertiesFile.getName();

            propFiles = FileUtils.getFiles(targetIibDirectory, "*.properties", null);// excludedDefaultPropertyFileName);


        } catch (IOException e) {
            // TODO handle exception
            throw new MojoFailureException("Error searching for properties files under " + targetIibDirectory, e);
        }

        return propFiles;
    }
}
