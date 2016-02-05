package ch.sbb.maven.plugins.iib.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

/**
 *
 * @author Brett Shelley
 * @version $Id: $
 * @since pom_version, 2015
 */
public class MqsiCommandLauncher {


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
    public void execute(Log log, String pathToMqsiProfileScript, String mqsiPrefixCommands, MqsiCommand mqsiCommand, String[] commands, String mqsiReplacementCommand) throws MojoFailureException
    {


        final ArrayList<String> osCommands = new ArrayList<String>();

        addMqsiSetProfileCommands(log, pathToMqsiProfileScript, mqsiPrefixCommands, osCommands);


        osCommands.add(mqsiCommand.toString());
        for (String c : commands)
        {
            osCommands.add(c);
        }
        logGeneratedCommands(log, osCommands);

        // / now we check to see if the user has chosen to run a separate mqsiReplacement command to override the default command
        // if replacement commands are set, then we replace the commands with what has been entered
        // this gives the user an opportunity to 'tweak' the mqsi commands being run

        if (mqsiReplacementCommand != null && !mqsiReplacementCommand.trim().isEmpty())
        {
            osCommands.clear();
            addMqsiSetProfileCommands(log, pathToMqsiProfileScript, mqsiPrefixCommands, osCommands);
            String[] replacementCommands = new CommandParser().parseCommands(mqsiReplacementCommand);
            for (String replacementCommand : replacementCommands)
            {
                osCommands.add(replacementCommand);
            }
            logReplacementCommands(log, osCommands);
        }


        final ProcessBuilder builder = new ProcessBuilder(osCommands);

        TimeElapsedThread thread = new TimeElapsedThread(log);
        try
        {
            builder.redirectErrorStream(true);
            builder.redirectOutput(Redirect.PIPE);


            thread.start();
            Process process = builder.start();

            final InputStream in = process.getInputStream();

            BufferedReader brStandardOut = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder out = new StringBuilder();
            String standardOutLine = null;
            while ((standardOutLine = brStandardOut.readLine()) != null)
            {
                log.info(standardOutLine);
                out.append(standardOutLine + "\n");
            }
            process.waitFor();
            thread.interrupt();

            log.info("process ended with a " + process.exitValue() + " value");

            if (process.exitValue() != 0)
            {
                throw new MojoFailureException(out.toString());
            }


        } catch (Exception e)
        {
            log.info("unable to execute " + mqsiCommand + " with arguments " + commands);
            throw new MojoFailureException("Unable to execute command(s): " + osCommands + " : " + e);
        }


    }

    private void addMqsiSetProfileCommands(Log log, String pathToMqsiProfileScript, String mqsiPrefixCommands, final ArrayList<String> osCommands) throws MojoFailureException {
        if (OSValidator.isWindows())
        {

            if (!FileUtils.fileExists(pathToMqsiProfileScript))
            {
                String message = "The mqsiprofile script could not be found or reached at the path " + pathToMqsiProfileScript;
                throw new MojoFailureException(message);
            }


            osCommands.add("cmd");
            osCommands.add("/c");
            osCommands.add(pathToMqsiProfileScript);// "\"C:\\Program Files\\IBM\\MQSI\\9.0.0.2\\bin\\mqsiprofile.cmd\"");
            osCommands.add("&");
            osCommands.add("cmd");
            osCommands.add("/c");
        }
        else
        {
            if (mqsiPrefixCommands == null || mqsiPrefixCommands.trim().isEmpty())
            {
                String message = "The configuration parameter 'mqsiPrefixCommands' is needed for this operating system.  This configuration parameter is a ";
                message += "comma-separated set of commands to run before executing a specific mqsicommand.  The command needs to typically execute mqsiprofile and setup the mqsicommand";
                throw new MojoFailureException(message);


            }
            String[] prefixCommands = mqsiPrefixCommands.split(Pattern.quote(","));
            for (String prefixCommand : prefixCommands)
            {
                if (prefixCommand == null || prefixCommand.trim().isEmpty()) {
                    continue;
                }
                log.info("adding '" + prefixCommand + "' to commands array");
                osCommands.add(prefixCommand);
            }

        }
    }

    public void logGeneratedCommands(Log log, List<String> osCommands)
    {

        String launchMessage = "\n\n"
                + "generated mqsiCommand follows...\n"
                + "--------------------------------\n\n";
        launchMessage += new CommandParser().toSingleLineCommand(osCommands);
        launchMessage += "\n\n";
        launchMessage += "--------------------------------\n";
        log.info(launchMessage);
    }


    public void logReplacementCommands(Log log, List<String> osCommands)
    {
        String launchMessage = "\n\n"
                + "replacement mqsiCommand follows...\n"
                + "--------------------------------\n\n";
        launchMessage += new CommandParser().toSingleLineCommand(osCommands);
        launchMessage += "\n\n";
        launchMessage += "--------------------------------\n";
        log.info(launchMessage);
    }


    class TimeElapsedThread extends Thread
    {
        private long startTime = -1;
        private long sleepTime = 20000;
        Log log;

        TimeElapsedThread(Log log)
        {
            this.log = log;
        }

        @Override
        public void run()
        {
            try
            {
                startTime = System.currentTimeMillis();

                while (true)
                {
                    Thread.sleep(sleepTime);

                    long timeElapsed = System.currentTimeMillis() - startTime;
                    long minutes = timeElapsed / 60000;
                    long seconds = (timeElapsed - (minutes * 60000)) / 1000;
                    String message = "";
                    if (minutes == 0)
                    {
                        message += seconds + " seconds elapsed...";
                    }
                    else if (minutes == 1)
                    {
                        if (seconds < 2)
                        {
                            message += minutes + " minute elapsed...";
                        }
                        else
                        {
                            message += minutes + " minute and " + seconds + " seconds elapsed...";
                        }

                    }
                    else
                    {
                        if (seconds < 2)
                        {
                            message += minutes + " minutes elapsed...";
                        }
                        else
                        {
                            message += minutes + " minutes and " + seconds + " seconds elapsed...";
                        }
                    }
                    log.info(message);

                }


            } catch (Exception ie)
            {
                log.info("shutting down timer");
            }


        }


    }


    static class OSValidator {

        private static String OS = System.getProperty("os.name").toLowerCase();

        public static boolean isWindows() {

            return (OS.indexOf("win") >= 0);
        }

        public static boolean isMac() {

            return (OS.indexOf("mac") >= 0);

        }

        public static boolean isUnix() {

            return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
        }

        public static boolean isSolaris() {

            return (OS.indexOf("sunos") >= 0);

        }

    }


}
