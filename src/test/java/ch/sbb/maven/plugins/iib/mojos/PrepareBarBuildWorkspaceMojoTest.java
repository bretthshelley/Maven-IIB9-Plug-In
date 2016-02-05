package ch.sbb.maven.plugins.iib.mojos;

import static ch.sbb.maven.plugins.iib.mojos.PrepareBarBuildWorkspaceMojo.verifyIibIncludeTypes;
import junit.framework.Assert;

import org.junit.Test;


public class PrepareBarBuildWorkspaceMojoTest {

    @Test
    public void verifyIncludeTypes()
    {
        String types = "zip,jar";
        String actual = verifyIibIncludeTypes(types);
        String expected = "zip";
        Assert.assertEquals(expected, actual);

        types = "jar,zip,war";
        actual = verifyIibIncludeTypes(types);
        expected = "zip,war";
        Assert.assertEquals(expected, actual);

        types = null;
        actual = verifyIibIncludeTypes(types);
        expected = "zip";
        Assert.assertEquals(expected, actual);

        types = "";
        actual = verifyIibIncludeTypes(types);
        expected = "zip";
        Assert.assertEquals(expected, actual);

        types = "JAR,,zip, ,";
        actual = verifyIibIncludeTypes(types);
        expected = "zip";
        Assert.assertEquals(expected, actual);


        types = "ear,,JAR,,zip, ,";
        actual = verifyIibIncludeTypes(types);
        expected = "ear,zip";
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void checkUnpackIIbDependencies()
    {
        // / verify that the value is false
        PrepareBarBuildWorkspaceMojo mojo = new PrepareBarBuildWorkspaceMojo();

        Boolean expected = null;
        Boolean actual = mojo.unpackIibDependenciesIntoWorkspace;
        String message = "The unpackIibDependenciesIntoWorkspace should be by default null, but was " + actual;
        Assert.assertEquals(message, expected, actual);


    }
}
