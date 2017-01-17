package ch.sbb.maven.plugins.iib.mojos;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import ch.sbb.maven.plugins.iib.utils.EclipseProjectUtils;
import ch.sbb.maven.plugins.iib.utils.SkipUtil;

/**
 * Unpacks the dependent WebSphere Message Broker Projects.
 * 
 * Implemented with help from: https://github.com/TimMoore/mojo-executor/blob/master/README.md
 * 
 * requiresDependencyResolution below is required for the unpack-dependencies goal to work correctly. See https://github.com/TimMoore/mojo-executor/issues/3
 */
@Mojo(name = "validate-bar-build-workspace", requiresDependencyResolution = ResolutionScope.TEST)
public class ValidateBarBuildWorkspaceMojo extends AbstractMojo {

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
     * The path of the workspace in which the projects are extracted to be built.
     */
    @Parameter(property = "workspace", required = true)
    protected File workspace;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (new SkipUtil().isSkip(this.getClass())) {
            return;
        }

        // loop through the project directories
        File[] projects = workspace.listFiles();

        if (null == projects || 0 == projects.length) {
            getLog().info("no projects in the workspace to validate.");
        } else {

            for (File projectDirectory : projects) {

                if (!projectDirectory.isDirectory()) {
                    continue;
                }

                String projectDirectoryName = projectDirectory.getName();

                if (projectDirectoryName.startsWith(".")) {
                    getLog().info("ignoring the directory " + projectDirectoryName + " in the workspace.");
                    continue;
                }

                // / ignore any directory that doesn not have a .project file in it
                File dotProjectFile = new File(projectDirectory, ".project");
                if (!dotProjectFile.exists()) {
                    getLog().info("The project directory " + projectDirectory.getName() + " does not have a .project file associated with it. skipping verification...");
                    continue;
                }


                // checks that the directory name is the same as the name in the .project file
                getLog().info("verifying that the directory " + projectDirectory + " has a .project file with the " + projectDirectory + " name");


                String eclipseProjectName = EclipseProjectUtils.getProjectName(projectDirectory);
                if (!projectDirectoryName.equals(eclipseProjectName)) {
                    throw new MojoFailureException("The Project Directory Name ('" + projectDirectoryName + "') is not the same as the Project Name (in .project file) ('" + eclipseProjectName + "')");
                }
            }
        }
    }
}