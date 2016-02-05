/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2015.
 */
package ch.sbb.maven.plugins.iib.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

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
public class SkipUtil {

    @SuppressWarnings("rawtypes")
    static LinkedHashMap<Class, String> classGoalMap = new LinkedHashMap<Class, String>();
    List<String> skipGoals = new ArrayList<String>();
    String skipToGoal = null;

    static
    {
        classGoalMap.put(InitializeBarBuildWorkspaceMojo.class, "initialize");
        classGoalMap.put(PrepareBarBuildWorkspaceMojo.class, "generate-resources");
        classGoalMap.put(ValidateBarBuildWorkspaceMojo.class, "process-resources");
        classGoalMap.put(PackageBarMojo.class, "compile");
        classGoalMap.put(String.class, "test-compile");
        classGoalMap.put(ApplyBarOverridesMojo.class, "process-classes");
        classGoalMap.put(ValidateClassloaderApproachMojo.class, "process-classes");
        classGoalMap.put(MqsiDeployMojo.class, "pre-integration-test");
        classGoalMap.put(Integer.class, "integration-test");
        classGoalMap.put(Double.class, "verify");
        classGoalMap.put(DeployBarMojo.class, "deploy");


    }

    private static String getValidGoals()
    {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String goal : classGoalMap.values())
        {
            if (!first)
            {
                sb.append(",");
            }
            sb.append(goal);
            first = false;
        }
        return sb.toString();
    }


    @SuppressWarnings("rawtypes")
    public boolean isSkip(Class clazz)
    {
        // / determine goals to skip from comma-separated list
        String skip = System.getProperty("skip");
        if (skip != null && !skip.trim().isEmpty())
        {
            String[] sa = skip.split(Pattern.quote(","));
            for (String goal : sa)
            {
                if (goal == null) {
                    continue;
                }
                if (goal.trim().isEmpty()) {
                    continue;
                }
                goal = goal.trim().toLowerCase();
                if (!classGoalMap.values().contains(goal)) {
                    throw new RuntimeException("The '-Dskip=...' value(s) must be a comma-separated list of goal(s) from the group:" + getValidGoals());

                }
                skipGoals.add(goal);
            }
        }

        // / determine goals to skip from comma-separated list
        String skipTo = System.getProperty("skipTo");
        if (skipTo != null && !skipTo.trim().isEmpty())
        {
            skipTo = skipTo.toLowerCase().trim();

            if (!classGoalMap.values().contains(skipTo))
            {
                throw new RuntimeException("The '-DskipTo=...' value must be a single goal from the group:" + getValidGoals());
            }
            skipToGoal = skipTo;

        }


        if (skipGoals.isEmpty() && skipToGoal == null) {
            return false;
        }

        String currentGoal = classGoalMap.get(clazz);
        if (skipGoals.contains(currentGoal))
        {
            return true;
        }

        // / see if skipToGoal
        if (skipToGoal == null)
        {
            return false;
        }


        // / go through the list to determine if goal is before current
        if (skipToGoal.equals(currentGoal)) {
            return false;
        }

        List<String> goalsBefore = new ArrayList<String>();
        List<String> goalsAfter = new ArrayList<String>();
        boolean skipToGoalFound = false;
        for (String goal : classGoalMap.values())
        {
            if (goal.equals(skipToGoal))
            {
                skipToGoalFound = true;
                continue;
            }
            if (skipToGoalFound)
            {
                goalsAfter.add(goal);
            }
            else
            {
                goalsBefore.add(goal);
            }
        }

        if (goalsBefore.contains(currentGoal)) {
            return true;
        }
        if (goalsAfter.contains(currentGoal)) {
            return false;
        }
        return false;


    }


}
