/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.9 $
*/
package com.eris4.lux4.test;

import com.eris4.lux4.client.*;
import com.eris4.lux4.util.timeutil.StopWatch;
import com.eris4.lux4.util.timeutil.TimeUtil;
import org.apache.log4j.Logger;

public class Client_MultiThreadSingleRequest implements Runnable {

    private static final Logger logger = Logger.getLogger(Client_MultiThreadSingleRequest.class);

    private static int[] latencyArray = new int[10000];
	private static final int invokeCountModulusForDump = 100000;
	private static long allThreadRequestProgr = 0;
	private static Object allThreadRequestProgrLock = new Object();
	private static long allThreadResponseProgr = 0;
	private static Object allThreadResponseProgrLock = new Object();
	private static int[] fasterCountArray = new int[1000];
	private static int[] slowerCountArray = new int[1000];
	private static int allThreadInvokeCount;
	private static Object allThreadDumpLock = new Object();

	private long thisThreadRequestProgr;
	private int thisThreadInvokeCount = 0;
	private final StopWatch timer = TimeUtil.makeNewWarmedStopWatch();

	static void dumpAndResetLatencyArray() {
		System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////////");
		int sum = 0;
		double weightedSum = 0;
		int maxLatency = 0;
		int percBoundLatency = 0;
		double percBound = 99;
		int invokeCountBound = (int) ((allThreadInvokeCount * percBound) / 100.0);
		for (int i = 0 ; i < latencyArray.length ; i++) {
			if (latencyArray[i] != 0) {
				maxLatency = i;
				sum += latencyArray[i];
				weightedSum += ((i + 0.5) * latencyArray[i]);
				if (sum >= invokeCountBound && percBoundLatency == 0) {
					percBoundLatency = i;
				}
        latencyArray[i] = 0;
			}
		}
		//assert sum == allThreadInvokeCount : "sum != allThreadInvokeCount";
		System.out.println("Average latency = " + ( weightedSum / (double)allThreadInvokeCount) + " ms");
		System.out.println("For " + percBound + "% of requests latency < " + percBoundLatency + " ms");
		System.out.println("Max Latency = " + maxLatency + " ms");
		allThreadInvokeCount = 0;

		for (int i = 0 ; i < fasterCountArray.length ; i++) {
			int count = fasterCountArray[i];
			if (count != 0) {
				System.out.println("faster[" + i + "] = " + count);
				fasterCountArray[i] = 0;
			}
			count = slowerCountArray[i];
			if (count != 0) {
				System.out.println("           slower[" + i + "] = " + count);
				slowerCountArray[i] = 0;
			}
		}
	}


	private RequestInvoker invoker;
	private int maxInvokeCount;
	private double clientDelay;

	Client_MultiThreadSingleRequest(RequestInvoker invoker, int maxInvokeCount, double clientDelay) {
		this.invoker = invoker;
		this.maxInvokeCount = maxInvokeCount;
		this.clientDelay = clientDelay;
	}

	public void run() {

		try {
			invoker.connect();
		}
		catch (CannotConnectException e) {
            logger.fatal(e);
            System.exit(-1);
		}


		int operationCount = invoker.getOperations().length;
		int[] numOfInputParams = invoker.getNumberOfInputParameters();

		Request request = Lux4ClientFactory.createRequest();

		int operation = 0;
		while (maxInvokeCount <= 0 || thisThreadInvokeCount < maxInvokeCount) {
			TimeUtil.workForMillis(clientDelay);
			operation = (operation + 1) % operationCount;
			String[] inputParams =  getInputParam(numOfInputParams[operation]);

			request.set(operation, inputParams);

			beforeInvoke();
			try {
				invoker.invoke(request);
			}
			catch (InvokerNotConnectedException e) {
                logger.fatal(e);
                System.exit(-1);
			}
			catch (ConnectionException  e) {
                logger.fatal(e);
                System.exit(-1);
			}
			afterInvoke();

			if (request.isResponseValid()) {
				String result = request.getResponseResult();
//				System.out.println("result = " + result);
			}
			else {
				System.out.println("request.getResponseErrorClassName() = " + request.getResponseErrorClassName());
				System.out.println("request.getResponseErrorMessage() = " + request.getResponseErrorMessage());
			}
			thisThreadInvokeCount++;
		}
		invoker.disconnect();
	}

	private void beforeInvoke() {
		synchronized(allThreadRequestProgrLock) {
			thisThreadRequestProgr = allThreadRequestProgr++;
		}
		timer.start ();
	}

	private void afterInvoke() {
		timer.stop ();
		int diff;
		synchronized(allThreadResponseProgrLock) {
			diff = (int) (thisThreadRequestProgr - allThreadResponseProgr++);
		}
		synchronized (allThreadDumpLock) {
			if (diff >= 0) {
				try {
					fasterCountArray[diff]++;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					fasterCountArray[fasterCountArray.length - 1]++;
				}
			}
			else {
				try {
					slowerCountArray[- diff]++;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					slowerCountArray[slowerCountArray.length - 1]++;
				}
			}

			allThreadInvokeCount++;
			try {
				latencyArray[(int)Math.floor(timer.getDuration ())]++;
			}
			catch (ArrayIndexOutOfBoundsException e) {
				latencyArray[latencyArray.length - 1]++;
			}

			if ((allThreadInvokeCount % invokeCountModulusForDump) == 0) {
				dumpAndResetLatencyArray();
			}
		}
	}

	private static String[] getInputParam(int numOfInputParams) {
		String[] out = new String[numOfInputParams];
		for (int i = 0 ; i < out.length ; i++) {
			if (i == 0){
				out[i] = Thread.currentThread().getName();
			}
			else{
			  out[i] = "Param" + i;
			}
		}
		return out;
	}



	public static void main(String[] args) {

		String hostname = LUX4TestClientProperties.getString("HostName", "localhost");
		int port = LUX4TestClientProperties.getInt("Port", 3333);
		String initializationString = LUX4TestClientProperties.getString("InitializationString", "initStr");

		int connectionCount = LUX4TestClientProperties.getInt("ConnectionCount", 4);
		boolean aggregating = LUX4TestClientProperties.getBoolean("Aggregating", true);

		int maxRequestPerPacket = LUX4TestClientProperties.getInt("MaxRequestPerPacket", 12);

		int numOfClientThread = LUX4TestClientProperties.getInt("NumOfClientThread", 64);
		double clientDelay = LUX4TestClientProperties.getDouble("ClientDelay", 0.1);
		int maxInvokeCountPerThread = LUX4TestClientProperties.getInt("MaxInvokeCountPerThread", 0);// <= 0 for no limit

		RequestInvoker invoker;
		if (aggregating) {
			  invoker = Lux4ClientFactory.createMultiThreadAggregatingRequestInvoker(hostname, port, initializationString, maxRequestPerPacket, connectionCount);

		}
		else {
			invoker = Lux4ClientFactory.createMultiThreadConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
		}

//		try {
//			invoker.connect();
//		}
//		catch (CannotConnectException e) {
//			System.exit(-1);
//		}
//
		Thread[] pool = new Thread[numOfClientThread];
		for (int i = 0 ; i < numOfClientThread ; i++) {
			Client_MultiThreadSingleRequest client = new Client_MultiThreadSingleRequest(invoker, maxInvokeCountPerThread, clientDelay);
			pool[i] = new Thread(client);
			pool[i].start();
		}

//		for (int i = 0 ; i < numOfClientThread ; i++) { //waits for all threads to die
//			try {
//				pool[i].join();
//			}
//			catch (InterruptedException e) {
//				;  // Interrupted is ok
//			}
//		}
//
//		invoker.disconnect();
	}


}

