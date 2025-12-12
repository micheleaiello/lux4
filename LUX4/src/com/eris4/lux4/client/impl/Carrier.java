/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/07 17:58:55 $
* $Name:  $ - $Revision: 1.12 $
*/
package com.eris4.lux4.client.impl;

import java.io.IOException;

import com.eris4.lux4.client.InvokerNotConnectedException;
import com.eris4.lux4.client.CannotConnectException;
import com.eris4.lux4.LUX4;

class Carrier implements Runnable {

	private RequestQueue requestQueue;
	private PacketPool packetPool;
	private STMultiRequestInvoker requestInvoker;

	Carrier(PacketPool packetPool, RequestQueue requestQueue,
	        String hostname, int port, String initializationString)
	{
		this.packetPool = packetPool;
		this.requestQueue = requestQueue;
		requestInvoker = new STMultiRequestInvoker(hostname, port, initializationString);
	}

	public void run() {
		Packet packetToSend;
		while (true) {
			packetToSend = requestQueue.pull();
			deliver(packetToSend);
		}
	}

	private void deliver(Packet packet) {
		synchronized(requestInvoker) {
			int requestCount = packet.getRequestCount();

			try {
				requestInvoker.sendPacket(packet);
			}
			catch (IOException e) {
				fillWithException(e, packet, ResponseType.clientIOException);
				packetPool.put(packet);
				return;
			}
			catch (InvokerNotConnectedException e) {
				fillWithException(e, packet, ResponseType.clientInvokerNotConnectedException);
				packetPool.put(packet);
				return;
			}
			catch (RuntimeException e) {
				fillWithException(e, packet, ResponseType.clientRuntimeException);
				packetPool.put(packet);
				return;
			}

			try {
				for (int i = 0 ; i < requestCount ; i++) {
					//assert packet.getRequest(i).isRequestSet() : "! packet.getRequest(i).isRequestSet()";
					requestInvoker.receiveOne(packet.getRequest(i));
				}
			}
			catch (InvokerNotConnectedException e) {
				fillWithException(e, packet, ResponseType.clientInvokerNotConnectedException);
			}
			catch (IOException e) {
				fillWithException(e, packet, ResponseType.clientIOException);
			}
			catch (RuntimeException e) {
				fillWithException(e, packet, ResponseType.clientRuntimeException);
			}
			finally {
				packetPool.put(packet);
            }
            return;
		}
	}

	private void fillWithException(Exception e, Packet packet, ResponseType responseType) {
		for (int i = 0 ; i < packet.getRequestCount() ; i++) {
			packet.getRequest(i).setResponse(e.getClass().getName() + LUX4.RESPONSE_EXCEPTION_SEPARATOR + e.getMessage(), responseType);
		}
	}

	OperationsData doConnect()
	        throws CannotConnectException
	{
		synchronized(requestInvoker) {
			requestInvoker.connect();
			OperationsData operationsData = new OperationsData(requestInvoker.getOperations().length);
			operationsData.operationNameArray = requestInvoker.getOperations();
			operationsData.numberOfInputParametersArray = requestInvoker.getNumberOfInputParameters();
			return operationsData;
		}
	}

	void doDisconnect()	{
		synchronized(requestInvoker) {
			requestInvoker.disconnect();
		}
	}

}
