package ch.sbb.maven.plugins.iib.mojos;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import ch.sbb.maven.plugins.iib.utils.PomXmlUtils;

/**
 * This mojo generates an index.html file, a powerpoint file, and a set of image files in the
 * directory from which this maven goal is launched. The objective is to make this plug-in self-documenting.
 * 
 */
@Mojo(name = "morehelp", requiresProject = false)
public class MoreHelpMojo extends AbstractMojo {


    /**
     * The Maven Session Object
     */
    @Parameter(property = "session", required = true, readonly = true)
    protected MavenSession session;


    public void execute() throws MojoExecutionException, MojoFailureException {

        String root = session.getExecutionRootDirectory();
        File workspace = new File(root);
        writeIndexHtml(workspace);
        writeImage(workspace, "abc-bar.png");
        writeImage(workspace, "bar.png");
        writeImage(workspace, "deleted-projects.png");
        writeImage(workspace, "downloaded-projects.png");
        writeImage(workspace, "nexus-before.png");
        writeImage(workspace, "nexus-libs.png");
        writeImage(workspace, "pomfiles.png");
        writeImage(workspace, "deployed-bar.png");
        writeImage(workspace, "nexus-before.png");
        writeImage(workspace, "nexus-before.png");
        writeImage(workspace, "MavenIIB9PlugIn.pptx");
        writeImage(workspace, "settings.xml");
    }


    private void writeIndexHtml(File workspace) throws MojoFailureException {
        File index = new File(workspace, "index.html");
        String content = null;
        try {
            content = PomXmlUtils.getTemplateText("instructions/index.html");
        } catch (IOException e) {
            getLog().error("Unable to load index.html from plugin");
            throw new MojoFailureException(e.toString());
        }

        try {
            FileOutputStream fos = new FileOutputStream(index);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();

            getLog().info("HELP FILE HAS BEEN WRITTEN TO:  " + index.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(index);
            }


        } catch (Exception e) {
            getLog().error("Unable to write index.html to " + index.getAbsolutePath());
            throw new MojoFailureException(e.toString());
        }
    }


    private void writeImage(File workspace, String imageFile) throws MojoFailureException {
        File image = new File(workspace, imageFile);
        InputStream is = null;
        OutputStream os = null;

        String resourcePath = "instructions/" + imageFile;
        try
        {
            is = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
            os = new FileOutputStream(image);
            int intRead = -1;
            while ((intRead = is.read()) != -1)
            {
                os.write(intRead);
            }
            os.flush();


        } catch (Exception e)
        {
            getLog().error(e.toString());
        } finally
        {
            try {
                is.close();
            } catch (Exception ignore) {
            }
            try {
                os.close();
            } catch (Exception ignore) {
            }

        }
    }


}
