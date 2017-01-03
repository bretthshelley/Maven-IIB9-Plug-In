/*
 * Copyright (C) Syntegrity Solutions Pty Ltd, 2017.
 */
package com.syntegrity.iib;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import ch.sbb.maven.plugins.iib.generated.eclipse_project.ProjectDescription;
import ch.sbb.maven.plugins.iib.utils.EclipseProjectUtils;

/**
 *
 * 
 *
 * @author steve (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2017
 */
public class EclipseProjUtils extends EclipseProjectUtils {

    private static ProjectDescription getProjectDescription(File projectDirectory) throws MojoFailureException {
        ProjectDescription projectDescription = new ProjectDescription();
        try {
            // unmarshall the .project file, which is in the temp workspace
            // under a directory of the same name as the projectName
            projectDescription = unmarshallEclipseProjectFile(new File(
                    projectDirectory, ".project"));
        } catch (JAXBException e) {
            throw (new MojoFailureException(
                    "Error parsing .project file in: " + projectDirectory.getPath(), e));
        }
        return projectDescription;
    }

    /**
     * @param projectDirectory the (workspace) directory containing the project
     * @param log logger to be used if debugging information should be produced
     * @return ProjectType enum
     * @throws MojoFailureException if something went wrong
     */
    public static ProjectType getProjectType(File projectDirectory, Log log) throws MojoFailureException {
        ProjectType ret = null;
        try {
            if (projectDirectory.getName().equalsIgnoreCase("BARFiles")) {
                ret = ProjectType.BARFILES;
            } else {
                // Determine type by nature
                List<String> natureList = getProjectDescription(projectDirectory).getNatures().getNature();
                if (natureList.contains(NatureType.APPLICATION.getFullName())) {
                    log.debug(projectDirectory + " is an IIB Application");
                    ret = ProjectType.APPLICATION;
                } else if (natureList.contains(NatureType.SHAREDLIBRARY.getFullName())) {
                    log.debug(projectDirectory + " is an IIB Shared Library");
                    ret = ProjectType.SHAREDLIBRARY;
                } else if (natureList.contains(NatureType.LIBRARY.getFullName())) {
                    log.debug(projectDirectory + " is an IIB Library");
                    ret = ProjectType.LIBRARY;
                } else if (natureList.contains(NatureType.JAVA.getFullName())) {
                    log.debug(projectDirectory + " is a Java project");
                    ret = ProjectType.JAVA;
                }
            }
        } catch (Exception e) {
            String message = "An error occurred trying to determine the nature of the eclipse project at " + projectDirectory.getAbsolutePath() + ".";
            message += "\n" + "The error was: " + e;
            message += "\n" + "Instead of allowing the build to fail, the EclipseProjectUtils.isApplication() method is returning null";
            log.warn(message);
        }
        return ret;
    }
}
