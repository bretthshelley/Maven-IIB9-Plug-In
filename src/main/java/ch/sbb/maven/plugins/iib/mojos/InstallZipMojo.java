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
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

/**
 * Installs a zipped-up, IIB9 project into the user's local maven repository.
 * 
 *
 * @author Brett Shelley, 2016
 */
@Mojo(name = "install-zip", defaultPhase = LifecyclePhase.INSTALL)
public class InstallZipMojo extends AbstractMojo {

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


    @Parameter(property = "zipFilePath", required = true, defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.zip")
    protected String zipFilePath;


    public void execute() throws MojoFailureException, MojoExecutionException
    {


        try
        {
            File file = new File(zipFilePath);
            if (!file.exists())
            {
                String message = "The zip file '" + file.getAbsolutePath() + "' is missing";
                getLog().error(message);
                throw new MojoFailureException(message);
            }

            // / attempt to add the pom file
            Element pomElement = null; //
            String pomFilePath = project.getBasedir().getAbsolutePath() + File.separator + "pom.xml";
            File pomFile = new File(pomFilePath);
            if (!pomFile.exists())
            {
                getLog().warn("no pom file could be located at " + pomFilePath);
                pomElement = element("generatePom", "true");
            }
            else
            {
                pomElement = element("pomFile", pomFilePath);
            }

            // unpack all IIB dependencies that match the given scope (compile)
            Plugin plugin = plugin(groupId("org.apache.maven.plugins"), artifactId("maven-install-plugin"), version("2.5.2"));
            String goal = goal("install-file");
            Xpp3Dom xpp3Dom = configuration(
                    element(name("file"), zipFilePath), // / Output location.
                    element(name("repositoryLayout"), "default"), // / Comma Separated list of Types to include
                    element(name("artifactId"), project.getArtifactId()),
                    element(name("version"), project.getVersion()),
                    element(name("packaging"), "zip"),
                    element(name("groupId"), project.getGroupId()),
                    pomElement

                    );

            ExecutionEnvironment executionEnvironment = executionEnvironment(project, session, buildPluginManager);
            executeMojo(plugin, goal, xpp3Dom, executionEnvironment);

        } catch (Exception e)
        {
            throw new MojoFailureException(e.toString());
        }


    }

}
