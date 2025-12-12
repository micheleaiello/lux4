/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Sep 24, 2003
*/
package com.eris4.lux4;

public class StringTokenizer {
    String toTokenize;
    String separator;
    int numTokens=-1;

    public StringTokenizer(String toTokenize, String separator) {
        this.toTokenize = toTokenize;
        this.separator = separator;
    }

    public int countTokens() {
        if(numTokens==-1){
            int pos=toTokenize.indexOf(separator);
            if(pos!=-1){
                numTokens=1;
                while(pos!=-1){
                   pos=toTokenize.indexOf(separator,pos+1);
                    numTokens++;
                }
            } else{
                numTokens=0;
            }
        }
        return numTokens;
    }

    public String nextToken() {
        return null;
    }


}
