/*
 * Copyright (C) Syntegrity Solutions Pty Ltd, 2017.
 */
package com.syntegrity.iib;

/**
 *
 * Enum holds the fully qualified nature name by type
 *
 * @author steve (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2017
 */
public enum NatureType {
    APPLICATION("com.ibm.etools.msgbroker.tooling.applicationNature"), LIBRARY("com.ibm.etools.msgbroker.tooling.libraryNature"), SHAREDLIBRARY(
            "com.ibm.etools.msgbroker.tooling.sharedLibraryNature"), BARFILES("com.ibm.etools.mft.bar.ext.barnature"), JAVA("org.eclipse.jdt.core.javanature");

    private String fullname;

    private NatureType(String name) {
        fullname = name;
    }

    public String getFullName() {
        return fullname;
    }
}
