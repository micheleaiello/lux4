/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * User: MK
 * Date: 17-mar-2003
 */
package com.eris4.lux4me.impl;


class PacketPool {

	private int maxRequestPerPacket;
	private int chunkSize;
	private int totalInstantiated;
	private int currInPool;
	private Packet[] packetArray;

	PacketPool(int initialSize, int chunkSize, int maxRequestPerPacket) {
		this.maxRequestPerPacket = maxRequestPerPacket;
		this.chunkSize = chunkSize;
		totalInstantiated = initialSize;
		currInPool = initialSize;
		packetArray = new Packet[initialSize];
		for (int i = 0 ; i < packetArray.length ; i++) {
			packetArray[i] = new Packet(maxRequestPerPacket);
		}
	}

	synchronized void put(Packet packet) {
		if (currInPool == packetArray.length){
			Packet[] newPacketArray = new Packet[packetArray.length + chunkSize] ;
			System.arraycopy(packetArray, 0, newPacketArray, 0, currInPool);
			packetArray = newPacketArray;
		}
		packetArray[currInPool] = packet;
		currInPool++;
	}
	
	synchronized Packet get() {
		if (currInPool > 0){
			currInPool--;
			packetArray[currInPool].empty();
			return packetArray[currInPool];
		}
		else {
			totalInstantiated++;
			return new Packet(maxRequestPerPacket);
		}
	}

	int getMissingCount() {
		return totalInstantiated - currInPool;
	}

}
