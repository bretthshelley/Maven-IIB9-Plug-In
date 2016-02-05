package ch.sbb.maven.plugins.iib.mojos;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import ch.sbb.maven.plugins.iib.utils.ConfigurationValidator;
import ch.sbb.maven.plugins.iib.utils.DirectoriesUtil;
import ch.sbb.maven.plugins.iib.utils.SkipUtil;

/**
 * Initializes the Bar Build Workspace. The intent of this mojo is to augment the standard
 * <b>mvn clean</b> goal. Where mvn clean deletes the target directory and its contents,
 * this mojo performs additional project file deletion. It does this by deleting
 * the file(s) and/or ant-style file pattern(s) defined in the <b>initialDeletes</b> argument.
 * This mojo also checks to ensure that the workspace for this project has been defined.
 * 
 */
@Mojo(name = "initialize-bar-build-workspace", requiresDependencyResolution = ResolutionScope.TEST)
public class InitializeBarBuildWorkspaceMojo extends AbstractMojo {


    /**
     * a comma-separated list of files and/or ant-style file patterns to delete during the initialization phase.
     */
    @Parameter(property = "initialDeletes", required = false)
    protected String initialDeletes;

    /**
     * The path of the workspace in which the projects are extracted to be built.
     */
    @Parameter(property = "workspace", required = false)
    protected File workspace;

    /**
     * The Maven Project Object
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (new SkipUtil().isSkip(this.getClass())) {
            return;
        }

        ConfigurationValidator.validateWorkspace(workspace, getLog());

        // / verify that the workspace directory exists
        workspace.mkdirs();

        performInitialDeletes();


    }


    private void attemptToDelete(String expression)
    {


        try
        {
            boolean isExpression = expression.indexOf("*") != -1;
            if (isExpression)
            {
                @SuppressWarnings("unchecked")
                List<File> files = FileUtils.getFiles(project.getBasedir(), expression, null, false);
                for (File file : files)
                {
                    file.delete();
                }
                return;
            }


            // / try to delete absolute name
            boolean result = delete(expression);
            if (result == true) {
                return;
            }

            // / attempt to append path to project base directory
            File baseDir = project.getBasedir();
            String newPath = baseDir + File.separator + expression;
            // newPath = newPath.replaceAll(File.separator + File.separator, File.separator);
            result = delete(newPath);
            if (result == true) {
                return;
            }

            // / attempt to append path to workspace directory
            newPath = workspace.getAbsolutePath() + File.separator + expression;
            // newPath = newPath.replaceAll(File.separator + File.separator, File.separator);
            result = delete(newPath);
            if (result == true) {
                return;
            }

        } catch (Exception e)
        {
            getLog().info("unable to delete file " + expression + " for following reason: " + e);
        }

    }

    private boolean delete(String path)
    {
        File file = new File(path);
        if (file.exists())
        {
            try
            {
                if (file.isDirectory())
                {
                    FileUtils.deleteDirectory(file);
                    getLog().info("deleting directory at " + file.getAbsolutePath());
                    return true;
                }
                else
                {
                    boolean result = file.delete();
                    getLog().info("deleting file at " + file.getAbsolutePath());
                    return result;
                }
            } catch (Exception e)
            {
                getLog().info("unable to delete file " + file.getAbsolutePath() + " for following reason:" + e);
                return false;
            }
        }
        {
            getLog().info("unable to delete " + file.getAbsolutePath() + "; process as regular expression");
            return false;
        }
    }

    private void performInitialDeletes() throws MojoFailureException {

        if (initialDeletes == null || initialDeletes.trim().isEmpty()) {
            getLog().info("no initialDeletes have been defined.  No files will be deleted as part of initialization.");
            return;
        }

        String[] expressions = DirectoriesUtil.getFilesAndRegexes(initialDeletes);


        for (String expression : expressions)
        {
            attemptToDelete(expression);
        }


    }


}
