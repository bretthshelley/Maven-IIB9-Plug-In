/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2015.
 */
package ch.sbb.maven.plugins.iib.utils;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 *
 * 
 *
 * @author Brett (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2015
 */
public class DependenciesManager {
    private TreeSet<String> apps = new TreeSet<String>();
    private TreeSet<String> libs = new TreeSet<String>();
    private TreeSet<String> javaProjects = new TreeSet<String>();

    private MavenProject project;
    private File workspace;
    private Log log;

    public DependenciesManager() {
        super();
    }


    public DependenciesManager(MavenProject project, File workspace, Log log) throws MojoFailureException {
        super();
        this.project = project;
        this.workspace = workspace;
        this.log = log;
        determineDependencies();
    }

    public String getApp()
    {
        return project.getArtifactId();
    }

    public Collection<String> getDependentApps()
    {
        Set<String> dependentApps = new TreeSet<String>();
        dependentApps.addAll(apps);
        dependentApps.remove(project.getArtifactId());
        return dependentApps;
    }

    public Collection<String> getDependentLibs()
    {
        Set<String> dependentLibs = new TreeSet<String>();
        dependentLibs.addAll(libs);
        return dependentLibs;
    }

    public Collection<String> getDependentJavaProjects()
    {
        Set<String> projects = new TreeSet<String>();
        projects.addAll(javaProjects);
        return projects;
    }

    private void determineEclipseProjectDependencies() throws MojoFailureException
    {
        determineEclipseProjectDependencies(project.getArtifactId());
    }

    private void determineDependencies() throws MojoFailureException {

        determineEclipseProjectDependencies();

        // / let's add this project itself, either as an application or a
        if (EclipseProjectUtils.isApplication(new File(workspace, project.getArtifactId()), getLog()))
        {
            apps.add(project.getArtifactId());
        }
        else if (EclipseProjectUtils.isLibrary(new File(workspace, project.getArtifactId()), getLog()))
        {
            libs.add(project.getArtifactId());
        }
        else if (EclipseProjectUtils.isJavaProject(new File(workspace, project.getArtifactId()), getLog()))
        {
            javaProjects.add(project.getArtifactId());
        }
        else
        {
            // / make the assumption that the project itself is an application
            apps.add(project.getArtifactId());
        }

        for (Dependency dependency : project.getDependencies()) {

            // only check for dependencies with scope "compile"
            if (!dependency.getScope().equals("compile")) {
                continue;
            }

            // the projectName is the directoryName is the artifactId
            String projectName = dependency.getArtifactId();

            if (EclipseProjectUtils.isApplication(new File(workspace, projectName), getLog())) {
                apps.add(projectName);
            }
            else if (EclipseProjectUtils.isLibrary(new File(workspace, projectName), getLog())) {
                libs.add(projectName);
            }

        }


    }


    public void fixIndirectLibraryReferences(File projectDirectory) throws Exception
    {
        new EclipseProjectFixUtil().fixIndirectLibraryReferences(projectDirectory, libs, log);
    }

    /**
     * @param projectDirectory
     * @throws MojoFailureException
     */
    private void determineEclipseProjectDependencies(String projectDirectory) throws MojoFailureException
    {
        File projectDir = new File(workspace, projectDirectory);
        String[] projectNames = EclipseProjectUtils.getDependentProjectNames(projectDir);
        for (String projectName : projectNames)
        {
            if (EclipseProjectUtils.isApplication(new File(workspace, projectName), getLog()))
            {
                apps.add(projectName);

            }
            else if (EclipseProjectUtils.isLibrary(new File(workspace, projectName), getLog()))
            {
                libs.add(projectName);
            }
            determineEclipseProjectDependencies(projectName);
        }

    }


    private Log getLog() {
        return log;
    }


}
