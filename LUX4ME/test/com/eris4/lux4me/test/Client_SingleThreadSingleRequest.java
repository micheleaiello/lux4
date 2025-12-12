/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * User: MK
 * Date: 25-mar-2003
 */
package com.eris4.lux4me.test;

import com.eris4.lux4me.*;

import java.io.IOException;


//todo farne una versione per l'SDK
public class Client_SingleThreadSingleRequest {

	public static void main(String[] args)
	{
		String hostname = "localhost";//todo usare property
		int port = 3333;//todo usare property
		String initializationString = "initStr";//todo usare property

		int maxInvokeCount = 100;// <= 0 for no limit//todo usare property

		RequestInvoker invoker = Lux4ClientFactory.createRequestInvoker(hostname, port, initializationString);
		try {
				invoker.connect();
		} catch (CannotConnectException e) {
				e.printStackTrace();
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
					e.printStackTrace();
					System.exit(-1);
				}
				catch (IOException e) {
					e.printStackTrace();
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
