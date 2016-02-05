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

import java.io.File;
import java.io.IOException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import ch.sbb.maven.plugins.iib.utils.ConfigurationValidator;
import ch.sbb.maven.plugins.iib.utils.SkipUtil;

/**
 * Unpacks the dependent WebSphere Message Broker Projects.
 * 
 * Implemented with help from: https://github.com/TimMoore/mojo-executor/blob/master/README.md
 * 
 * requiresDependencyResolution below is required for the unpack-dependencies goal to work correctly. See https://github.com/TimMoore/mojo-executor/issues/3
 */
@Mojo(name = "prepare-bar-build-workspace", requiresDependencyResolution = ResolutionScope.TEST)
public class PrepareBarBuildWorkspaceMojo extends AbstractMojo {

    /**
     * The path of the workspace in which the projects are extracted to be built.
     */
    @Parameter(property = "workspace", required = false)
    protected File workspace;

    @Parameter(property = "unpackIibDependenciesIntoWorkspace", required = false)
    protected Boolean unpackIibDependenciesIntoWorkspace;

    @Parameter(property = "unpackIibDependencyTypes", defaultValue = "zip", required = false)
    protected String unpackIibDependencyTypes;

    @Parameter(property = "copyDependentJarsLocation", required = false, defaultValue = "${basedir}")
    protected String copyDependentJarsLocation;


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

    /**
     * The entry point to Aether, i.e. the component doing all the work.
     */
    @Component
    protected RepositorySystem repoSystem;

    /**
     * The current repository/network configuration of Maven.
     */
    @Parameter(property = "repositorySystemSession")
    protected RepositorySystemSession repoSession;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (new SkipUtil().isSkip(this.getClass())) {
            return;
        }

        validateConfiguration();

        // unpack the iib-src dependencies
        unpackIibCompileDependenciesToWorkspace();

        copyJarDependencies();


    }


    private void validateConfiguration() throws MojoFailureException
    {
        ConfigurationValidator.validateWorkspace(workspace, getLog());
        ConfigurationValidator.validateUnpackIibDependenciesIntoWorkspace(unpackIibDependenciesIntoWorkspace, getLog());
        ConfigurationValidator.validateUnpackIibDependencyTypes(unpackIibDependencyTypes, getLog());
        // ConfigurationValidator.validateUseClassloaders(useClassloaders, getLog());
        ConfigurationValidator.validateCopyDependentJarsLocation(copyDependentJarsLocation, getLog());
    }


    /**
     * 
     * 
     * @param includeTypes a string like 'zip,jar'
     * @return a string like 'zip'
     * 
     *         the iib dependencies will be unpacked into the workspace directory if the plugin is so configured.
     *         At this time, we don't want to unpack jar files into the same workspace directory. Rather, jar files
     *         will be unpacked into a directory within a specific project. For example, a 3rd party library might be unpacked
     *         into the "/workspace/HDR/target/classes" directory. Regardless, this method just makes sure that jar types
     *         don't get included in the 'includeTypes' string sent to the unpack-dependencies maven plugin.
     * 
     */
    public static String verifyIibIncludeTypes(String includeTypes)
    {
        if (includeTypes == null || includeTypes.trim().isEmpty())
        {
            includeTypes = "zip";
        }
        // / let's split up the String by comma's, and rebuild the expression without the jar

        final String regex = "\\s*,[,\\s]*";
        String[] types = includeTypes.split(regex);

        String result = null;
        for (String type : types)
        {
            if (type.trim().equalsIgnoreCase("jar"))
            {
                continue;
            }
            if (result == null)
            {
                result = type.trim();
            }
            else
            {
                result += "," + type.trim();
            }
        }
        return result;


    }


    /**
     * unpacks dependencies of a given scope to the workspace directory. This method executes teh maven dependency plugin's unpack-dependencies goal.
     * The goal 'unpack-dependencies' unpacks the project dependencies from the repository to a defined location and binds by default to the lifecycle phase: process-sources.
     * 
     * @throws MojoExecutionException
     */
    private void unpackIibCompileDependenciesToWorkspace() throws MojoExecutionException {

        workspace.mkdirs();

        if (!unpackIibDependenciesIntoWorkspace)
        {
            return;
        }


        String iibIncludeTypes = verifyIibIncludeTypes(unpackIibDependencyTypes);

        String outputDirectory = workspace.getAbsolutePath();

        // unpack all IIB dependencies that match the given scope (compile)
        Plugin plugin = plugin(groupId("org.apache.maven.plugins"), artifactId("maven-dependency-plugin"), version("2.8"));
        String goal = goal("unpack-dependencies");
        Xpp3Dom xpp3Dom = configuration(
                element(name("outputDirectory"), outputDirectory), // / Output location.
                element(name("includeTypes"), iibIncludeTypes), // / Comma Separated list of Types to include
                element(name("includeScope"), "compile"));

        ExecutionEnvironment executionEnvironment = executionEnvironment(project, session, buildPluginManager);
        executeMojo(plugin, goal, xpp3Dom, executionEnvironment);

        // delete the dependency-maven-plugin-markers directory
        try {
            FileUtils.deleteDirectory(new File(project.getBuild().getDirectory(), "dependency-maven-plugin-markers"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * unpacks jar dependencies of compile scope to the unpackJarsLocation directory
     *
     * @throws MojoExecutionException
     */
    private void copyJarDependencies() throws MojoExecutionException {

        new File(copyDependentJarsLocation).mkdirs(); // /ensure that the unpack directory exists

        // unpack all IIB dependencies that match the given scope (compile)
        Plugin plugin = plugin(groupId("org.apache.maven.plugins"), artifactId("maven-dependency-plugin"), version("2.8"));
        String goal = goal("copy-dependencies");
        Xpp3Dom xpp3Dom = configuration(
                element(name("outputDirectory"), copyDependentJarsLocation), // /Output location. Default value is: ${project.build.directory}/dependency. User property is: outputDirectory.
                element(name("includeTypes"), "jar"), // / Comma Separated list of Types to include. Empty String indicates include everything (default).
                element(name("includeScope"), "compile"));
        ExecutionEnvironment executionEnvironment = executionEnvironment(project, session, buildPluginManager);
        executeMojo(plugin, goal, xpp3Dom, executionEnvironment);
    }


}
