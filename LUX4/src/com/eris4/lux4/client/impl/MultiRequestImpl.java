/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.3 $
*/
package com.eris4.lux4.client.impl;

import java.util.Vector;

import com.eris4.lux4.client.MultiRequest;
import com.eris4.lux4.client.Request;

class MultiRequestImpl implements MultiRequest{

	private final int initialCapacity = 10;
	private final int increment = 10;

	private Vector requestVector;
	private int requestCount;

	MultiRequestImpl() {
		requestVector = new Vector(initialCapacity, increment);
		requestCount = 0;
	}

	public void set(int[] operationArray, String[][] inputParamsArray)
	        throws IllegalArgumentException
	{
		if ((operationArray.length != inputParamsArray.length) || (operationArray.length == 0)) {
			throw new IllegalArgumentException("operationArray.length = " + operationArray.length + " inputParamsArray.length = " + inputParamsArray.length);
		}
		requestCount = operationArray.length;
		ensureCapacity(requestCount);
		for (int i = 0 ; i < operationArray.length ; i++) {
			((Request) requestVector.get(i)).set(operationArray[i], inputParamsArray[i]);
		}
	}

	public void add(int operation, String[] inputParams) {
		requestCount++;
		ensureCapacity(requestCount);
		((Request) requestVector.get(requestCount - 1)).set(operation, inputParams);
	}

	public Request get(int index) {
		if (index > requestCount) throw new ArrayIndexOutOfBoundsException(index);
		return (Request) requestVector.get(index);
	}

	public int getRequestCount() {
		return requestCount;
	}

	public boolean isSet() {
		return (requestCount > 0);
	}

	public void empty() {
		requestCount = 0;
	}

	private void ensureCapacity(int requestCount) {
		while (requestVector.size() < requestCount) {
			requestVector.add(new RequestImpl());
		}
	}

}
