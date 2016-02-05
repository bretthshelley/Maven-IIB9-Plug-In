/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */
package ch.sbb.maven.plugins.iib.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * 
 *
 * @author Brett (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2016
 */
public class CommandParserTest {

    @Test
    public void parseCommands()
    {
        String content = "-c -a c:\\test\\programx -b";
        CommandParser cp = new CommandParser();
        String[] sa = cp.parseCommands(content);

        String actual = sa[0];
        String expected = "-c";
        assertEquals(expected, actual);
        actual = sa[1];
        expected = "-a";
        assertEquals(expected, actual);
        actual = sa[2];
        expected = "c:\\test\\programx";
        assertEquals(expected, actual);
        actual = sa[3];
        expected = "-b";
        assertEquals(expected, actual);

    }


    @Test
    public void parseCommandsWithQuotedSpace()
    {
        String content = "-c -a \"c:\\program files\\x.exe\" -b";
        CommandParser cp = new CommandParser();
        String[] sa = cp.parseCommands(content);

        String actual = sa[0];
        String expected = "-c";
        assertEquals(expected, actual);
        actual = sa[1];
        expected = "-a";
        assertEquals(expected, actual);
        actual = sa[2];
        expected = "\"c:\\program files\\x.exe\"";
        assertEquals(expected, actual);
        actual = sa[3];
        expected = "-b";
        assertEquals(expected, actual);

    }


    @Test(expected = IllegalStateException.class)
    public void parseCommandsWithMissingEndQuote()
    {
        String content = "-c -a \"c:\\program files\\x.exe -b";
        CommandParser cp = new CommandParser();
        String[] sa = cp.parseCommands(content);

        String actual = sa[0];
        String expected = "-c";
        assertEquals(expected, actual);
        actual = sa[1];
        expected = "-a";
        assertEquals(expected, actual);
        actual = sa[2];
        expected = "\"c:\\program files\\x.exe\"";
        assertEquals(expected, actual);
        actual = sa[3];
        expected = "-b";
        assertEquals(expected, actual);

    }
}
