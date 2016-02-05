/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */
package ch.sbb.maven.plugins.iib.utils;

import java.io.File;
import java.util.Collection;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

/**
 * @See http://www.mqseries.net/phpBB2/viewtopic.php?p=373078&sid=61e37afd78d0a6e548abd37975b2e03e
 * 
 *
 * @author Brett (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2016
 */
public class EclipseProjectFixUtil {

    /**
     * 
     * 
     * @param projectDirectory
     * @param dependentLibs
     * @param log
     * @throws Exception
     */
    public void fixIndirectLibraryReferences(File projectDirectory, Collection<String> dependentLibs, Log log) throws Exception
    {
        File projectFile = new File(projectDirectory, ".project");
        if (!projectFile.exists()) {
            throw new Exception("unable to locate .project file at " + projectFile.getAbsolutePath());
        }
        String content = FileUtils.fileRead(projectFile);
        String projectsStartTag = "<projects>";
        String projectsEndTag = "</projects>";
        String projectStartTag = "<project>";
        String projectEndTag = "</project>";

        // / just want to append the file at the right point
        int insertionPoint = content.indexOf(projectsEndTag);
        if (insertionPoint == -1)
        {
            log.info("unable to find " + projectsEndTag + " in .project file");
        }
        String firstPart = content.substring(0, insertionPoint);

        String lastPart = content.substring(insertionPoint);

        String projectsSection = content.substring(content.indexOf(projectsStartTag), insertionPoint);
        boolean firstPartAppended = false;
        for (String dependentLib : dependentLibs)
        {
            String libTagNeeded = projectStartTag + dependentLib + projectEndTag;
            if (!projectsSection.contains(libTagNeeded))
            {
                firstPartAppended = true;
                firstPart += "\t" + libTagNeeded;
                log.info("adding direct project reference " + libTagNeeded + " to " + projectFile.getAbsolutePath());
            }

        }
        if (firstPartAppended)
        {
            firstPart += "\n\t";
        }
        else
        {
            log.info("no dependent libraries fix needed for " + projectFile.getAbsolutePath());
        }
        content = firstPart + lastPart;
        FileUtils.fileWrite(projectFile, content);


    }

}
