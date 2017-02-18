package ch.sbb.maven.plugins.iib.mojos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.syntegrity.iib.EclipseProjUtils;
import com.syntegrity.iib.ProjectType;

import ch.sbb.maven.plugins.iib.utils.ConfigurationValidator;
import ch.sbb.maven.plugins.iib.utils.EclipseProjectUtils;
import ch.sbb.maven.plugins.iib.utils.PomXmlUtils;

/**
 * This Mojo looks into the directory from which maven is run and attempts to "mavenize" the
 * library projects embedded in the execution root directory - that is, the directory from which this
 * goal is being executed. Each iib library, shared library and application project is given a pom.xml file
 * and a parent pom.xml containing modules is created.
 * 
 * 
 */
@Mojo(name = "mavenize", requiresProject = false)
public class MavenizeMojo extends AbstractMojo {


    /**
     * The groupId for the common library pom.xml files.
     */
    @Parameter(property = "groupId", required = false, defaultValue = "org.yourorg.yourteam")
    protected String groupId;

    /**
     * The version for each common library's pom.xml file.
     */
    @Parameter(property = "version", required = false, defaultValue = "1.0-SNAPSHOT")
    protected String version;

    /**
     * The version for each common library's pom.xml file.
     */
    @Parameter(property = "distribution.repository", required = false, defaultValue = "http://www.vadosity.com:8081/nexus/content/repositories/snapshots/")
    protected String distributionRepository;

    /**
     * indicates whether existing pom.xml files should be overwritten
     */
    @Parameter(property = "overwrite", required = true, defaultValue = "true")
    protected Boolean overwrite = Boolean.FALSE;

    /**
     * mqsiprofile path
     */
    @Parameter(property = "mqsiprofile", required = false, defaultValue = "C:\\Program Files\\IBM\\IIB\\10.0.0.6\\server\\bin\\mqsiprofile.cmd")
    protected String mqsiprofile;
    /**
     * The path of the workspace in which the projects are to be sought.
     */
    @Parameter(property = "workspace", required = false)
    protected File workspace;

    /**
     * iibDependenciesLocal flag is used to define whether iib dependencies are local or added to the repo
     */
    @Parameter(property = "iibDependenciesLocal", required = false, defaultValue = "false")
    protected boolean iibDependenciesLocal;

    //
    // /**
    // * The Maven Project Object
    // */
    // @Parameter(property = "project", required = true, readonly = true)
    // protected MavenProject project;
    //
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

    private HashMap<ProjectType, List<String>> projects = new HashMap<ProjectType, List<String>>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        validateWorkspaceIfSet();
        validateGroupIdAndVersion();
        validateMQSIprofile();


        if (workspace == null) {
            String root = session.getExecutionRootDirectory();
            workspace = new File(root);
        }

        // / traverse workspace seeking iib library projects and application projects
        determineProjectDirectoryTypes();

        // / add a pom.xml to project if not already present
        if (!iibDependenciesLocal && null != projects.get(ProjectType.LIBRARY)) {
            for (String libraryProject : projects.get(ProjectType.LIBRARY)) {
                getLog().info("analyzing project dependencies for " + libraryProject);
                createAndWritePom(libraryProject, ProjectType.LIBRARY);
            }
        }

        if (!iibDependenciesLocal && null != projects.get(ProjectType.JAVA)) {
            for (String javaProject : projects.get(ProjectType.JAVA)) {
                getLog().info("analyzing project dependencies for " + javaProject);
                createAndWritePom(javaProject, ProjectType.JAVA);
            }
        }

        if (null != projects.get(ProjectType.SHAREDLIBRARY)) {
            for (String shlibProj : projects.get(ProjectType.SHAREDLIBRARY)) {
                getLog().info("analyzing project dependencies for " + shlibProj);
                try {
                    makeCommonMavenDirectories(shlibProj);
                } catch (IOException e) {
                    getLog().warn("unable to create common maven directories: " + e);
                }
                createAndWritePom(shlibProj, ProjectType.SHAREDLIBRARY);
            }
        }

        if (null != projects.get(ProjectType.APPLICATION)) {
            for (String applicationProject : projects.get(ProjectType.APPLICATION)) {
                getLog().info("analyzing project dependencies for " + applicationProject);
                try {
                    makeCommonMavenDirectories(applicationProject);
                } catch (IOException e) {
                    getLog().warn("unable to create common maven directories: " + e);
                }
                createAndWritePom(applicationProject, ProjectType.APPLICATION);
            }
        }

        createAndWriteParentPom();
    }

    /**
     * @param project
     * @param type
     * @throws MojoFailureException
     */
    private void createAndWritePom(String project, ProjectType type) throws MojoFailureException {
        File projectDirectory = new File(workspace, project);
        String[] dependentProjectNames = new String[0];
        if (!iibDependenciesLocal) {
            dependentProjectNames = determineDependentProjectsInWorkspace(projectDirectory);
        }
        try {
            String pomContent = null;
            switch (type) {
                case LIBRARY:
                case JAVA: {
                    pomContent = PomXmlUtils.getLibaryPomText(groupId,
                            project,
                            version,
                            distributionRepository,
                            dependentProjectNames);
                    break;
                }
                case SHAREDLIBRARY: {
                    pomContent = PomXmlUtils.getSharedLibraryPomText(workspace, groupId,
                            project,
                            version,
                            distributionRepository,
                            dependentProjectNames, mqsiprofile, iibDependenciesLocal);
                    break;
                }
                case APPLICATION: {
                    pomContent = PomXmlUtils.getApplicationPomText(workspace, groupId,
                            project,
                            version,
                            distributionRepository,
                            dependentProjectNames, mqsiprofile, iibDependenciesLocal);
                    break;
                }
            }
            if (null != pomContent) {
                writePomToFile(pomContent, projectDirectory);
            }
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    /**
     * @param applicationProject
     * @throws IOException
     */
    private void makeCommonMavenDirectories(String applicationProject) throws IOException {
        File baseDir = new File(workspace, applicationProject);
        baseDir.mkdirs();
        File srcDir = new File(baseDir, "src");
        srcDir.mkdirs();
        File mainDir = new File(srcDir, "main");
        mainDir.mkdirs();
        File mainResources = new File(mainDir, "resources");
        mainResources.mkdirs();
        // File mainJava = new File(mainDir,"java");
        // mainJava.mkdirs();
        File testDir = new File(srcDir, "test");
        testDir.mkdirs();
        File testResources = new File(testDir, "resources");
        testResources.mkdirs();
        File testJava = new File(testDir, "java");
        testJava.mkdirs();

        // / add localdev.broker and localdev.config files
        File localDevBroker = new File(mainResources, "localdev.broker");
        String localDevBrokerContent = PomXmlUtils.getLocalDevBrokerTempate();
        write(localDevBrokerContent, localDevBroker);

        File localDevConfig = new File(mainResources, "localdev.deploy-config");
        String localDevConfigContent = PomXmlUtils.getLocalDevConfigTemplate();
        write(localDevConfigContent, localDevConfig);

        File localDevProperties = new File(mainResources, "localdev.properties");
        String localDevPropertiesContent = PomXmlUtils.getLocalDevPropertiesTemplate();
        write(localDevPropertiesContent, localDevProperties);

    }

    /**
     * @throws MojoFailureException
     * 
     */
    private void createAndWriteParentPom() throws MojoFailureException {

        try {
            List<String> allProjs = new ArrayList<String>();
            List<ProjectType> types = new ArrayList<ProjectType>();
            if (!iibDependenciesLocal) {
                types.add(ProjectType.LIBRARY);
                types.add(ProjectType.JAVA);
            }
            types.add(ProjectType.SHAREDLIBRARY);
            types.add(ProjectType.APPLICATION);
            for (ProjectType typ : types) {
                if (null != projects.get(typ)) {
                    allProjs.addAll(projects.get(typ));
                }
            }
            String pomContent = PomXmlUtils.getParentPomText(groupId, version, distributionRepository, allProjs);
            // / write the pom.xml for the project
            File pomXml = new File(workspace, "pom.xml");
            if (pomXml.exists() && overwrite.equals(Boolean.FALSE)) {
                // / don't write the pom
                getLog().info("POM file found at " + pomXml.getAbsolutePath() + " - ignoring since overwrite set to false");
            } else {
                write(pomContent, pomXml);
            }
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    private void writePomToFile(String pomContent, File projectDirectory) throws FileNotFoundException, IOException {
        // / write the pom.xml for the project
        File pomXml = new File(projectDirectory, "pom.xml");
        write(pomContent, pomXml);
    }

    private void write(String pomContent, File pomXml) throws FileNotFoundException, IOException {
        if (pomXml.exists() && overwrite.equals(Boolean.FALSE)) {
            // / don't write the pom
            getLog().info("POM file found at " + pomXml.getAbsolutePath() + " - ignoring since overwrite set to false");
            return;
        }


        getLog().info("writing POM to " + pomXml.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            // / write the pom.xml
            fos = new FileOutputStream(pomXml);
            fos.write(pomContent.getBytes());
            fos.flush();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {

            }
        }
    }

    private String[] determineDependentProjectsInWorkspace(File projectDirectory) throws MojoFailureException {
        List<String> dependentProjectsInWS = new ArrayList<String>();
        String[] dependentProjectNames = EclipseProjectUtils.getDependentProjectNames(projectDirectory);
        for (String dependentProjectName : dependentProjectNames) {

            getLog().info(projectDirectory.getName() + " has dependency " + dependentProjectName + "; checking " + dependentProjectName);

            // / if the workspace has the dependent project name, then add to the present list
            // Unless it is a shared library
            File dir = new File(workspace, dependentProjectName);
            if (dir.exists() && dir.isDirectory()) {
                // if (!EclipseProjectUtils.isApplication(dir, getLog()))
                // {
                if (null == projects.get(ProjectType.SHAREDLIBRARY) || !projects.get(ProjectType.SHAREDLIBRARY).contains(dependentProjectName)) {
                    // / make the assumption that the groupId, version, and other attributes are identical
                    dependentProjectsInWS.add(dependentProjectName);
                    // }
                }

            }
        }
        return dependentProjectsInWS.toArray(new String[dependentProjectsInWS.size()]);
    }

    private void determineProjectDirectoryTypes() throws MojoFailureException {
        List<String> projectDirectories = EclipseProjectUtils.getWorkspaceProjects(workspace);
        for (String projectDirectory : projectDirectories) {
            getLog().debug("mavenize Processing: " + projectDirectory);
            File projectDir = new File(workspace, projectDirectory);
            ProjectType pt = EclipseProjUtils.getProjectType(projectDir, getLog());
            if (null == projects.get(pt)) {
                List<String> projlist = new ArrayList<String>();
                projlist.add(projectDirectory);
                projects.put(pt, projlist);
            } else {
                List<String> projlist = projects.get(pt);
                projlist.add(projectDirectory);
            }
        }
        String message = "Projects in workspace: " + projects.toString();
        // String message = projects.get(ProjectType.LIBRARY).size() + " library projects found in workspace; ";
        // message += projects.get(ProjectType.SHAREDLIBRARY).size() + " shared librray projects found in workspace; ";
        // message += projects.get(ProjectType.APPLICATION).size() + " application projects found in workspace";
        getLog().info(message);
    }

    private void validateGroupIdAndVersion() throws MojoFailureException {
        if (groupId == null || groupId.trim().isEmpty()) {
            logMavenizeInstructions();
            String message = "The 'groupId' configuration parameter has not been defined. The groupId is needed to mavenize projects.";
            throw new MojoFailureException(message);
        }
        if (version == null || version.trim().isEmpty()) {
            logMavenizeInstructions();
            String message = "The 'version' configuration parameter has not been defined. The version is needed to mavenize projects.";
            throw new MojoFailureException(message);
        }
    }

    private void validateWorkspaceIfSet() throws MojoFailureException {
        if (workspace != null) {
            if (!workspace.isDirectory()) {
                String message = "The defined workspace, '" + workspace.getAbsolutePath() + "', needs to be a directory.";
                logMavenizeInstructions();
                throw new MojoFailureException(message);
            } else {
                File[] list = workspace.listFiles();
                if (list == null || list.length == 0) {
                    logMavenizeInstructions();
                    String message = "The defined workspace, '" + workspace.getAbsolutePath() + "', contains no files to 'mavenize'.";
                    throw new MojoFailureException(message);
                }
            }
        }
    }

    /**
     * Validates mqsiprofile
     */
    private void validateMQSIprofile() throws MojoFailureException {
        File file_mqsiprofile = new File(mqsiprofile);
        if (!file_mqsiprofile.exists() || !file_mqsiprofile.canExecute()) {
            String message = "The defined mqsiprofile, '" + mqsiprofile + "', needs to be a valid executable script.";
            throw new MojoFailureException(message);
        }
    }

    private void logMavenizeInstructions() {
        try {
            String instructions = ConfigurationValidator.getResourceText("instructions/mavenize.txt");
            getLog().info(instructions);
        } catch (IOException ignore) {
            // TODO handle exception
            getLog().warn("Unable to load instructions 'instructions/mavenize.txt'");
        }
    }
}
