package ch.sbb.maven.plugins.iib.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.maven.plugin.logging.Log;


public abstract class AbstractValidator {

    public static String getExampleTagString(String tagName, String exampleText) {
        return "<" + tagName + ">" + exampleText + "</" + tagName + ">";
    }

    public static String getArgumentMissingString(String argumentName) {
        return "The configuration argument '" + argumentName + "' is missing.";
    }

    public static String getFileMissingString(String name, String value) {
        return "The value '" + value + "' for the configuration argument '" + name + "' points to a file that does not exist.";
    }

    public static void logErrorStart(Log log) {
        log.error("-----------------------------------------------------------------------");
        log.error("MAVEN CONFIGURATION ERROR");
    }

    public static void logWarnStart(Log log) {
        log.warn("---------------------------------------------------------------------");
        log.warn("MAVEN CONFIGURATION WARNING");
    }

    public static void logErrorBaseProblem(Log log, String[] messages) {
        log.error("");
        log.error("\tBASE PROBLEM:");
        for (String message : messages)
        {
            log.error("\t" + message);
        }
    }

    public static void logWarnBaseProblem(Log log, String[] messages) {
        log.warn("");
        log.warn("\tBASE PROBLEM:");
        for (String message : messages)
        {
            log.warn("\t" + message);
        }
    }

    public static void logErrorInstructions(Log log, String[] messages) {
        log.error("");
        log.error("\tINSTRUCTIONS:");
        for (String message : messages)
        {
            log.error("\t" + message);
        }
        log.error("");
    }

    public static void logWarnInstructions(Log log, String[] messages) {
        log.warn("");
        log.warn("\tINSTRUCTIONS:");
        for (String message : messages)
        {
            log.warn("\t" + message);
        }
        log.warn("");
    }

    public static void logErrorExample(Log log, String[] messages) {
        log.error("");
        log.error("\tEXAMPLE:");
        for (String message : messages)
        {
            log.error("\t" + message);
        }
        log.error("");
    }

    public static void logWarnExample(Log log, String[] messages) {
        log.warn("");
        log.warn("\tEXAMPLE:");
        for (String message : messages)
        {
            log.warn("\t" + message);
        }
        log.warn("");
    }

    public static void logErrorFinish(Log log) {
        log.error("-----------------------------------------------------------------------");
    }

    public static void logWarnFinish(Log log) {
        log.warn("---------------------------------------------------------------------");
    }

    /**
     * 
     */
    public AbstractValidator() {
        super();
    }

    public static String getResourceText(String resourcePath) throws IOException
    {
        InputStream is = null;
        BufferedReader br = null;
        try
        {
            is = PomXmlUtils.class.getClassLoader().getResourceAsStream(resourcePath);
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder content = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null)
            {
                content.append(line + "\n");
            }
            return content.toString();
        } finally
        {
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