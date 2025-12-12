/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/07 17:58:55 $
* $Name:  $ - $Revision: 1.9 $
*/
package com.eris4.lux4.client.impl;

import java.io.IOException;

import com.eris4.lux4.client.Request;
import com.eris4.lux4.client.InvokerNotConnectedException;
import com.eris4.lux4.client.CannotConnectException;
import com.eris4.lux4.client.ConnectionException;

final class MTAggregatingRequestInvoker extends AbstractRequestInvoker {

    private RequestQueue requestQueue;
    private Carrier[] carrierArray;
    private PacketPool packetPool;


    MTAggregatingRequestInvoker(String hostname, int port, String initializationString, int maxAggregatedCount, int connectionCount) {
        super(hostname, port, initializationString);

        if (maxAggregatedCount <= 1) throw new IllegalArgumentException("maxAggregatedCount <= 1");
        if (connectionCount < 1) throw new IllegalArgumentException("connectionCount < 1");

        constructorHelper(hostname, port, initializationString, maxAggregatedCount, connectionCount);
    }
    private void constructorHelper(String hostname, int port, String initializationString,
                                   int maxRequestPerPacket, int carrierCount) {
        if (maxRequestPerPacket <= 1) throw new IllegalArgumentException("maxRequestPerPacket <= 1");
        if (carrierCount < 1) throw new IllegalArgumentException("carrierCount < 1");

        int maxPacketInQueue = carrierCount * 2;
        packetPool = new PacketPool(maxPacketInQueue + carrierCount, 10, maxRequestPerPacket);
        requestQueue = new RequestQueue(packetPool, maxPacketInQueue);

        carrierArray = new Carrier[carrierCount];
        for (int i = 0 ; i < carrierArray.length ; i++) {
            carrierArray[i] = new Carrier(packetPool, requestQueue,hostname, port, initializationString);
            Thread carrierThread = new Thread(carrierArray[i]);
            carrierThread.setDaemon(true);
            carrierThread.setName("carrier_" + i);
            carrierThread.start();
        }
    }

    protected OperationsData doConnect()
            throws CannotConnectException
    {
        OperationsData out = null;
        for (int i = 0 ; i < carrierArray.length ; i++) {
            out = carrierArray[i].doConnect();
        }
        requestQueue.open();
        //assert out != null : "out == null";
        return out;
    }

    protected void doDisconnect() {
        for (int i = 0 ; i < carrierArray.length ; i++) {
            carrierArray[i].doDisconnect();
        }
    }

    // todo ? request != null, response != null;
    public void invoke(Request request)
            throws InvokerNotConnectedException, ConnectionException
    {
        if (! super.isConnected()) throw new InvokerNotConnectedException();
        //assert (isConnected()) : "! isConnected()";

        synchronized(request){
            //assert request.isRequestSet() : "! request.isRequestSet()";
            //assert ! request.isResponseSet() : "request.isResponseSet()";
            final RequestImpl requestImpl = (RequestImpl) request;
            requestQueue.push(requestImpl);
            while (! request.isResponseSet()){
                try {
                    request.wait();
                }
                catch (InterruptedException e) {
                    ;  //uninterruptable
                }
                final ResponseType responseType = requestImpl.getResponseType();
                if (! responseType.isValid() && ! responseType.isFromServer()) {
                    if (responseType.equals(ResponseType.clientInvokerNotConnectedException)){
                        throw new InvokerNotConnectedException();
                    }
                    else {
                        if (responseType.equals(ResponseType.clientIOException)){
                            throw new ConnectionException(requestImpl.getResponseErrorMessage(),ConnectionException.COMPLETED_MAYBE);
                        }
                        else {
                            if (responseType.equals(ResponseType.clientRuntimeException)){
                                throw new RuntimeException(requestImpl.getResponseErrorMessage());
                            }
                            else {
                                throw new RuntimeException("Unknown response type. responseType = " + responseType);
                            }
                        }
                    }
                }
            }
        }
    }

    //todo finalizer che ammazza i carrier
}
