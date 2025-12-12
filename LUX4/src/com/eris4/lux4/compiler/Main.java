/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/07/16 08:31:19 $
* $Name:  $ - $Revision: 1.1 $
*/

package com.eris4.lux4.compiler;

import com.eris4.lux4.server.RequestHandler;
import org.apache.log4j.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Missing source class name");
            System.exit(-1);
        }
        String sourceClassName = args[0];

        Class sourceClass = null;
        try {
            sourceClass = Class.forName(sourceClassName);
        } catch (ClassNotFoundException e) {
            System.err.println("Unable to find source class. Be sure is in the classpath.");
            System.exit(-1);
        }

        if ( ! RequestHandler.class.isAssignableFrom(sourceClass)){ // modified by MK
            System.err.println("Source class not inherited from RequestHandler.");
            System.exit(-1);
        }

        RequestHandler handler = null;
        try {
            handler = (RequestHandler) sourceClass.newInstance(); // modified by MK
        } catch (InstantiationException e) {
            logger.fatal(e);
            System.exit(-1);
        } catch (IllegalAccessException e) {
            logger.fatal(e);
            System.exit(-1);
        }

        String[] operations = handler.getOperations();
        createProxySource(sourceClass.getPackage().getName(), sourceClass.getName()+"Invoker", operations);
    }

    private static void createProxySource(String packageName, String className, String[] operations) {
        //todo?
    }

}
