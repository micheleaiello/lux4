/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 16, 2003.
*/

package com.eris4.warp4me;

import com.eris4.warp4me.RatingValue;

public class RatingValueUtil {
    public static RatingValue parse(String toParse){
        if (toParse.equals("null")) {
            return null;
        }
        int separatorIndex=toParse.indexOf(RatingValue.SEPARATOR);
        long amount=0;
        String unit=null;
        if(separatorIndex!=-1){
            String amountString=toParse.substring(0,separatorIndex);
            amount=Long.parseLong(amountString);
            unit=toParse.substring(separatorIndex+1);
        }else{
            amount=Long.parseLong(toParse);
        }
        return new RatingValue(amount,unit);
    }
}
