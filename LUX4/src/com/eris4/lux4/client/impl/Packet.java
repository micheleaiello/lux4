/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.4 $
*/
package com.eris4.lux4.client.impl;

class Packet {

	private int maxRequestPerPacket;
	private int currRequestCount;
	private RequestImpl[] requestArray;

	Packet(int maxRequestCount) {
		this.maxRequestPerPacket = maxRequestCount;
		requestArray = new RequestImpl[maxRequestCount];
		empty();
	}

	synchronized void empty() {
		currRequestCount = 0;
	}

	synchronized boolean isEmpty() {
		return (currRequestCount == 0);
	}

	synchronized boolean isFull() {
		return (currRequestCount == maxRequestPerPacket);
	}

	synchronized int getFreeCount() {
		return maxRequestPerPacket - currRequestCount;
	}

	synchronized int getRequestCount() {
		return currRequestCount;
	}

	synchronized int getMaxRequestPerPacket() {
		return maxRequestPerPacket;
	}

	synchronized void addRequest(RequestImpl request) {
		//assert ! isFull() : "Paket is full at addRequest beginning";
		//assert currRequestCount > 0 || request.getTimeStamp() != 0.0 :"Timestamp not set in first request of packet";
		requestArray[currRequestCount] = request;
		currRequestCount ++;
	}

	synchronized void addRequests(RequestImpl[] requestArray, int newRequestCount) {
		//assert requestArray.length >= newRequestCount : "requestArray.length < newRequestCount";
		//assert getFreeCount() >= newRequestCount : "getFreeCount() < newRequestCount";
		System.arraycopy(requestArray, 0, this.requestArray, currRequestCount, newRequestCount);
		currRequestCount += newRequestCount;
	}

	synchronized int getRequestImplArray(RequestImpl[] out) {
		//assert out.length >= currRequestCount : "out.length < currRequestCount";
		for (int i = 0 ; i < currRequestCount ; i++) {
			out[i] = requestArray[i];
		}
		return currRequestCount;
	}

	synchronized RequestImpl getRequest(int index) {
		//assert index >= 0 : "index < 0";
		//assert index < currRequestCount : "index >= currRequestCount";
		return requestArray[index];
	}

	synchronized double getOldestTimeStamp() {
		return requestArray[0].getTimeStamp();
	}

}
