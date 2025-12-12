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

/**
 * Available from version 2.0.
 * An MTConnectionPoolRequestInvoker can handle invoke requests from various concurrent threads.
 * If the number of invoking threads is hi the comunication overhead of each request can be lowered
 * using an aggregating request invoker. @see com.eris4.lux4.client.impl.MTAggregatingRequestInvoker
 */
final class MTConnectionPoolRequestInvoker extends AbstractRequestInvoker {//todo synchronize each invoker between connect-disconnect and invoke

    private InvokerPool invokerPool;

    /**
     * Default constructor for the class MTConnectionPoolRequestInvoker.
     * @param connectionCount The maximum number of client thread that can be served cuncurrently.
     */
    MTConnectionPoolRequestInvoker(String hostname, int port, String initializationString, int connectionCount) {
        super(hostname, port, initializationString);
        if (connectionCount <= 0) throw new IllegalArgumentException("maxInvokerCount <= 0");
        this.invokerPool = new InvokerPool(connectionCount, hostname, port, initializationString);
    }

    protected OperationsData doConnect() throws CannotConnectException{
        return invokerPool.doConnect();
    }

    protected void doDisconnect() {
        invokerPool.doDisconnect();
    }

    public void invoke(Request request)
            throws InvokerNotConnectedException, ConnectionException
    {
        if (!super.isConnected()) throw new InvokerNotConnectedException();
        STMultiRequestInvoker invoker = invokerPool.get();
        try {
            invoker.invoke(request);
        }
        finally {
            invokerPool.put(invoker);
        }
    }

    private class InvokerPool {

        private int maxInvokerCount;
        private volatile int currInvokerCount;
        private STMultiRequestInvoker[] invokerArray;

        InvokerPool(int invokerCount, String hostname, int port, String initializationString) {
            maxInvokerCount = invokerCount;
            currInvokerCount = invokerCount;
            invokerArray = new STMultiRequestInvoker[maxInvokerCount];
            for (int i = 0 ; i < invokerArray.length ; i++) {
                invokerArray[i] = new STMultiRequestInvoker(hostname, port, initializationString);
            }
        }

        public OperationsData doConnect()
                throws CannotConnectException
        {
            for (int i = 0; i < invokerArray.length; i++ ){
                invokerArray[i].connect();
            }
            return invokerArray[0].getOperationsData();
        }

        public void doDisconnect(){
            for (int i = 0; i < invokerArray.length; i++ ){
                invokerArray[i].disconnect();
            }
        }

        void put(STMultiRequestInvoker invokerToRelease) {
            synchronized(invokerArray){
                for (int i = 0 ; i < invokerArray.length ; i++) {
                    if(invokerArray[i] == null) {
                        invokerArray[i] = invokerToRelease;
                        currInvokerCount++;
                        invokerArray.notify();
                        return;
                    }
                }
            }
            //assert false;
        }

        STMultiRequestInvoker get() {
            synchronized(invokerArray){
                while(currInvokerCount <= 0){
                    try {
                        invokerArray.wait();
                    }
                    catch (InterruptedException e) {
                        ;  //uninterruptable
                    }
                }
                for (int i = 0 ; i < invokerArray.length ; i++) {
                    if(invokerArray[i] != null) {
                        STMultiRequestInvoker out = invokerArray[i];
                        invokerArray[i] = null;
                        currInvokerCount--;
                        return out;
                    }
                }
            }
            //assert false;
            return null;
        }

    }

}
