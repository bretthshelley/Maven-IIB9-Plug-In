/*
 * Copyright (C) Syntegrity Solutions Pty Ltd, 2017.
 */
package com.syntegrity.iib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.devdaily.system.SystemCommandExecutor;

import ch.sbb.maven.plugins.iib.utils.MqsiCommand;
import ch.sbb.maven.plugins.iib.utils.MqsiCommandLauncher;
import ch.sbb.maven.plugins.iib.utils.TimeElapsedThread;

/**
 *
 * 
 *
 * @author steve (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2017
 */
public class LinuxMqsiCommandLauncher extends MqsiCommandLauncher {

    /**
     * 
     * @param log
     * @param pathToMqsiProfileScript
     * @param mqsiPrefixCommands
     * @param mqsiCommand
     * @param commands
     * @param mqsiReplacementCommand
     * @throws MojoFailureException
     */
    @Override
    public void execute(Log log, String pathToMqsiProfileScript, String mqsiPrefixCommands, MqsiCommand mqsiCommand, String[] commands, String mqsiReplacementCommand) throws MojoFailureException {

        final ArrayList<String> osCommands = new ArrayList<String>();

        addMqsiSetProfileCommands(log, osCommands);
        String cmd = null;

        // logGeneratedCommands(log, osCommands);

        // / now we check to see if the user has chosen to run a separate mqsiReplacement command to override the default command
        // if replacement commands are set, then we replace the commands with what has been entered
        // this gives the user an opportunity to 'tweak' the mqsi commands being run

        if (mqsiReplacementCommand != null && !mqsiReplacementCommand.trim().isEmpty()) {

            cmd = mqsiReplacementCommand;

        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(mqsiCommand.toString());
            for (String c : commands) {
                sb.append(" ");
                sb.append(c);
            }
            cmd = sb.toString();
        }
        try {
            File tf = createTempScript(pathToMqsiProfileScript, cmd);
            osCommands.add(tf.getAbsolutePath());
            logGeneratedCommands(log, osCommands);
            SystemCommandExecutor commandExecutor = new SystemCommandExecutor(osCommands);

            TimeElapsedThread thread = new TimeElapsedThread(log);
            thread.start();
            int result = commandExecutor.executeCommand();

            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            // print the stdout and stderr
            log.info("STDOUT:");
            log.info(stdout);
            log.info("STDERR:");
            log.info(stderr);
            thread.interrupt();

            log.info("process ended with a " + result + " value");
            tf.delete();
            if (result != 0) {
                throw new MojoFailureException(stdout.toString());
            }

        } catch (Exception e) {
            log.info("(linux) unable to execute " + mqsiCommand + " with arguments " + osCommands);
            throw new MojoFailureException("Unable to execute command(s): " + osCommands + " : " + e);
        }


    }

    private void addMqsiSetProfileCommands(Log log, final ArrayList<String> osCommands) throws MojoFailureException {
        osCommands.add("/bin/bash");
    }

    public File createTempScript(String pathToMqsiProfileScript, String command) throws IOException {
        File tempScript = File.createTempFile("script-createbar", null);

        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(
                tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");
        printWriter.println("cat " + tempScript.getAbsolutePath());
        printWriter.println("if ! which mqsilist; then source " + pathToMqsiProfileScript + "; fi");
        printWriter.println(command);
        printWriter.close();

        return tempScript;
    }

}
