/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.38 $
*/
package com.eris4.lux4;

import java.util.StringTokenizer;
import java.util.PropertyResourceBundle;

public class Version {

    private static String name = "LUX4";
    private static String version = "2.1.1b";
    private static String cvsTag = "$Name:  $";

    private static final String separator = ".";

    public static String getName() {
        int dashIndex = cvsTag.indexOf("-");
        if (dashIndex < 0) {
            return name;
        }
        String tagName = cvsTag.substring(7, dashIndex);
        return tagName;
    }

    public static String getVersion() {
        int dashIndex = cvsTag.indexOf("-");
        if (dashIndex < 0) {
            return version;
        }

        int vstart = (dashIndex+1);
        String tagVersion = cvsTag.substring(vstart, cvsTag.length()-2);
        String vers = tagVersion.replaceAll("_", ".");
        return vers;
    }

    public static String asString() {
        int dashIndex = cvsTag.indexOf("-");
        if (dashIndex < 0) {
            return version;
        }

        int vstart = (dashIndex+1);
        String tagVersion = cvsTag.substring(vstart, cvsTag.length()-2);
        String vers = tagVersion.replace('_', '.');
        return vers;
    }

    public static boolean isCompatible(String anotherVersion) {

        String myVersion = asString();

        try {
            StringTokenizer myTokenizer = new StringTokenizer(myVersion, separator, false);
            if (myTokenizer.countTokens() < 3) {
                return false;
            }

            int majorReleaseNumber = Integer.parseInt(myTokenizer.nextToken());
            int minorReleaseNumber = Integer.parseInt(myTokenizer.nextToken());
//            int patchLevel = Integer.parseInt(myTokenizer.nextToken());

            StringTokenizer tokenizer = new StringTokenizer(anotherVersion, separator, false);
            if (tokenizer.countTokens() < 3) {
                return false;
            }

            int anotherMajor = Integer.parseInt(tokenizer.nextToken());
            int anotherMinor = Integer.parseInt(tokenizer.nextToken());
//            int anotherPatch = Integer.parseInt(tokenizer.nextToken());

            if ((anotherMajor != majorReleaseNumber) || (anotherMinor != minorReleaseNumber)) {
                return false;
            }

        } catch (NumberFormatException e) {
            return false;
        }

        return true;

    }

    public static String getBuildProperties() {
        try {
            PropertyResourceBundle bundle = (PropertyResourceBundle) PropertyResourceBundle.getBundle("LUX4Build");
            String buildNumber = bundle.getString("build.number");
            String buildTimestamp = bundle.getString("build.timestamp");
            String userName = bundle.getString("user.name");
            String osVersion = bundle.getString("os.name");
            String javaVersion = bundle.getString("java.vm.version");

            StringBuffer buff = new StringBuffer(100);

            buff.append("Build Number: "+buildNumber);
            buff.append('\n');
            buff.append("Build Timestamp: "+buildTimestamp);
            buff.append('\n');
            buff.append("User Name:    "+userName);
            buff.append('\n');
            buff.append("OS Version:   "+osVersion);
            buff.append('\n');
            buff.append("Java Version: "+javaVersion);

            return buff.toString();
        } catch (Exception e) {
        }
        return "N/A";
    }

    public static void main(String[] args) {
        final String all = "all";
        final String build = "build";
        final String help = "help";

        if (args.length == 0) {
            System.out.println(getName()+" "+getVersion());
            return;
        }

        if (help.equals(args[0])) {
            System.out.println(getName()+" "+getVersion());
            System.out.println();
            System.out.println("Available options:");
            System.out.println("- all     Prints all information.");
            System.out.println("- build   Prints build information.");
            System.out.println("- help    Prints this message.");
            return;
        }

        if (all.equals(args[0])) {
            System.out.println(getName()+" "+getVersion());
            System.out.println();
            System.out.println("Build Properties: ");
            System.out.println(getBuildProperties());
            return;
        }

        if (build.equals(args[0])) {
            System.out.println(getName()+" "+getVersion());
            System.out.println();
            System.out.println("Build Properties: ");
            System.out.println(getBuildProperties());
            return;
        }
    }

}
