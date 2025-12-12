/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.8 $
*/
package com.eris4.lux4.test;

import com.eris4.lux4.client.*;
import org.apache.log4j.Logger;

public class Client_SingleThreadSingleRequest {

    private static final Logger logger = Logger.getLogger(Client_SingleThreadSingleRequest.class);

    public static void main(String[] args)
	{
		String hostname = "localhost";//todo usare property
		int port = 3333;//todo usare property
		String initializationString = "initStr";//todo usare property

		int maxInvokeCount = 0;// <= 0 for no limit//todo usare property

		RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
		try {
				invoker.connect();
		} catch (CannotConnectException e) {
            logger.fatal(e);
            System.exit(-1);
		}

		int operationCount = invoker.getOperations().length;
		int[] numOfInputParams = invoker.getNumberOfInputParameters();

		Request request = Lux4ClientFactory.createRequest();
		int invokeCount = 0;
		while (maxInvokeCount <= 0 || invokeCount < maxInvokeCount) {
			for (int operation = 0 ; operation < operationCount ; operation++) {
				String[] inputParams =  getInputParam(numOfInputParams[operation]);

				request.set(operation, inputParams);
					try {
					invoker.invoke(request);
				}
				catch (InvokerNotConnectedException e) {
                    logger.fatal(e);
                    System.exit(-1);
				}
				catch (ConnectionException e) {
                    logger.fatal(e);
                    System.exit(-1);
				}

				if (request.isResponseValid()) {
					String result = request.getResponseResult();
	//					System.out.println("result = " + result);
				}
				else {
					System.out.println("request.getResponseErrorClassName() = " + request.getResponseErrorClassName());
					System.out.println("request.getResponseErrorMessage() = " + request.getResponseErrorMessage());
				}
				invokeCount++;
			}
		}
    invoker.disconnect();
	}

	private static String[] getInputParam(int numOfInputParams) {
		String[] out = new String[numOfInputParams];
		for (int i = 0 ; i < out.length ; i++) {
			out[i] = "Param" + i;
		}
		return out;
	}
}
