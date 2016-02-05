/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2015.
 */
package ch.sbb.maven.plugins.iib.utils;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * 
 *
 * @author Brett (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2015
 */
public class DirectoriesUtilTest {

    @Test
    public void getDirectories()
    {
        String REGEX = DirectoriesUtil.REGEX;
        Assert.assertTrue(Arrays.equals(new String[] { "Hello", "World" }, "Hello,,World".split(REGEX)));
        Assert.assertTrue(Arrays.equals(new String[] { "Hello", "World" }, "Hello, ,World".split(REGEX)));
        Assert.assertTrue(Arrays.equals(new String[] { "Hel lo", "World" }, "Hel lo, ,World".split(REGEX)));


        String dir1 = "C:\\Program Files\\Hello Program";
        String dir2 = "target";
        String dir3 = "C:\\data\\projects";

        String[] expected = new String[] { dir1, dir2, dir3 };
        String var1 = dir1 + " ,," + dir2 + " ," + dir3;
        String[] actual = DirectoriesUtil.getFilesAndRegexes(var1);
        Assert.assertTrue(Arrays.equals(expected, actual));

        var1 = dir1 + " , ,," + dir2 + " ," + dir3 + ", ";
        actual = DirectoriesUtil.getFilesAndRegexes(var1);
        Assert.assertTrue(Arrays.equals(expected, actual));

    }

}
