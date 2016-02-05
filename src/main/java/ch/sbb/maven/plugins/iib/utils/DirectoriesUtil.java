package ch.sbb.maven.plugins.iib.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;

/**
 *
 * 
 *
 * @author Brett Shelley
 * @version $Id: $
 * @since pom_version, 2015
 */
public class DirectoriesUtil {

    public static final String REGEX = "\\s*,[,\\s]*";

    private List<File> tempPomFiles = new ArrayList<File>();

    public void renamePomXmlFiles(File workspace, Log log) throws IOException
    {
        // / remove .pom from directories
        File[] projectDirectories = workspace.listFiles();
        for (File projectDirectory : projectDirectories)
        {
            if (!projectDirectory.isDirectory()) {
                continue;
            }
            if (projectDirectory.getName().startsWith("."))
            {
                continue;
            }

            String pomPath = projectDirectory.getAbsolutePath() + File.separator + "pom.xml";
            File pomFile = new File(pomPath);
            log.info("seeking file " + pomFile + " to temporarily rename");
            if (!pomFile.exists()) {
                continue;
            }

            String tempPomPath = projectDirectory.getAbsolutePath() + File.separator + "pom-xml-temp.txt";

            File tempPomFile = new File(tempPomPath);
            log.info("-->copying file " + pomFile + " to " + tempPomPath);

            FileUtils.copyFile(pomFile, tempPomFile, true);
            tempPomFiles.add(tempPomFile);
            log.info("-->deleting file: " + pomFile.getAbsolutePath());
            try
            {
                FileUtils.forceDelete(pomFile);
            } catch (IOException e)
            {
                String message = "This plugin attempts to remove pom.xml files from the workspace.\n";
                message += "The pom.xml files are the temporarily stored in the user.home directory at " + System.getProperty("user.home") + "\n";
                message += "It failed to delete the file " + pomFile.getAbsolutePath() + "\n";
                message += "Please check to ensure that no process is blocking the file's (" + pomFile.getAbsolutePath() + ") deletion.\n";
                message += "Please check to ensure that no process is blocking the file's deletion.\n";
                message += "The presence of the pom.xml file(s) will cause the mqsicreatebar command to fail.\n";
                message += "Error: " + e.toString();
                log.warn(message);
                throw e;
            }

        }


    }


    public static String[] getFilesAndRegexes(String commaSeparatedDirectories)
    {
        if (commaSeparatedDirectories == null || commaSeparatedDirectories.trim().isEmpty()) {
            return new String[0];
        }

        return commaSeparatedDirectories.split(REGEX);

    }


    public void restorePomFiles(File workspace, Log log) throws IOException
    {

        for (File tempPomFile : tempPomFiles)
        {
            String pomFilePath = tempPomFile.getAbsolutePath();
            pomFilePath = pomFilePath.substring(0, pomFilePath.lastIndexOf(File.separator));
            pomFilePath += File.separator + "pom.xml";

            File origFile = new File(pomFilePath);
            FileUtils.copyFile(tempPomFile, origFile);
            FileUtils.forceDelete(tempPomFile);
        }

    }
}
