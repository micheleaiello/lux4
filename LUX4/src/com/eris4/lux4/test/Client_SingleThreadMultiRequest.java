/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.7 $
*/
package com.eris4.lux4.test;

import com.eris4.lux4.client.*;
import org.apache.log4j.Logger;

public class Client_SingleThreadMultiRequest {

    private static final Logger logger = Logger.getLogger(Client_SingleThreadMultiRequest.class);

    public static void main(String[] args)
	{
		String hostname = "localhost";//todo usare property
		int port = 3333;//todo usare property
		String initializationString = "initStr";//todo usare property

		int blockSize = 50;//todo usare property?
		int maxInvokeBlockCount = 0;//todo usare property?
		// maxInvokeBlockCount <= 0 for no limit, total request count = blockSize * maxInvokeBlockCount


		MultiRequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
		try {
				invoker.connect();
		} catch (CannotConnectException e) {
            logger.fatal(e);
                System.exit(-1);
		}

		int operationCount = invoker.getOperations().length;
		int[] numOfInputParams = invoker.getNumberOfInputParameters();

		MultiRequest multiRequest = Lux4ClientFactory.createMultiRequest();

		int operation = 0;
		int invokeBlockCount = 0;
		while (maxInvokeBlockCount <= 0 || invokeBlockCount < maxInvokeBlockCount) {

			multiRequest.empty();

			for (int i = 0 ; i < blockSize ; i++) {
				operation = (operation + 1) % operationCount;
				multiRequest.add(operation,getInputParam(numOfInputParams[operation]));
			}

			try {
				invoker.invoke(multiRequest);
			}
			catch (InvokerNotConnectedException e) {
                logger.fatal(e);
                System.exit(-1);
			}
			catch (ConnectionException e) {
                logger.fatal(e);
                System.exit(-1);
			}

			for (int i = 0 ; i < multiRequest.getRequestCount() ; i++) {
				Request request = multiRequest.get(i);
				if (request.isResponseValid()) {
					String result = request.getResponseResult();
//					System.out.println("result = " + result);
				}
				else {
					System.out.println("request.getResponseErrorClassName() = " + request.getResponseErrorClassName());
					System.out.println("request.getResponseErrorMessage() = " + request.getResponseErrorMessage());
				}
			}
			invokeBlockCount++;
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
