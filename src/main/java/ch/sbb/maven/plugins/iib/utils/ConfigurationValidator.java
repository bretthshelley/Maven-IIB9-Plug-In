package ch.sbb.maven.plugins.iib.utils;

import java.io.File;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class ConfigurationValidator extends AbstractValidator {


    public static void validateUnpackIibDependenciesIntoWorkspace(Boolean unpackIibDependenciesIntoWorkspace, Log log) throws MojoFailureException
    {
        if (unpackIibDependenciesIntoWorkspace != null) {
            return;
        }

        logErrorStart(log);
        String[] messages = { getArgumentMissingString("unpackIibDependenciesIntoWorkspace") };
        logErrorBaseProblem(log, messages);

        String tagName = "unpackIibDependenciesIntoWorkspace";
        String exampleText1 = "true";
        String exampleTagString1 = getExampleTagString(tagName, exampleText1);
        String exampleText2 = "false";
        String exampleTagString2 = getExampleTagString(tagName, exampleText2);
        logErrorExample(log, new String[] { exampleTagString1, "OR", exampleTagString2 });

        String[] instructions = new String[]
        {
                "When set to true, the plugin will attempt to download and unzip the dependent projects into",
                "workspace directory.  For example, the HDR project might have a dependency defined like:",
                "\n",
                "<dependency>",
                "\t<groupId>gov.va.emi</groupId>",
                "\t<artifactId>EMICommon</artifactId>",
                "\t<version>1.0</version>",
                "\t<type>zip</type>",
                "\t<scope>compile</scope>",
                "</dependency>",
                "\n",
                "In this case, maven will get the zipped up EMICommon project from the remote repository and ",
                "unzip the project so that it is a 'sibling' of your project within the workspace.",
                "\n",
                "When set to false, then the plugin will not attempt to download and unzip the ",
                "dependent projects into your workspace directory.  In this case, it is each developer's",
                "responsibility to ensure that dependent projects are available in the workspace."
        };
        logErrorInstructions(log, instructions);

        logErrorFinish(log);

        throw new MojoFailureException(getArgumentMissingString("unpackIibDependenciesIntoWorkspace"));


    }

    /**
     * checks whether the createOrPackage configuration argument has been supplied.
     * 
     * @param createOrPackageBar
     * @param log
     * @return create or package or throws exception is not defined
     * @throws MojoFailureException
     */
    public static String validateCreateOrPackageBar(String createOrPackageBar, Log log) throws MojoFailureException
    {
        if (createOrPackageBar != null && !createOrPackageBar.trim().isEmpty())
        {
            if (createOrPackageBar.trim().equalsIgnoreCase("create")
                    || createOrPackageBar.trim().equalsIgnoreCase("createbar")) {
                return "create";
            }
            if (createOrPackageBar.trim().equalsIgnoreCase("package")
                    || createOrPackageBar.trim().equalsIgnoreCase("packagebar")) {
                return "package";
            }
        }

        logErrorStart(log);
        String[] messages = { getArgumentMissingString("createOrPackageBar") };
        logErrorBaseProblem(log, messages);

        String tagName = "createOrPackageBar";
        String exampleText1 = "create";
        String exampleTagString1 = getExampleTagString(tagName, exampleText1);
        String exampleText2 = "package";
        String exampleTagString2 = getExampleTagString(tagName, exampleText2);
        logErrorExample(log, new String[] { exampleTagString1, "OR", exampleTagString2 });

        String[] instructions = new String[]
        {
                "The 'createOrPackageBar' configuration value tells the plugin whether to ",
                "execute mqsiCreateBar which compiles messages flows or to",
                "execute packageBar which does not compile message flows.",
                "Note that the 'create' (mqsiCreateBar) approach is preferred but slower,",
                "while the 'package' approach is faster but does not compile message flows."
        };
        logErrorInstructions(log, instructions);

        logErrorFinish(log);

        throw new MojoFailureException(getArgumentMissingString("createOrPackageBar"));


    }


    public static void validatePathToMqsiProfileScript(String pathToMqsiProfileScript, Log log) throws MojoFailureException
    {
        if (pathToMqsiProfileScript == null || pathToMqsiProfileScript.trim().equals(""))
        {
            logErrorStart(log);
            String[] messages = { getArgumentMissingString("pathToMqsiProfileScript") };
            logErrorBaseProblem(log, messages);
            String tagName = "pathToMqsiProfileScript";
            String exampleText1 = "C:\\Program Files\\IBM\\MQSI\\9.0.0.2\\bin\\mqsiprofile.cmd";
            String exampleTagString1 = getExampleTagString(tagName, exampleText1);
            logErrorExample(log, new String[] { exampleTagString1 });
            String[] instructions = new String[]
            {
                    "The 'pathToMqsiProfileScript' configuration value tells the plugin where to ",
                    "execute the mqsiprofile command or shell script.  It is necessary to execute",
                    "this command prior to executing mqsicreatebar..."
            };
            logErrorInstructions(log, instructions);
            logErrorFinish(log);
            throw new MojoFailureException(getArgumentMissingString("copyDependentJarsLocation"));
        }
        File file = new File(pathToMqsiProfileScript);
        if (!file.exists())
        {
            logErrorStart(log);
            String[] messages = { getFileMissingString("pathToMqsiProfileScript", pathToMqsiProfileScript) };
            logErrorBaseProblem(log, messages);
            String tagName = "pathToMqsiProfileScript";
            String exampleText1 = "C:\\Program Files\\IBM\\MQSI\\9.0.0.2\\bin\\mqsiprofile.cmd";
            String exampleTagString1 = getExampleTagString(tagName, exampleText1);
            logErrorExample(log, new String[] { exampleTagString1 });
            String[] instructions = new String[]
            {
                    "The 'pathToMqsiProfileScript' configuration value tells the plugin where to ",
                    "execute the mqsiprofile command or shell script. "
            };
            logErrorInstructions(log, instructions);
            logErrorFinish(log);
            throw new MojoFailureException(getArgumentMissingString("copyDependentJarsLocation"));
        }

    }

    public static void validateUseClassloaders(Boolean useClassloaders, Log log) throws MojoFailureException
    {
        if (useClassloaders == null)
        {
            logErrorStart(log);
            String[] messages = { getArgumentMissingString("useClassloaders") };
            logErrorBaseProblem(log, messages);

            String tagName = "useClassloaders";
            String exampleText = "false";
            String exampleTagString = getExampleTagString(tagName, exampleText);
            logErrorExample(log, new String[] { exampleTagString });

            String[] instructions = new String[] {
                    "'useClassLoaders' indicates whether classloaders are in use with this bar;",
                    "false adds dependent jars into workspace META-INF dir; true does not; ",
                    "both true and false will unpack dependent jar resources.",
                    "This also comes into play in the Bar Override process."

            };

            logErrorInstructions(log, instructions);

            logErrorFinish(log);

            throw new MojoFailureException(getArgumentMissingString("useClassloaders"));
        }
    }

    public static void validateWorkspace(File workspace, Log log) throws MojoFailureException
    {
        if (workspace == null)
        {
            logErrorStart(log);
            String[] messages = { getArgumentMissingString("workspace") };
            logErrorBaseProblem(log, messages);

            String tagName = "workspace";
            String exampleText = "[path to your IIB workspace]";
            String exampleTagString = getExampleTagString(tagName, exampleText);
            logErrorExample(log, new String[] { exampleTagString });

            logErrorFinish(log);

            throw new MojoFailureException(getArgumentMissingString("workspace"));
        }
    }

    /**
     * @param unpackIibDependencyTypes
     * @param log
     */
    public static void validateUnpackIibDependencyTypes(String unpackIibDependencyTypes, Log log)
    {
        if (unpackIibDependencyTypes == null || unpackIibDependencyTypes.trim().isEmpty())
        {
            logWarnStart(log);
            String[] messages = new String[] {
                    "The unpackIibDependencyTypes configuration is not defined",
                    "Defaulting to 'zip'",

            };
            logWarnBaseProblem(log, messages);

            String exampleString = getExampleTagString("unpackIibDependencyTypes", "zip");

            logWarnExample(log, new String[] { exampleString });

            logWarnFinish(log);
        }

        else if (unpackIibDependencyTypes != null && unpackIibDependencyTypes.toLowerCase().contains("jar"))
        {
            logWarnStart(log);
            String[] messages = new String[] {
                    "The unpackIibDependencyTypes configuration contains the word 'jar'",
                    "The jar component is being removed. ",

            };
            logErrorBaseProblem(log, messages);

            String exampleString = getExampleTagString("unpackIibDependencyTypes", "zip");

            logErrorExample(log, new String[] { exampleString });

            logWarnFinish(log);
        }

    }


    public static void validateCopyDependentJarsLocation(String copyDependentJarsLocation, Log log) throws MojoFailureException
    {
        if (copyDependentJarsLocation != null) {
            return;
        }

        logErrorStart(log);
        String[] messages = { getArgumentMissingString("copyDependentJarsLocation") };
        logErrorBaseProblem(log, messages);

        String tagName = "copyDependentJarsLocation";
        String exampleText1 = "${project.build.directory}/lib";
        String exampleTagString1 = getExampleTagString(tagName, exampleText1);
        String exampleText2 = "${project.basedir}";
        String exampleTagString2 = getExampleTagString(tagName, exampleText2);
        logErrorExample(log, new String[] { exampleTagString1, "OR", exampleTagString2 });

        String[] instructions = new String[]
        {
                "The 'copyDependentJarsLocation' configuration value tells the plugin where to download",
                "and copy dependent java archives. Only jars with compile scope will be downloaded. ",
                "For example, the project might have a dependency defined like:",
                "\n",
                "<dependency>",
                "\t<groupId>gov.va.emi</groupId>",
                "\t<artifactId>ThirdPartyJavaArchive</artifactId>",
                "\t<version>1.0</version>",
                "\t<type>jar</type>",
                "\t<scope>compile</scope>",
                "</dependency>",
                "\n",
                "In this case, the ThirdPartyJavaArchive jar will be downloaded to the designated directory.  ",
                "Furthermore, the compile scope dependencies of ThirdPartyJavaArchive will also be downloaded."
        };
        logErrorInstructions(log, instructions);

        logErrorFinish(log);

        throw new MojoFailureException(getArgumentMissingString("unpackIibDependenciesIntoWorkspace"));


    }


    /**
     * @param projectBaseDir
     * @param resourceDirectory
     * @param log
     * @param warning
     */
    public static void warnOnApplyBarOverrides(String projectBaseDir, File resourceDirectory, Log log, String warning) {
        logWarnStart(log);
        logWarnBaseProblem(log, new String[] { warning });
        String[] instructions = new String[]
        {
                "The iib9 maven plugin's process-classes goal executes the applybaroverrides command using the generated bar file",
                "",
                "Here's how it works:",
                "1 - resource files in your project's src/main/resources directory are written to target/iib-overrides",
                "2 - mqsireadbar is executed against the just-generated bar.",
                "\t- The just-generated bar will normally have a format of <yourproject>-<version>.bar - e.g. HDR-1.0-SNAPSHOT.bar",
                "\t- The properties found in the bar are written to target/default.properties",
                "3 - The properties files in the iib-overrides directory are checked",
                "    to ensure that each property matches a property found in the bar (default.properties)",
                "4 - Overridden bars are created in the iib-overrides directory for each valid properties file.",
                "\t- eg:  apply-bar-overrides will be executed against hdr-dev.properties and HDR-1.0-SNAPSHOT.bar to produce HDR-1.0-SNAPSHOT-hdr-dev.bar",
                "\t- This process will be executed against each properties file in resource and test resource directories",
                "\t- The overriden bar files are found in either  target/iib-overrides or  target/iib-test-overrides "


        };
        logWarnInstructions(log, instructions);

        logWarnFinish(log);
    }

    public static void validateDeployBarMojo(MavenProject project, Log log) throws MojoFailureException
    {
        ArtifactRepository repo = project.getDistributionManagementArtifactRepository();
        if (repo == null)
        {


            logErrorStart(log);
            String[] messages = { "DistributionManagementArtifactRepository is not defined!" };
            logErrorBaseProblem(log, messages);


            String[] instructions = new String[] {
                    "Your Maven pom.xml file is missing a distribution management entry.",
                    "",
                    "For example, a configuration similar to the following should be in your pom.xml file:",
                    "",
                    "<distributionManagement>",
                    "\t<repository>",
                    "\t\t<id>distribution.repository</id>",
                    "\t\t<name>distribution.repository</name>",
                    "\t\t<url>http://ec2-52-34-30-253.us-west-2.compute.amazonaws.com:8081/nexus/content/repositories/releases/</url>",
                    "\t</repository>",
                    "</distributionManagement>",
                    "",
                    "Also, a corresponding configuration is needed in [user.home.dir]/.m2/settings.xml that contains server authentication information:",
                    "",
                    "For example:",
                    "",
                    "<server>",
                    "\t<id>distribution.repository</id>",
                    "\t<username>admin</username>",
                    "\t<password>[admin password]</password>",
                    "</server>"
            };

            logErrorInstructions(log, instructions);

            logErrorFinish(log);

            throw new MojoFailureException(messages[0]);


        }


        boolean isSnapshot = project.getVersion().toLowerCase().contains("snapshot");
        boolean isSnapshotUrl = repo.getUrl().toLowerCase().contains("snapshot");
        boolean isReleaseUrl = repo.getUrl().toLowerCase().contains("release");

        if (isSnapshot && isReleaseUrl)
        {
            String[] messages = new String[] {
                    "possible mismatch between snapshot version and release Url",
                    "Your Version " + project.getVersion() + " appears to be in conflict with the url " + repo.getUrl()
            };
            logWarnStart(log);

            logWarnBaseProblem(log, messages);

            logWarnFinish(log);
        }
        if (!isSnapshot && isSnapshotUrl)
        {
            String[] messages = new String[] {
                    "possible mismatch between release version and snapshot Url",
                    "Your Version " + project.getVersion() + " appears to be in conflict with the url " + repo.getUrl()
            };
            logWarnStart(log);

            logWarnBaseProblem(log, messages);

            logWarnFinish(log);
        }
        if (!isSnapshot)
        {
            String[] messages = new String[] {
                    "Please Note:",
                    "*** This is a warning to consider if your build fails for unknown reason(s):***",
                    "",
                    "Your Version " + project.getVersion() + " appears to a release.",
                    "If you attempt to deploy a release to a Distribution Server that already contains the release,",
                    "then you are likely to get an error such as 'Http 400: Bad Request.",
                    "To redeploy a version, then the existing release version must be deleted from ",
                    "the distribution repository."
            };
            logWarnStart(log);

            logWarnBaseProblem(log, messages);

            logWarnFinish(log);


        }


    }


}
