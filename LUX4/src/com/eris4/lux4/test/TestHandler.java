/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/11 13:53:19 $
* $Name:  $ - $Revision: 1.16 $
*/
package com.eris4.lux4.test;

import com.eris4.lux4.server.InitializationException;
import com.eris4.lux4.server.RequestHandler;
import com.eris4.lux4.util.timeutil.TimeUtil;

public class TestHandler extends RequestHandler {
    private String initStr = "A";//todo usare property?
    private double delayMillis = 1;//todo usare property?

//    public void beginBlock(int blocksize) throws HandlerException {
//    }
//
//    public void endBlock() throws HandlerException {
//    }

    public void init(String initializationString) throws InitializationException
    {
//        throw new InitializationException("Wrong Initialization String!");
        initStr = initializationString;
    }

    public void close() {
    }

    public String append(String inputString) {

    	TimeUtil.workForMillis(delayMillis);

	    if (inputString != null) {
            return inputString+initStr;
        }
        else {
            return "~MISSING~"+initStr;
        }
    }

    public String prepend(String inputString) {

	    TimeUtil.workForMillis(delayMillis);

        if (inputString != null) {
            return initStr+inputString;
        }
        else {
            return initStr+"~MISSING~";
        }
    }

    public String concat(String first, String second) {

        TimeUtil.workForMillis(delayMillis);

	    return first+second;
    }

    public String getInit() {

	    TimeUtil.workForMillis(delayMillis);

	    return initStr;
    }
}
