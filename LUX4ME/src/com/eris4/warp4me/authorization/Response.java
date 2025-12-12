/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 11, 2003.
*/

package com.eris4.warp4me.authorization;

import com.eris4.warp4me.RatingValue;
import com.eris4.warp4me.Util;

public class Response {

    private RatingValue chargedAmount;
    private RatingValue chunkSize;
    private boolean isLast = false;

    public Response(RatingValue charged, RatingValue chunk, boolean isLast) {
        this.chargedAmount = new RatingValue(charged);
        this.chunkSize = new RatingValue(chunk);
        this.isLast = isLast;
    }

    public Response(String csvResponse) {
        int commaIndex = csvResponse.indexOf(',');

        if(commaIndex == -1){
            chargedAmount = new RatingValue(0, null);
            chunkSize = new RatingValue(0, null);
        }
        else{
            String chargedString = csvResponse.substring(0, commaIndex);
            chargedAmount = Util.parseRatingValue(chargedString);

            int commaIndex2 = csvResponse.indexOf(',', commaIndex+1);
            if (commaIndex2 == -1) {
                chargedAmount = new RatingValue(0, null);
                chunkSize = new RatingValue(0, null);
            }
            else {
                String chunkString = csvResponse.substring(commaIndex+1, commaIndex2);
                chunkSize = Util.parseRatingValue(chunkString);
                String remainingString = csvResponse.substring(commaIndex2+1);
                isLast=Util.parseBoolean(remainingString);
            }
        }
    }

    public RatingValue getChargedAmount() {
        return chargedAmount;
    }

    public RatingValue getChunkSize() {
        return chunkSize;
    }

    public boolean isLast() {
        return isLast;
    }

    public String toString(){
        StringBuffer buff=new StringBuffer();
        if (chargedAmount != null) {
            buff.append(chargedAmount.toString());
        }
        else {
            buff.append("null");
        }
        buff.append(',');
        if (chunkSize != null) {
            buff.append(chunkSize.toString());
        }
        else {
            buff.append("null");
        }
        buff.append(',');
        buff.append(isLast);
        return buff.toString();
    }

}
