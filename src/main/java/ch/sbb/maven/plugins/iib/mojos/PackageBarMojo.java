package ch.sbb.maven.plugins.iib.mojos;

import static ch.sbb.maven.plugins.iib.utils.ConfigurationValidator.validateCreateOrPackageBar;
import static ch.sbb.maven.plugins.iib.utils.ConfigurationValidator.validatePathToMqsiProfileScript;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import com.ibm.broker.config.appdev.CommandProcessorPublicWrapper;
import com.syntegrity.iib.EclipseProjUtils;
import com.syntegrity.iib.ProjectType;

import ch.sbb.maven.plugins.iib.utils.DependenciesManager;
import ch.sbb.maven.plugins.iib.utils.DirectoriesUtil;
import ch.sbb.maven.plugins.iib.utils.MqsiCommand;
import ch.sbb.maven.plugins.iib.utils.MqsiCommandLauncher;
import ch.sbb.maven.plugins.iib.utils.SkipUtil;


/**
 * Packages or Creates a .bar file.
 */
@Mojo(name = "package-bar", defaultPhase = LifecyclePhase.COMPILE)
public class PackageBarMojo extends AbstractMojo {

    /**
     * indicates whether this MOJO will use MQSI commands to perform tasks or default to original SBB approach.
     * 
     */
    @Parameter(property = "createOrPackageBar", required = false, defaultValue = "create")
    protected String createOrPackageBar;
    private boolean create;

    /**
     * indicates the absolute file path to the location of the mqsiprofile command or shell script.
     */
    @Parameter(property = "pathToMqsiProfileScript", defaultValue = "\"C:\\Program Files\\IBM\\MQSI\\9.0.0.2\\bin\\mqsiprofile.cmd\"", required = false)
    protected String pathToMqsiProfileScript;

    /**
     * a comma-separated list of commands that will be issued to the underlying os before launching the mqsi* command.
     * This will substitute for the Windows approach covered by the 'pathToMqsiProfileScript' value. These
     * commands should be operating system specific and
     * execute the mqsiprofile command as well as setup the launch of the followup mqsi command
     * 
     */
    @Parameter(property = "mqsiPrefixCommands", required = false)
    protected String mqsiPrefixCommands;

    @Parameter(property = "mqsiCreateBarReplacementCommand", required = false, defaultValue = "")
    protected String mqsiCreateBarReplacementCommand;

    @Parameter(property = "mqsiCreateBarCompileOnlyReplacementCommand", required = false, defaultValue = "")
    protected String mqsiCreateBarCompileOnlyReplacementCommand;


    @Parameter
    protected boolean mqsiCreateBarDeployAsSource;

    /**
     * The name of the BAR (compressed file format) archive file where the
     * result is stored.
     */
    @Parameter(property = "barName", defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.bar", required = true)
    protected File barName;

    /**
     * The name of the trace file to use when packaging bar files
     */
    @Parameter(property = "packageBarTraceFile", defaultValue = "${project.build.directory}/packagebartrace.txt", required = true)
    protected File packageBarTraceFile;

    /**
     * The name of the trace file to use when packaging bar files
     */
    @Parameter(property = "createBarTraceFile", defaultValue = "${project.build.directory}/createbartrace.txt", required = true)
    protected File createBarTraceFile;


    /**
     * The path of the workspace in which the projects are extracted to be built.
     */
    @Parameter(property = "workspace", required = true)
    protected File workspace;


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


    DependenciesManager dependenciesManager;


    private List<String> getApplicationAndLibraryParams() throws MojoFailureException {

        dependenciesManager = new DependenciesManager(project, workspace, getLog());
        List<String> params = new ArrayList<String>();

        params.add("-k");
        params.add(dependenciesManager.getApp());

        // if there are applications, add them
        if (!dependenciesManager.getDependentApps().isEmpty()) {
            params.addAll(dependenciesManager.getDependentApps());
        }

        // if there are libraries, add them
        if (!dependenciesManager.getDependentLibs().isEmpty()) {
            // instead of adding the dependent libraries ( which don't make it into the appzip - fix the
            // indirect references problem by altering the project's .project file


            try {
                dependenciesManager.fixIndirectLibraryReferences(project.getBasedir());
            } catch (Exception e) {
                // TODO handle exception
                throw new MojoFailureException("problem fixing Indirect Library References", e);
            }

            // params.add("-y");
            // params.addAll(dependenciesManager.getDependentLibs());
        }

        // if (dependenciesManager.getDependentApps().isEmpty() && dependenciesManager.getDependentLibs().isEmpty()) {
        // throw new MojoFailureException("unable to determine apps or libraries to packagebar/createbar");
        // }

        return params;
    }


    protected List<String> constructPackageBarParams() throws MojoFailureException {
        List<String> params = new ArrayList<String>();

        // bar file name - required
        params.add("-a");
        params.add(barName.getAbsolutePath());

        // workspace parameter - required
        createWorkspaceDirectory();
        params.add("-w");
        params.add(workspace.toString());

        // object names - required
        params.addAll(getApplicationAndLibraryParams());

        // always trace the packaging process
        params.add("-v");
        params.add(packageBarTraceFile.getAbsolutePath());

        return params;
    }

    protected List<String> constructCreateBarParams() throws MojoFailureException {
        List<String> params = new ArrayList<String>();

        // bar file name - required

        // workspace parameter - required
        createWorkspaceDirectory();
        params.add("-data");
        params.add(workspace.toString());

        params.add("-b");
        params.add(barName.getAbsolutePath());

        File projectDir = new File(workspace, project.getName());
        if (EclipseProjUtils.getProjectType(projectDir, getLog()) == ProjectType.APPLICATION) {
            params.add("-a");
        } else {
            // else we assume shared library
            params.add("-l");
        }
        params.add(project.getName());

        params.add("-cleanBuild");

        if (mqsiCreateBarDeployAsSource) {
            params.add("-deployAsSource");
        }

        if (EclipseProjUtils.getProjectType(projectDir, getLog()) == ProjectType.APPLICATION) {
            params.addAll(getApplicationAndLibraryParams());
        }

        // always trace the packaging process

        params.add("-trace");
        params.add("-v");
        params.add(createBarTraceFile.getAbsolutePath());

        return params;
    }

    protected List<String> constructCreateBarCompileOnlyParams() throws MojoFailureException {
        List<String> params = new ArrayList<String>();
        params.add("-data");
        params.add(workspace.toString());
        params.add("-compileOnly");
        return params;
    }


    /**
     * @param params
     * @throws MojoFailureException
     * @throws IOException
     */
    private void executeMqsiCreateBar(List<String> params) throws MojoFailureException, IOException {
        DirectoriesUtil util = new DirectoriesUtil();
        try {
            // / the better approach is simply to rename the pom.xml files as pom-xml-temp.txt
            // / and run maven with a "mvn [goal] -f pom.xml.txt"
            util.renamePomXmlFiles(workspace, getLog());

            new MqsiCommandLauncher().execute(
                    getLog(),
                    pathToMqsiProfileScript,
                    mqsiPrefixCommands,
                    MqsiCommand.mqsicreatebar,
                    params.toArray(new String[params.size()]),
                    mqsiCreateBarReplacementCommand);


        } finally {
            util.restorePomFiles(workspace, getLog());
        }

    }


    /**
     * @throws MojoFailureException
     */
    protected void createWorkspaceDirectory() throws MojoFailureException {
        if (!workspace.exists()) {
            workspace.mkdirs();
        }
        if (!workspace.isDirectory()) {
            throw new MojoFailureException(
                    "Workspace parameter is not a directory: "
                            + workspace.toString());
        }
    }

    @Override
    public void execute() throws MojoFailureException, MojoExecutionException {
        if (new SkipUtil().isSkip(this.getClass())) {
            return;
        }
        validateConfig();


        try {

            File barDir = barName.getParentFile();
            if (!barDir.exists()) {
                barDir.getParentFile().mkdirs();
            }

            List<String> params = null;
            if (create) {
                getLog().info("Creating bar file: " + barName);
                params = constructCreateBarParams();
                executeMqsiCreateBar(params);
            } else {
                getLog().info("Packaging bar file: " + barName);
                params = constructPackageBarParams();
                executeMqsiPackageBar(params);
            }
        } catch (Exception e) {

            throw new MojoFailureException(e.toString());
        }
    }


    /**
     * @throws MojoFailureException
     * 
     */
    private void validateConfig() throws MojoFailureException {
        String result = validateCreateOrPackageBar(createOrPackageBar, getLog());
        create = result.equals("create");
        if (create) {
            validatePathToMqsiProfileScript(pathToMqsiProfileScript, getLog());

        }


    }

    private void executeMqsiPackageBar(List<String> params) throws Exception {

        DirectoriesUtil util = new DirectoriesUtil();
        try {

            executeCreateBarCompileOnly();


            // / the better approach is simply to rename the pom.xml files as pom.xml.dat
            // / and run maven with a "mvn [goal] -f pom.xml.dat"
            util.renamePomXmlFiles(workspace, getLog());

            getLog().info("Packaging Bar File with the parameters: ");
            String[] paramsArray = params.toArray(new String[0]);
            for (String param : paramsArray) {
                getLog().info(param);
            }
            new CommandProcessorPublicWrapper(paramsArray).process();

        } finally {
            util.restorePomFiles(workspace, getLog());
        }


    }


    private void executeCreateBarCompileOnly() throws MojoFailureException {
        // / To compile your message sets and Java code, complete one of the following steps
        // Enter the following command:
        // mqsicreatebar -data workspace -compileOnly
        // where workspace is the name of the workspace that contains the message set projects or Java projects that you want to compile. Your message sets are compiled into .dictionary, or
        // .xsdzip files. Your Java code is compiled into .jar files.
        String[] messages = new String[] {
                "Prior to Packaging Bar, it is necessary to compile and package the java archives in the workspace",
                "Thus, msqicreatebar command will be executed in -compileonly mode"
        };
        for (String message : messages) {
            getLog().info(message);
        }

        List<String> compileOnlyParams = constructCreateBarCompileOnlyParams();
        new MqsiCommandLauncher().execute(
                getLog(),
                pathToMqsiProfileScript,
                mqsiPrefixCommands,
                MqsiCommand.mqsicreatebar,
                compileOnlyParams.toArray(new String[compileOnlyParams.size()]),
                mqsiCreateBarCompileOnlyReplacementCommand);

        // / stick the dependent jars into the root of the application
        Collection<String> javaProjects = dependenciesManager.getDependentJavaProjects();
        for (String javaProject : javaProjects) {
            File javaProjectDir = new File(workspace, javaProject);
            File jar = new File(javaProjectDir, javaProject + ".jar");
            try {
                FileUtils.copyFile(jar, new File(project.getBasedir(), javaProject + ".jar"));
            } catch (IOException e) {
                // TODO handle exception
                throw new MojoFailureException("could not copy jar", e);
            }


        }


    }

    // private void jarDependentJars()
    // {
    //
    //
    // Element pomElement = null; //
    // String pomFilePath = project.getBasedir().getAbsolutePath() + File.separator + "pom.xml";
    // File pomFile = new File(pomFilePath);
    // if (!pomFile.exists())
    // {
    // getLog().warn("no pom file could be located at " + pomFilePath);
    // pomElement = element("generatePom", "true");
    // }
    // else
    // {
    // pomElement = element("pomFile", pomFilePath);
    // }
    //
    // // unpack all IIB dependencies that match the given scope (compile)
    // Plugin plugin = plugin(groupId("org.apache.maven.plugins"), artifactId("maven-jar-plugin"), version("2.6"));
    // String goal = goal("jar");
    // Xpp3Dom xpp3Dom = configuration(
    // element(name("file"), zipFilePath), // / Output location.
    // element(name("repositoryLayout"), "default"), // / Comma Separated list of Types to include
    // element(name("artifactId"), project.getArtifactId()),
    // element(name("version"), project.getVersion()),
    // element(name("packaging"), "zip"),
    // element(name("groupId"), project.getGroupId()),
    // pomElement
    //
    // );
    //
    // ExecutionEnvironment executionEnvironment = executionEnvironment(project, session, buildPluginManager);
    // executeMojo(plugin, goal, xpp3Dom, executionEnvironment);
    // }
    //

}
