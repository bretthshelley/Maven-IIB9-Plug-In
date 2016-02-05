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

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
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

import ch.sbb.maven.plugins.iib.utils.ConfigurationValidator;

/**
 * This Mojo deploys zipped up IIB9 projects to the designated maven distribution repository.
 *
 * 
 *
 * @author Brett (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2016
 */
@Mojo(name = "deploy-zip", defaultPhase = LifecyclePhase.INSTALL)
public class DeployZipMojo extends AbstractMojo {

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

            ConfigurationValidator.validateDeployBarMojo(project, getLog());

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


            ArtifactRepository repo = project.getDistributionManagementArtifactRepository();

            String artifactId = project.getArtifactId();
            String groupId = project.getGroupId();
            String version = project.getVersion();


            // copy the main resources
            Plugin deployFilePlugin = plugin(groupId("org.apache.maven.plugins"), artifactId("maven-deploy-plugin"), version("2.6"));
            String goal = goal("deploy-file");
            Xpp3Dom pluginConfiguration = configuration(
                    element(name("file"), zipFilePath),
                    element(name("repositoryId"), repo.getId()),
                    element(name("url"), repo.getUrl()),
                    element(name("artifactId"), artifactId),
                    element(name("groupId"), groupId),
                    element(name("version"), version),
                    pomElement
                    );
            ExecutionEnvironment executionEnvironment = executionEnvironment(project, session, buildPluginManager);

            executeMojo(deployFilePlugin, goal, pluginConfiguration, executionEnvironment);


            // / delete the zip file as it is a temporary resource
            file.delete();
            FileUtils.cleanDirectory(new File(project.getBuild().getDirectory()));
            new File(project.getBuild().getDirectory()).delete();

        } catch (Exception e)
        {
            throw new MojoFailureException(e.toString());
        }

    }

}
