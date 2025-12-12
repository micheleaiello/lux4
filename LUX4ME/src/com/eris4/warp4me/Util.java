/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 16, 2003.
*/

package com.eris4.warp4me;

import com.eris4.warp4me.RatingValue;

public class Util {
    public static RatingValue parseRatingValue(String toParse){
        if (toParse.equals("null")) {
            return null;
        }
        int separatorIndex=toParse.indexOf(RatingValue.SEPARATOR);
        long amount=0;
        String unit=null;
        String amountString=null;
        if(separatorIndex!=-1){
            amountString=toParse.substring(0,separatorIndex);

            unit=toParse.substring(separatorIndex+1);
        }else{
            amountString=toParse;
        }
        if(amountString.indexOf('.')!=-1){
            amountString=amountString.substring(0,amountString.indexOf('.'));
        }
        amount=Long.parseLong(amountString);
        return new RatingValue(amount,unit);
    }

    public static boolean parseBoolean(String toParse){
        if(toParse.indexOf("true")!=-1){
            return true;
        }else{
            return false;
        }
    }
}
