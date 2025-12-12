/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* User: Michele Aiello
* Date: 31-ago-2002
*/
package com.eris4.lux4;


public class Version {

    private static String untaggedVersion = "2.0.1a";
    private static String cvsTag = "$Name:  $";

    private static final String separator = ".";

    public static String asString() {
        int dashIndex = cvsTag.indexOf("-");
        if (dashIndex < 0) {
            return untaggedVersion;
        }

        int vstart = (dashIndex+1);
        String tagVersion = cvsTag.substring(vstart, cvsTag.length()-2);
        String vers = tagVersion.replace('_', '.');
        return vers;
    }

    public static boolean isCompatible(String anotherVersion) {

        String myVersion = asString();

        try {
            StringTokenizer myTokenizer = new StringTokenizer(myVersion, separator);
            if (myTokenizer.countTokens() < 3) {
                return false;
            }

            int majorReleaseNumber = Integer.parseInt(myTokenizer.nextToken());
            int minorReleaseNumber = Integer.parseInt(myTokenizer.nextToken());
//            int patchLevel = Integer.parseInt(myTokenizer.nextToken());

            StringTokenizer tokenizer = new StringTokenizer(anotherVersion, separator);
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

    public static void main(String[] args) {
        System.out.println("LUX4 "+asString());
    }

}
