package ch.sbb.maven.plugins.iib.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import ch.sbb.maven.plugins.iib.generated.maven_pom.Model;

/**
 * @author u209936
 * 
 */
public class PomXmlUtils {

    /**
     * @param pomFile
     * @return
     * @throws JAXBException
     */
    public static Model unmarshallPomFile(File pomFile)
            throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Model.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Model) JAXBIntrospector.getValue(unmarshaller.unmarshal(pomFile));
    }

    public static String getParentPomText(String groupId, String version, String distributionRepository, List<String> libraryProjects) throws IOException {
        String content = getParentPomTemplate();
        String groupIdRegex = Pattern.quote("<<groupId>>");
        content = content.replaceAll(groupIdRegex, groupId);
        // String artifactIdRegex = Pattern.quote("${artifactId}");
        // content = content.replaceAll(artifactIdRegex, artifactId);
        String versionRegex = Pattern.quote("<<version>>");
        content = content.replaceAll(versionRegex, version);
        if (libraryProjects != null && libraryProjects.size() > 0) {
            String modulesMarker = "<modules>";
            int startIndex = content.indexOf(modulesMarker) + modulesMarker.length();
            String firstPart = content.substring(0, startIndex);

            StringBuilder modulesText = new StringBuilder();
            boolean first = true;
            for (String libraryProject : libraryProjects) {
                if (first) {
                    modulesText.append("\n");
                    first = false;
                }
                String moduleText = "\t\t<module>" + libraryProject + "</module>\n";
                modulesText.append(moduleText);
            }

            String lastPart = content.substring(startIndex + 1);
            content = firstPart + modulesText.toString() + lastPart;
        }

        // / update the distribution repository
        String distributionRepositoryText = getDistributionRepositoryText(distributionRepository);
        String distributionRepositoryTextRegex = Pattern.quote("<<distributionRepositoryText>>");
        content = content.replaceAll(distributionRepositoryTextRegex, distributionRepositoryText);

        return content;
    }

    /**
     * @return
     * @throws IOException
     */
    private static String getParentPomTemplate() throws IOException {
        return getTemplateText("parent-pom-xml.template");
    }


    public static String getLibaryPomText(String groupId, String artifactId, String version, String distributionRepository, String[] dependentProjects) throws IOException {
        String content = getLibraryPomTemplate();
        String groupIdRegex = Pattern.quote("<<groupId>>");
        content = content.replaceAll(groupIdRegex, groupId);
        String artifactIdRegex = Pattern.quote("<<artifactId>>");
        content = content.replaceAll(artifactIdRegex, artifactId);
        String versionRegex = Pattern.quote("<<version>>");
        content = content.replaceAll(versionRegex, version);

        if (dependentProjects != null && dependentProjects.length > 0) {
            String dependenciesMarker = "<dependencies>";
            int startIndex = content.indexOf(dependenciesMarker) + dependenciesMarker.length();
            String firstPart = content.substring(0, startIndex);

            StringBuilder dependencies = new StringBuilder();
            for (String dependentProject : dependentProjects) {
                String dependencyText = getSingleDependencyText(groupId, dependentProject, version);
                dependencies.append("\n" + dependencyText + "\n");
            }

            String lastPart = content.substring(startIndex + 1);
            content = firstPart + dependencies.toString() + lastPart;
        }

        // / update the distribution repository
        String distributionRepositoryText = getDistributionRepositoryText(distributionRepository);
        String distributionRepositoryTextRegex = Pattern.quote("<<distributionRepositoryText>>");
        content = content.replaceAll(distributionRepositoryTextRegex, distributionRepositoryText);
        return content;
    }

    public static String replaceAll(String content, String toReplace, String replacement) {
        int startIndex = -1;
        while ((startIndex = content.indexOf(toReplace)) != -1) {
            String firstPart = startIndex == 0 ? "" : content.substring(0, startIndex);
            String lastPart = content.substring(startIndex + toReplace.length());
            content = firstPart + replacement + lastPart;
        }
        return content;
    }


    public static String getSharedLibraryPomText(File workspace, String groupId, String artifactId, String version, String distributionRepository, String[] dependentProjects, String mqsiprofile,
            Boolean depsLocal)
            throws IOException {

        String content;
        if (depsLocal) {
            content = getSharedLibraryDepsLocalPomTemplate();
        } else {
            content = getSharedLibraryPomTemplate();
        }
        content = replaceAll(content, "<<workspace>>", workspace.getAbsolutePath());
        String groupIdRegex = Pattern.quote("<<groupId>>");
        content = content.replaceAll(groupIdRegex, groupId);
        String artifactIdRegex = Pattern.quote("<<artifactId>>");
        content = content.replaceAll(artifactIdRegex, artifactId);
        String versionRegex = Pattern.quote("<<version>>");
        content = content.replaceAll(versionRegex, version);
        String mqsiprofileRegex = Pattern.quote("<<mqsiprofile>>");
        content = content.replaceAll(mqsiprofileRegex, mqsiprofile);

        if (dependentProjects != null && dependentProjects.length > 0) {
            String dependenciesMarker = "<dependencies>";
            int startIndex = content.indexOf(dependenciesMarker) + dependenciesMarker.length();
            String firstPart = content.substring(0, startIndex);

            StringBuilder dependencies = new StringBuilder();
            for (String dependentProject : dependentProjects) {
                String dependencyText = getSingleDependencyText(groupId, dependentProject, version);
                dependencies.append("\n" + dependencyText + "\n");
            }

            String lastPart = content.substring(startIndex + 1);
            content = firstPart + dependencies.toString() + lastPart;
        }

        // / update the distribution repository
        String distributionRepositoryText = getDistributionRepositoryText(distributionRepository);
        String distributionRepositoryTextRegex = Pattern.quote("<<distributionRepositoryText>>");
        content = content.replaceAll(distributionRepositoryTextRegex, distributionRepositoryText);


        return content;
    }

    public static String getApplicationPomText(File workspace, String groupId, String artifactId, String version, String distributionRepository, String[] dependentProjects, String mqsiprofile,
            Boolean depsLocal)
            throws IOException {
        String content;
        if (depsLocal) {
            content = getApplicationDepsLocalPomTemplate();
        } else {
            content = getApplicationPomTemplate();

        }
        content = replaceAll(content, "<<workspace>>", workspace.getAbsolutePath());
        String groupIdRegex = Pattern.quote("<<groupId>>");
        content = content.replaceAll(groupIdRegex, groupId);
        String artifactIdRegex = Pattern.quote("<<artifactId>>");
        content = content.replaceAll(artifactIdRegex, artifactId);
        String versionRegex = Pattern.quote("<<version>>");
        content = content.replaceAll(versionRegex, version);
        String mqsiprofileRegex = Pattern.quote("<<mqsiprofile>>");
        content = content.replaceAll(mqsiprofileRegex, mqsiprofile);

        if (dependentProjects != null && dependentProjects.length > 0) {
            String dependenciesMarker = "<dependencies>";
            int startIndex = content.indexOf(dependenciesMarker) + dependenciesMarker.length();
            String firstPart = content.substring(0, startIndex);

            StringBuilder dependencies = new StringBuilder();
            for (String dependentProject : dependentProjects) {
                String dependencyText = getSingleDependencyText(groupId, dependentProject, version);
                dependencies.append("\n" + dependencyText + "\n");
            }

            String lastPart = content.substring(startIndex + 1);
            content = firstPart + dependencies.toString() + lastPart;
        }

        // / update the distribution repository
        String distributionRepositoryText = getDistributionRepositoryText(distributionRepository);
        String distributionRepositoryTextRegex = Pattern.quote("<<distributionRepositoryText>>");
        content = content.replaceAll(distributionRepositoryTextRegex, distributionRepositoryText);

        return content;
    }

    public static String getSingleDependencyText(String groupId, String artifactId, String version) throws IOException {
        String content = getSingleDependencyTemplate();
        String groupIdRegex = Pattern.quote("<<groupId>>");
        content = content.replaceAll(groupIdRegex, groupId);
        String artifactIdRegex = Pattern.quote("<<artifactId>>");
        content = content.replaceAll(artifactIdRegex, artifactId);
        String versionRegex = Pattern.quote("<<version>>");
        content = content.replaceAll(versionRegex, version);
        return content;
    }

    public static String getDistributionRepositoryText(String distributionRepository) throws IOException {
        String content = getDistributionRepositoryTemplateText();
        String distributionRepositoryRegex = Pattern.quote("<<distribution.repository>>");
        content = content.replaceAll(distributionRepositoryRegex, distributionRepository);
        return content;
    }

    public static String getDistributionRepositoryTemplateText() throws IOException {
        return getTemplateText("distributionRepository.template");
    }

    public static String getLocalDevBrokerTempate() throws IOException {
        return getTemplateText("localdev-broker.template");
    }

    public static String getLocalDevConfigTemplate() throws IOException {
        return getTemplateText("localdev-deploy-config.template");
    }

    public static String getLocalDevPropertiesTemplate() throws IOException {
        return getTemplateText("localdev-properties.template");
    }

    public static String getLibraryPomTemplate() throws IOException {
        return getTemplateText("library-pom-xml.template");
    }

    public static String getApplicationPomTemplate() throws IOException {
        return getTemplateText("application-pom-xml.template");
    }

    public static String getApplicationDepsLocalPomTemplate() throws IOException {
        return getTemplateText("application-depslocal-pom-xml.template");
    }

    public static String getSharedLibraryPomTemplate() throws IOException {
        return getTemplateText("sharedlibrary-pom-xml.template");
    }

    public static String getSharedLibraryDepsLocalPomTemplate() throws IOException {
        return getTemplateText("sharedlibrary-depslocal-pom-xml.template");
    }

    public static String getSingleDependencyTemplate() throws IOException {
        return getTemplateText("single-dependency.template");
    }

    public static String getTemplateText(String resourcePath) throws IOException {
        InputStream is = null;// PomXmlUtils.class.getResourceAsStream("library-pom-xml.template");
        BufferedReader br = null;
        try {
            is = PomXmlUtils.class.getClassLoader().getResourceAsStream(resourcePath);
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder content = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                content.append(line + "\n");
            }
            return content.toString();
        } finally {
            try {
                br.close();
            } catch (Exception ignore) {
            }
            try {
                is.close();
            } catch (Exception ignore) {
            }

        }
    }

}
