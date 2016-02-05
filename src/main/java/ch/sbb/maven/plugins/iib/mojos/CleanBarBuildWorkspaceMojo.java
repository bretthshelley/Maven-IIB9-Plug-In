package ch.sbb.maven.plugins.iib.mojos;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import ch.sbb.maven.plugins.iib.utils.SkipUtil;

/**
 * Cleans up the ${iib.workspace} directory. Build errors will appear in the IIB Toolkit if .msgflow files are left under the ${iib.workspace} - the path determines the Namespace of the flow and that
 * certainly won't match the original directory structure.
 */
@Mojo(name = "clean-bar-build-workspace", requiresProject = false)
public class CleanBarBuildWorkspaceMojo extends AbstractMojo {

    /**
     * The path of the workspace in which the projects were created.
     */
    @Parameter(property = "workspace", defaultValue = "${project.build.directory}/iib/workspace", required = true)
    protected File workspace;

    /**
     * set to true to disable the workspace cleaning
     */
    @Parameter(property = "wipeoutWorkspace", defaultValue = "false")
    protected boolean wipeoutWorkspace;

    public void execute() throws MojoFailureException {
        if (new SkipUtil().isSkip(this.getClass())) {
            return;
        }

        if (wipeoutWorkspace)
        {
            getLog().info("Deleting the workspace directory: " + workspace);
            if (workspace.exists()) {
                try {
                    FileUtils.deleteDirectory(workspace);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        else
        {
            getLog().info("'wipeoutWorkspace' configuration property set to false - workspace will not be deleted");
        }
    }

}
