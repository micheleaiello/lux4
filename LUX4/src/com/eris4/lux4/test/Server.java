/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.11 $
*/
package com.eris4.lux4.test;

import com.eris4.lux4.client.CannotConnectException;
import com.eris4.lux4.server.RequestDispatcher;
import com.eris4.lux4.server.HandlerInstantiationException;
import org.apache.log4j.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class);

    public static void main(String[] args)
    {

        int numOfThreads = 10;//todo usare property?
        int portNumber = 3333;//todo usare property?

	    RequestDispatcher rd = null;
	    try {
		    rd = new RequestDispatcher(portNumber, numOfThreads, TestHandler.class);
	    }
	    catch (HandlerInstantiationException e) {
            logger.fatal(e);
            System.exit(-1);
	    }

	    try {
            rd.bind();
        } catch (CannotConnectException e) {
            logger.fatal(e);
            System.exit(-1);
        }

        Thread dispatcherThread = new Thread(rd);
        dispatcherThread.start();


//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//        }
//
//        System.out.println("Shutting down the dispatcher");
//
//        rd.shutdown();

        try {
            dispatcherThread.join();
        } catch (InterruptedException e) {
        }

    }

}