/*******************************************************************************
 * Copyright (c) 2012 Tobias Althoff, Ji-Seung Shin, Susanne Könning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *	   Tobias Althoff, Ji-Seung Shin, Susanne Könning - initial implementation
 ********************************************************************************/
package net.sf.csv_export;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    public static String project;
    public static String path;
    public static String location;
    public static String file;
    public static String line;
    public static String textBefore;
    public static String match;
    public static String textAfter;
    public static String not_available;
    public static String error_message_file_opened;

    private static final String BUNDLE_NAME =
            "net.sf.csv_export.messages"; //$NON-NLS-1$
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    private Messages() {}

}
