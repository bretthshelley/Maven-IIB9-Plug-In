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
import java.io.FileInputStream;
import java.util.Properties;

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
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import ch.sbb.maven.plugins.iib.utils.ConfigurationValidator;


/**
 * Deploys the specified bar file to the project's maven distribution repository.
 * Note that this deployment to maven repository has nothing to do with deploying
 * a bar file to a broker.
 */
@Mojo(name = "deploy-bar", defaultPhase = LifecyclePhase.DEPLOY)
public class DeployBarMojo extends AbstractMojo {


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

        ConfigurationValidator.validateDeployBarMojo(project, getLog());
        ArtifactRepository repo = project.getDistributionManagementArtifactRepository();

        try {

            String baseBarFilePath = project.getBuild().getDirectory();
            if (!baseBarFilePath.endsWith(File.separator))
            {
                baseBarFilePath += File.separator;
            }
            baseBarFilePath += project.getArtifactId() + "-" + project.getVersion() + ".bar";

            File file = new File(baseBarFilePath);
            String artifactId = project.getArtifactId();
            String groupId = project.getGroupId();
            String version = project.getVersion();


            // copy the main resources
            Plugin deployFilePlugin = plugin(groupId("org.apache.maven.plugins"), artifactId("maven-deploy-plugin"), version("2.6"));
            String goal = goal("deploy-file");
            Xpp3Dom pluginConfiguration = configuration(
                    element(name("file"), file.getAbsolutePath()),
                    element(name("repositoryId"), repo.getId()),
                    element(name("url"), repo.getUrl()),
                    element(name("artifactId"), artifactId),
                    element(name("groupId"), groupId),
                    element(name("version"), version)
                    // element(name("classifier"), "DEV1")
                    );
            ExecutionEnvironment executionEnvironment = executionEnvironment(project, session, buildPluginManager);

            executeMojo(deployFilePlugin, goal, pluginConfiguration, executionEnvironment);

            try
            {
                String buildDir = project.getBuild().getDirectory();
                File buildDirectory = new File(buildDir);
                File overridesDir = new File(buildDirectory, "iib-overrides");
                File resultsFile = new File(overridesDir, "deployment.results");
                Properties resultsProperties = new Properties();
                FileInputStream fis = new FileInputStream(resultsFile);
                resultsProperties.load(fis);
                fis.close();

                for (Object key : resultsProperties.keySet())
                {
                    String environment = (String) key;
                    String deployedBarFilePath = resultsProperties.getProperty(environment);

                    pluginConfiguration = configuration(
                            element(name("file"), deployedBarFilePath),
                            element(name("repositoryId"), repo.getId()),
                            element(name("url"), repo.getUrl()),
                            element(name("artifactId"), artifactId),
                            element(name("groupId"), groupId + "." + environment),
                            element(name("version"), version),
                            element(name("classifier"), environment));

                    executeMojo(deployFilePlugin, goal, pluginConfiguration, executionEnvironment);

                }

            } catch (Exception e)
            {

            }


        } catch (MojoExecutionException e) {
            throw new MojoFailureException("Error while deploying resources", e);
        }
    }
}
