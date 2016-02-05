/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */
package ch.sbb.maven.plugins.iib.utils;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

/**
 *
 * 
 *
 * @author Brett (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2016
 */
public class JavaProjectUtil {
    private int fileCount;
    private int javaFileCount;

    public boolean isJavaProject(File projectDirectory, Log log)
    {
        countFiles(projectDirectory);

        double percentJava = (javaFileCount * 100.0) / fileCount;
        if (percentJava >= 50)
        {
            return true;
        }
        else
        {
            return false;
        }


    }

    private void countFiles(File file)
    {
        if (file.isDirectory())
        {
            for (File f : file.listFiles())
            {
                countFiles(f);
            }
        }
        else
        {
            fileCount++; // / only count non-directories
            if (file.getName().endsWith(".java"))
            {
                javaFileCount++;
            }
            else if (file.getName().endsWith(".class"))
            {
                javaFileCount++;
            }
            else
            {
                // / do nothing;
            }
        }

    }
}
