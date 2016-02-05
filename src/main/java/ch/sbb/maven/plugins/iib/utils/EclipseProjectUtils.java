package ch.sbb.maven.plugins.iib.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import ch.sbb.maven.plugins.iib.generated.eclipse_project.ProjectDescription;
import ch.sbb.maven.plugins.iib.generated.eclipse_project.ProjectDescription.Projects;

/**
 * @author u209936
 * 
 */
public class EclipseProjectUtils {

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
     * @param workspace
     * @return the names of the projects (actually, just all directories) in the
     *         workspace
     * @throws MojoFailureException
     */
    public static List<String> getWorkspaceProjects(File workspace) throws MojoFailureException {

        List<String> workspaceProjects = new ArrayList<String>();

        for (File file : workspace.listFiles()) {
            if (!file.isDirectory()) {
                continue;
            }
            if (file.getName().equals(".metadata")) {
                continue;
            }
            if (file.getName().startsWith("."))
            {
                continue;
            }
            File dotProjectFile = new File(file, ".project");
            if (dotProjectFile.exists())
            {
                workspaceProjects.add(file.getName());
            }

        }

        if (workspaceProjects.isEmpty()) {
            throw (new MojoFailureException(
                    "No projects were found in the workspace: "
                            + workspace.getAbsolutePath()));
        }

        return workspaceProjects;
    }


    /**
     * returns the name of the project out of the .project file
     * 
     * @param projectDirectory the (workspace) directory containing the project
     * @return the name of the project out of the .project file
     * @throws MojoFailureException if something goes wrong
     */
    public static String getProjectName(File projectDirectory) throws MojoFailureException {

        return getProjectDescription(projectDirectory).getName();
    }

    public static String[] getDependentProjectNames(File projectDirectory) throws MojoFailureException
    {
        Projects projects = getProjectDescription(projectDirectory).getProjects();
        List<String> projectNames = projects.getProject();
        if (projectNames == null || projectNames.isEmpty()) {
            return new String[0];
        }
        return projectNames.toArray(new String[projectNames.size()]);
    }


    /**
     * @param projectDirectory the (workspace) directory containing the project
     * @param log logger to be used if debugging information should be produced
     * @return true if the project is an IIB Application
     * @throws MojoFailureException if something went wrong
     */
    public static boolean isApplication(File projectDirectory, Log log) throws MojoFailureException {
        try
        {
            if (projectDirectory.getName().equalsIgnoreCase("BARFiles")) {
                return false;
            }


            List<String> natureList = getProjectDescription(projectDirectory).getNatures().getNature();
            if (natureList
                    .contains("com.ibm.etools.msgbroker.tooling.applicationNature")) {
                log.debug(
                        projectDirectory + " is an IIB Application");
                return true;
            } else {
                return false;
            }
        } catch (Exception e)
        {
            String message = "An error occurred trying to determine the nature of the eclipse project at " + projectDirectory.getAbsolutePath() + ".";
            message += "\n" + "The error was: " + e;
            message += "\n" + "Instead of allowing the build to fail, the EclipseProjectUtils.isApplication() method is returning false";
            log.warn(message);
            return false;
        }
    }

    /**
     * @param projectDirectory the (workspace) directory containing the project
     * @param log logger to be used if debugging information should be produced
     * @return true if the project is an IIB Application
     * @throws MojoFailureException if something went wrong
     */
    public static boolean isLibrary(File projectDirectory, Log log) throws MojoFailureException {
        try
        {
            if (projectDirectory.getName().equalsIgnoreCase("BARFiles")) {
                return false;
            }

            List<String> natureList = getProjectDescription(projectDirectory).getNatures().getNature();
            if (natureList
                    .contains("com.ibm.etools.msgbroker.tooling.libraryNature")) {
                log.debug(projectDirectory + " is an IIB Library");
                return true;
            } else {
                return false;
            }
        } catch (Exception e)
        {
            String message = "An error occurred trying to determine the nature of the eclipse project at " + projectDirectory.getAbsolutePath() + ".";
            message += "\n" + "The error was: " + e;
            message += "\n" + "Instead of allowing the build to fail, the EclipseProjectUtils.isLibrary() method is returning false";
            log.warn(message);
            return false;
        }
    }

    public static boolean isJavaProject(File projectDirectory, Log log)
    {
        if (projectDirectory.getName().equalsIgnoreCase("BARFiles")) {
            return false;
        }

        return new JavaProjectUtil().isJavaProject(projectDirectory, log);
    }


    /**
     * returns a java object containing the contents of the .project file
     * 
     * @param projectFile the .project file to be unmarshalled
     * @return the unmarshalled .profile file
     * @throws JAXBException if something goes wrong
     */
    protected static ProjectDescription unmarshallEclipseProjectFile(File projectFile)
            throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ProjectDescription.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (ProjectDescription) unmarshaller.unmarshal(projectFile);

    }

}
