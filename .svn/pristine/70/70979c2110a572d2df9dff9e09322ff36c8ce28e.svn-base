/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2015.
 */
package ch.sbb.maven.plugins.iib.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.sbb.maven.plugins.iib.mojos.ApplyBarOverridesMojo;
import ch.sbb.maven.plugins.iib.mojos.DeployBarMojo;
import ch.sbb.maven.plugins.iib.mojos.InitializeBarBuildWorkspaceMojo;
import ch.sbb.maven.plugins.iib.mojos.MqsiDeployMojo;
import ch.sbb.maven.plugins.iib.mojos.PackageBarMojo;
import ch.sbb.maven.plugins.iib.mojos.PrepareBarBuildWorkspaceMojo;
import ch.sbb.maven.plugins.iib.mojos.ValidateBarBuildWorkspaceMojo;
import ch.sbb.maven.plugins.iib.mojos.ValidateClassloaderApproachMojo;

/**
 *
 * 
 *
 * @author Brett (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2015
 */
public class SkipUtilTest {


    /*
     * 
     * classGoalMap.put(InitializeBarBuildWorkspaceMojo.class, "initialize");
     * classGoalMap.put(PrepareBarBuildWorkspaceMojo.class, "generate-resources");
     * classGoalMap.put(ValidateBarBuildWorkspaceMojo.class, "process-resources");
     * classGoalMap.put(PackageBarMojo.class, "compile");
     * classGoalMap.put(String.class, "testCompile");
     * classGoalMap.put(ApplyBarOverridesMojo.class, "process-classes");
     * classGoalMap.put(ValidateClassloaderApproachMojo.class, "process-classes");
     * classGoalMap.put(MqsiDeployMojo.class, "pre-integration-test");
     * classGoalMap.put(Integer.class, "integration-test");
     * classGoalMap.put(Double.class, "verify");
     * classGoalMap.put(DeployBarMojo.class, "deploy");
     */

    @Before
    public void setup()
    {
        System.getProperties().remove("skip");
        System.getProperties().remove("skipTo");
    }


    @Test()
    public void testSkipToInitializeTest()
    {

        System.setProperty("skipTo", "initialize");
        Assert.assertFalse(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));


    }

    @Test()
    public void testSkipToGenerateResourcesTest()
    {

        System.setProperty("skipTo", "generate-resources");
        Assert.assertTrue(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));


    }


    @Test()
    public void testSkipToProcessResourcesTest()
    {

        System.setProperty("skipTo", "process-resources");
        Assert.assertTrue(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));


    }


    @Test()
    public void testSkipToCompileTest()
    {

        System.setProperty("skipTo", "compile");
        Assert.assertTrue(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));


    }

    @Test()
    public void testSkipToProcessClassesTest()
    {

        System.setProperty("skipTo", "process-classes");
        Assert.assertTrue(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));


    }

    @Test()
    public void testSkipToPreIntegrationTest()
    {

        System.setProperty("skipTo", "pre-integration-test");
        Assert.assertTrue(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));


    }

    @Test()
    public void testSkipToIntegrationTest()
    {


        System.setProperty("skipTo", "integration-test");
        Assert.assertTrue(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));


    }


    @Test()
    public void testSkipToVerify()
    {


        System.setProperty("skipTo", "verify");
        Assert.assertTrue(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));


    }


    @Test()
    public void testSkipToDeploy()
    {


        System.setProperty("skipTo", "deploy");
        Assert.assertTrue(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));


    }


    @Test(expected = java.lang.RuntimeException.class)
    public void testJustOneSkipTo()
    {
        System.setProperty("skipTo", "compile, deploy, Pre-integration-test");
        Assert.assertFalse(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(DeployBarMojo.class));

    }


    @Test()
    public void testMultipleGoals()
    {
        System.setProperty("skip", "compile, deploy, Pre-integration-test");
        Assert.assertFalse(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(DeployBarMojo.class));

    }

    @Test(expected = java.lang.RuntimeException.class)
    public void testBadGoals()
    {
        System.setProperty("skip", "compile,pree-integration-test");
        Assert.assertFalse(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));

    }

    @Test
    public void testTwoSkips()
    {
        System.setProperty("skip", "compile,pre-integration-test");
        Assert.assertFalse(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));

    }


    @Test
    public void testNoSkip()
    {
        Assert.assertFalse(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));

    }


    @Test
    public void testOneSkip()
    {
        System.setProperty("skip", "compile");
        Assert.assertFalse(new SkipUtil().isSkip(InitializeBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(PrepareBarBuildWorkspaceMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateBarBuildWorkspaceMojo.class));
        Assert.assertTrue(new SkipUtil().isSkip(PackageBarMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ApplyBarOverridesMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(ValidateClassloaderApproachMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(MqsiDeployMojo.class));
        Assert.assertFalse(new SkipUtil().isSkip(DeployBarMojo.class));

    }

}
