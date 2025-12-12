/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.17 $
*/
package com.eris4.lux4.server;

import com.eris4.lux4.Version;
import com.eris4.lux4.client.CannotConnectException;
import com.eris4.lux4.util.LUX4Properties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

/**
 * The RequestDispatcher class is used to implement the server side of a LUX4 client-server
 * application. A LUX4 server can use any number of RequestDispatcher, but each dispatcher
 * must use a different port.
 */
public class RequestDispatcher implements Runnable {

    static private Logger logger = Logger.getLogger(RequestDispatcher.class);

    // Reconnection parameters, sleepTime between retries and number of retries.
    static private final int sleepTime = 1000;
    static private final int numOfRetry = 10;

    private int portNumber;
    private ServerSocket serverSocket;
    private Vector receiversPool;

    private boolean shutdown = false;

    private int serverSocketSoTimeout = 1000;
    private int clientSocketSoTimeout = 0;

    /**
     * Constructor for the RequestDispatcher class.
     * This constructor creates a new RequestDispatcher. The RequestDispatcher is resposible
     * for the creation of a number of Receivers and for the creation of the RequestHandlers used
     * by the receivers.
     *
     * @param port the port used by the Dispatcher.
     * @param numOfReceivers the number of Receivers that the Dispatcher allocates for
     * the cuncurrent handling of incoming requests.
     * @param handlersClass a class implementing the RequestHandler interface. Instances of this
     * class will have to handle each incoming request.
     */
    public RequestDispatcher(int port, int numOfReceivers, Class handlersClass) throws HandlerInstantiationException {

        logger.debug("Creating a RequestDispatcher.");
        logger.debug("Port number = "+port);
        logger.debug("Number of Receivers = "+numOfReceivers);
        logger.debug("Handlers Class = "+handlersClass.getName());

        logger.debug("Checking Handlers Class.");
	      if ( ! RequestHandler.class.isAssignableFrom(handlersClass)){ // modified by MK
		      logger.error("The supplied handlers class does't implements the RequestHandler interface. Raising an exception");
		      throw new HandlerInstantiationException("handlersClass doesn't extend RequestHandler. handlersClass = " + handlersClass.getName());
		    }
        logger.debug("OK. Handlers Class implements RequestHandler.");

        portNumber = port;
        receiversPool = new Vector();

        RequestReceiver[] receivers = new RequestReceiver[numOfReceivers];
        for (int i = 0; i < numOfReceivers; i++) {
            RequestHandler rh = null;
            try {
                rh = (RequestHandler)handlersClass.newInstance();
            } catch (InstantiationException e) {
	            throw new HandlerInstantiationException("InstantiationException - Empty constructor not found. handlersClass = " + handlersClass.getName());
            } catch (IllegalAccessException e) {
	            throw new HandlerInstantiationException("IllegalAccessException - Empty constructor is not accessible. handlersClass = " + handlersClass.getName());
            }
            RequestReceiver rr = RequestReceiver.createRequestReceiver(rh);
            receiversPool.addElement(rr);
            receivers[i] = rr;
        }

        try {
            String serverSocketSoTimeoutStr = LUX4Properties.getProperty("ServerSocketSoTimeout");
            if ((serverSocketSoTimeoutStr != null) && (!serverSocketSoTimeoutStr.equals(""))) {
                serverSocketSoTimeout = Integer.parseInt(serverSocketSoTimeoutStr);
                logger.debug("Server Socket SoTimeout = "+serverSocketSoTimeout);
            }
        } catch (Throwable e) {
            serverSocketSoTimeout = 1000;
            logger.debug("Using default Server Socket SoTimeout = "+serverSocketSoTimeout);
        }

        try {
            String clientSocketSoTimeoutStr = LUX4Properties.getProperty("ClientSocketSoTimeout");
            if ((clientSocketSoTimeoutStr != null) && (!clientSocketSoTimeoutStr.equals(""))) {
                clientSocketSoTimeout = Integer.parseInt(clientSocketSoTimeoutStr);
                logger.debug("Client Socket SoTimeout = "+clientSocketSoTimeout);
            }
        } catch (Throwable e) {
            clientSocketSoTimeout = 0;
            logger.debug("Using default Client Socket SoTimeout = "+clientSocketSoTimeout);
        }

        String perfViewerStr = LUX4Properties.getProperty("PerformanceViewer");
        boolean perfViewer = Boolean.valueOf(perfViewerStr).booleanValue();
        logger.debug("PerformanceViewer = "+perfViewer);

        if (perfViewer) {
            logger.debug("Creating a Preformance Viewer");
            PerformanceViewer.createPerformanceViewer(receivers);
        }
        else {
            logger.debug("Preformance Viewer not active.");
        }
    }

    /**
     * This method is used to bind the request dispatcher to a port.
     * When the dispatcher has been bind to a port the eventLoop method has to
     * be called in order to dispatch the incoming requests to the handlers.
     *
     * @throws CannotConnectException if the bind can't take place (i.e. address already in use).
     */
    public void bind()
            throws CannotConnectException
    {
        unbind();
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            logger.error("Can't open server socket on port "+portNumber, e);
            throw new CannotConnectException();
        }
        logger.info("Dispatcher ready on port "+portNumber);
        logger.debug("Communication Protocol Version = "+Version.asString());

        if (serverSocketSoTimeout > 0) {
            logger.debug("Setting Server Socket SoTimeout to "+serverSocketSoTimeout+"ms");
            try {
                serverSocket.setSoTimeout(serverSocketSoTimeout);
            } catch (SocketException e) {
                logger.warn("Exception while setting SoTimeout for the server socket", e);
            }
        }

        logger.info("Dispatcher ready on port "+portNumber);
        System.out.println("Dispatcher ready on port "+portNumber);
    }

    /**
     * This method is used to unbind the request dispatcher and to release all the used resources.
     */
    public void unbind() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.warn("Exception while closing server socket", e);
            }
            serverSocket = null;
        }
    }

    /**
     * Tell the dispatcher to close the open connections and stop the event loop.
     */
    public void shutdown() {
        logger.debug("Shutdown called.");
        shutdown = true;

        logger.debug("Shutting down all the receivers.");
        int numOfRec = receiversPool.size();
        for (int i = 0; i < numOfRec; i++) {
            RequestReceiver rec = (RequestReceiver) receiversPool.get(i);
            rec.shutdown();
        }

        String perfViewerStr = LUX4Properties.getProperty("PerformanceViewer");
        boolean perfViewer = Boolean.valueOf(perfViewerStr).booleanValue();

        if (perfViewer) {
            logger.debug("Shutting down the Preformance Viewer");
            PerformanceViewer.shutdownPerformanceViewer();
        }
        else {
            logger.debug("Preformance Viewer not active.");
        }

    }

    /**
     * This method is used to start the dispatcher. When started the dispatcher will accept incoming
     * requests and dispatch them to the receivers.
     * The policy used to dispatch the requests is a simple round robin.
     */
    public void run() {
        int receiverIndex = 0;
        int receiversPoolSize = receiversPool.size();
        while (true) {
            try {
                Socket clientSocket = null;
                clientSocket = serverSocket.accept();

                if (shutdown) {
                    logger.debug("Shutdown = true. Breaking the loop.");
                    break;
                }

                logger.debug("Request accepted!");

                logger.debug("Dispatching Request.");
                boolean handling = false;
                int initialIndex = receiverIndex;
                int loopNum = 0;

                while (!handling) {
                    RequestReceiver requestReceiver = (RequestReceiver)receiversPool.get(receiverIndex);
                    receiverIndex = (receiverIndex + 1) % receiversPoolSize;
                    if (requestReceiver.isReady()) {
                        handling = true;
                        if (clientSocketSoTimeout > 0) {
                            logger.debug("Setting client socket SoTimeout to "+clientSocketSoTimeout+"ms");
                            try {
                                clientSocket.setSoTimeout(clientSocketSoTimeout);
                            } catch (SocketException e) {
                                logger.warn("Exception while setting SoTimeout for client socket", e);
                            }
                        }
                        clientSocket.setTcpNoDelay(true);
                        requestReceiver.handle(clientSocket);
                    }
                    else {
                        Thread.yield();
                        if (receiverIndex == initialIndex) {
                            logger.debug("Can't dispatch the request. All Receivers are busy.");
                            loopNum++;
                            if (loopNum >= numOfRetry) {
                                logger.warn("Total number of tries exceted. Closing the connection.");
                                handling = true;
                                clientSocket.close();
                                logger.warn("Connection refused (too many clients) from: "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
                            }
                            else {
                                try {
                                    Thread.sleep(sleepTime);
                                } catch (InterruptedException e) {
                                    logger.debug("Interrupted.", e);
                                    if (shutdown) {
                                        logger.debug("Shutdown = true. Breaking the loop.");
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }
            } catch (java.net.SocketTimeoutException e) {
                // Don't log! This is only a Timeout on the accept of the server socket.
                if (shutdown) {
                    logger.debug("Shutdown = true. Breaking the loop.");
                    break;
                }
            } catch (java.net.SocketException e) {
                logger.warn("SocketException", e);
                if (shutdown) {
                    logger.debug("Shutdown = true. Breaking the loop.");
                    break;
                }
            } catch (IOException e) {
                //TODO Exception handling
                logger.warn("IOException", e);
                if (shutdown) {
                    logger.debug("Shutdown = true. Breaking the loop.");
                    break;
                }
            }
            if (shutdown) {
                logger.debug("Shutdown = true. Breaking the loop.");
                break;
            }

        }

        unbind();
        logger.debug("Exit from run.");
    }

}

