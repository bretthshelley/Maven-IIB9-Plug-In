/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */
package ch.sbb.maven.plugins.iib.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 
 *
 * @author Brett (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2016
 */
public class CommandParser {


    public String[] parseCommands(String content)
    {

        List<String> results = new ArrayList<String>();

        String[] sa = content.split("\\s+");

        boolean mergingCommands = false;
        String mergedCommand = "";
        for (int i = 0; i < sa.length; i++)
        {
            String term = sa[i];

            if (term.startsWith("\""))
            {
                mergingCommands = true;
                mergedCommand = term + ' ';
                continue;
            }
            if (mergingCommands)
            {
                if (term.endsWith("\""))
                {
                    mergedCommand += term;
                    results.add(mergedCommand);
                    mergedCommand = "";
                    mergingCommands = false;
                    continue;
                }
                else
                {
                    mergedCommand += term + ' ';
                    continue;
                }
            }
            else
            {
                results.add(term);
            }

        }

        if (mergingCommands == true)
        {
            throw new IllegalStateException("No closing double-quote character found for " + mergedCommand);
        }

        sa = results.toArray(new String[results.size()]);
        validateCommands(sa);
        return sa;

    }

    public String toSingleLineCommand(List<String> commands)
    {
        return toSingleLineCommand(commands.toArray(new String[commands.size()]));
    }

    public String toSingleLineCommand(String[] commands)
    {
        StringBuilder result = new StringBuilder();
        for (String command : commands)
        {
            result.append(command + ' ');
        }
        return result.toString().trim();
    }

    public void validateCommands(String[] commands)
    {
        for (String command : commands)
        {
            if (command.startsWith("\""))
            {
                if (!command.endsWith("\""))
                {
                    String message = command + " starts with \" character but does not end with \" character";
                    throw new IllegalArgumentException(message);
                }
            }

        }


    }


}
