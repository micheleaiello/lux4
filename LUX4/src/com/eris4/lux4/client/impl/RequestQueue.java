/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.6 $
*/
package com.eris4.lux4.client.impl;

class RequestQueue {

	private Object pusherLock = new Object();
	private Object pullerLock = new Object();
	private Object everybodyLock = new Object();

	private PacketPool packetPool;
	private boolean open;
	private Packet[] packetArray;
	private int packetToSendIndex;
	private int packetToFillIndex;
	private int packetInQueueCount;

	RequestQueue(PacketPool packetPool, int maxPacketInQueue) {
		this.packetPool = packetPool;
		open = false;
		packetArray = new Packet[maxPacketInQueue];
		packetToSendIndex = 0;
		packetToFillIndex = 0;
		packetArray[packetToFillIndex] = packetPool.get();
		packetInQueueCount = 1;
	}

	void push(RequestImpl requestImpl) {
		synchronized(pusherLock){
			synchronized(everybodyLock){
				//assert open : "Not open";
				requestImpl.setTimeStamp();
				while(packetArray[packetToFillIndex].isFull()){
					try {
						everybodyLock.wait();//only this pusher can be in wait
					}
					catch (InterruptedException e) {
						;  //uninterruptable
					}
				}
				packetArray[packetToFillIndex].addRequest(requestImpl);

				if (  packetArray[packetToFillIndex].isFull()
				   && packetInQueueCount != packetArray.length
				   ){ //add empty packet
					int nextPacketToFillIndex = (packetToFillIndex + 1) % packetArray.length;
					packetArray[nextPacketToFillIndex] = packetPool.get();
					packetToFillIndex = nextPacketToFillIndex;
					packetInQueueCount++;
				}
				everybodyLock.notify(); //at most one puller can be in wait, no pusher can
			}
		}
	}

	synchronized Packet pull() {
		synchronized(pullerLock){
			synchronized(everybodyLock){

				while(isEmpty()){
					try {
						everybodyLock.wait();//only this puller can be in wait
					}
					catch (InterruptedException e) {
						;  //uninterruptable
					}
				}
				//assert ! packetArray[packetToSendIndex].isEmpty():"packetArray[packetToSendIndex].isEmpty() at sending beginning";

				Packet out = packetArray[packetToSendIndex];

				if (packetToSendIndex == packetToFillIndex){ //only one paket in the queue
					packetArray[packetToFillIndex] = packetPool.get();
				}
				else {
					if (isFull()){
						packetToSendIndex = (packetToSendIndex + 1) % packetArray.length;
						packetToFillIndex = (packetToFillIndex + 1) % packetArray.length;
						packetArray[packetToFillIndex] = packetPool.get();

					}
					else {
						packetToSendIndex = (packetToSendIndex + 1) % packetArray.length;
						packetInQueueCount--;
					}
				}
				everybodyLock.notify();//at most one pusher can be in wait, no puller can
				return out;
			}
		}
	}

	boolean isFull(){
		synchronized(everybodyLock){
		  return packetInQueueCount == packetArray.length && packetArray[packetToFillIndex].isFull();
		}
	}

	boolean isEmpty(){
		synchronized(everybodyLock){
		  return (packetInQueueCount == 1) && packetArray[packetToFillIndex].isEmpty();
		}
	}

  boolean isOpen() {
		synchronized(everybodyLock){
		  return open;
		}
  }
	void open() {
		synchronized(everybodyLock){
			//assert ! open : "Already open";
			open = true;
		}
	}
	void close() {
		synchronized(everybodyLock){
			//assert open : "Not open";
			open = false;
	  }
	}

}
