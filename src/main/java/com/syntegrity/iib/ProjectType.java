/*
 * Copyright (C) Syntegrity Solutions Pty Ltd, 2017.
 */
package com.syntegrity.iib;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 
 *
 * @author steve (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2017
 */
public enum ProjectType {
    LIBRARY, SHAREDLIBRARY, APPLICATION, JAVA, BARFILES;

    // Reverse-lookup map for getting a day from an abbreviation
    private static final Map<String, ProjectType> lookup = new HashMap<String, ProjectType>();

    static {
        for (ProjectType d : ProjectType.values()) {
            lookup.put(d.toString(), d);
        }
    }

    public static ProjectType findByStr(String str) {
        return lookup.get(str);
    }
}
