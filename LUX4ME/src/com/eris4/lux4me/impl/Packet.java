/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * User: MK
 * Date: 17-mar-2003
 */
package com.eris4.lux4me.impl;

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
		requestArray[currRequestCount] = request;
		currRequestCount ++;
	}

	synchronized void addRequests(RequestImpl[] requestArray, int newRequestCount) {
		System.arraycopy(requestArray, 0, this.requestArray, currRequestCount, newRequestCount);
		currRequestCount += newRequestCount;
	}

	synchronized int getRequestImplArray(RequestImpl[] out) {
		for (int i = 0 ; i < currRequestCount ; i++) {
			out[i] = requestArray[i];
		}
		return currRequestCount;
	}

	synchronized RequestImpl getRequest(int index) {
		return requestArray[index];
	}

	synchronized long getOldestTimeStamp() {
		return requestArray[0].getTimeStamp();
	}

}
