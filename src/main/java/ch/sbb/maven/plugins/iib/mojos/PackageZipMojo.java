package ch.sbb.maven.plugins.iib.mojos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


/**
 * creates a zipped-up IIB9 project.
 */

@Mojo(name = "package-zip", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageZipMojo extends AbstractMojo {


    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    @Parameter(property = "zipFilePath", required = true, defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.zip")
    protected String zipFilePath;


    private File projectParentDirectory;


    public void execute() throws MojoFailureException, MojoExecutionException
    {
        try
        {
            String artifactId = project.getArtifactId();
            String baseDir = project.getBasedir().getName();

            if (!artifactId.equals(baseDir))
            {
                getLog().error("The project's directory name must exactly match the artifactId");
                getLog().error("Your project's directory is '" + baseDir + "' while the maven artifact id is '" + artifactId + "'");
                getLog().error("Please correct the problem");

                throw new MojoFailureException("The project's directory name (" + baseDir + ") must exactly match the artifactId (" + artifactId + ")");

            }

            // / create the target directory if necessary
            File buildDirectory = new File(project.getBuild().getDirectory());
            buildDirectory.mkdirs();
            FileUtils.cleanDirectory(buildDirectory);


            projectParentDirectory = project.getBasedir().getParentFile();

            List<File> fileList = new ArrayList<File>();
            getLog().info("Getting references to project artifacts in " + project.getBasedir().getCanonicalPath());

            fileList.add(project.getBasedir());
            getAllFiles(project.getBasedir(), fileList);


            writeZipFile(new File(zipFilePath), fileList);

            getLog().info("project zipped up to " + zipFilePath);

            getLog().info("DONE");
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new MojoFailureException(e.toString());
        }


    }


    public void getAllFiles(File dir, List<File> fileList) {
        try {
            File[] files = dir.listFiles();
            for (File file : files)
            {
                // / ignore the project build directory
                File buildDirectory = new File(project.getBuild().getDirectory());
                if (file.isDirectory() && file.getName().equals(buildDirectory.getName()))
                {
                    continue;
                }

                fileList.add(file);
                if (file.isDirectory()) {
                    getLog().info("directory:" + file.getCanonicalPath());
                    getAllFiles(file, fileList);
                } else {
                    getLog().info("  file:" + file.getCanonicalPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writeZipFile(File zipFile, List<File> fileList) {

        try {


            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (File file : fileList) {
                if (!file.isDirectory()) { // we add only files, not directories
                    addToZip(file, zos);
                }
            }

            zos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addToZip(File file, ZipOutputStream zos) throws FileNotFoundException,
            IOException {

        FileInputStream fis = new FileInputStream(file);

        int startIndex = projectParentDirectory == null ? 0 : projectParentDirectory.getCanonicalPath().length() + 1;


        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        // directoryToZip.getCanonicalPath().length() + 1,

        String zipFilePath = file.getCanonicalPath().substring(startIndex,
                file.getCanonicalPath().length());
        getLog().info("Writing '" + zipFilePath + "' to zip file");
        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }

}
