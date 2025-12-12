/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.30 $
*/
package com.eris4.lux4.server;

import com.eris4.lux4.Version;
import com.eris4.lux4.LUX4;
import com.eris4.lux4.util.LUX4Properties;
import com.eris4.lux4.client.OperationNotExistent;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is resposible for the receiving of requests from a client and for the dispatching to a
 * Request Handler. This class handles all communications handshakes and performance issues.
 */
class RequestReceiver implements Runnable {

    static private Logger logger = Logger.getLogger(RequestReceiver.class);

    private Socket clientSocket;
    private boolean ready;
    private long eventsHandled;
    private RequestHandler handlerDelegate;
    private String[] delegateOperations;

    private boolean yield = false;

    private boolean shutdown = false;

    private RequestReceiver(RequestHandler handlerDelegate) {
        logger.debug("Creating a RequestReceiver");

        String yieldStr = LUX4Properties.getProperty("Yield");
        if (yieldStr != null) {
            try {
                yield = Boolean.valueOf(yieldStr).booleanValue();
            } catch (Exception e) {
                yield = false;
            }
        }
        logger.debug("Yield = "+String.valueOf(yield));

        ready = true;
        eventsHandled = 0;
        this.handlerDelegate = handlerDelegate;
        logger.debug("Handler Delegate class = "+handlerDelegate.getClass().getName());

        logger.debug("Discovering operations");
        delegateOperations = handlerDelegate.getOperations();
        if (delegateOperations.length > 0) {
            logger.debug("Handler Delegate has "+delegateOperations.length+" operations.");
            logger.debug("List all the operations:");
            for (int i = 0; i < delegateOperations.length; i++) {
                logger.debug("\t"+i+") "+delegateOperations[i]+" ("+handlerDelegate.getNumberOfInputParameters(i)+" parameters)");
            }
        }
        else {
            logger.fatal("Handler Class does not implement any operation. Exiting");
            System.exit(-1);
        }
    }

    /**
     * This method is used to create a new Request Receiver.
     *
     * @param handlerDelegate the delegate assiged to this receiver.
     * @return the newly created receiver.
     */
    static RequestReceiver createRequestReceiver(RequestHandler handlerDelegate) {
        RequestReceiver requestReceiver = new RequestReceiver(handlerDelegate);
        Thread t = new Thread(requestReceiver);
        int priority = t.getThreadGroup().getMaxPriority();
        logger.debug("Setting RequestReceiver priority = "+priority+".");
        t.setPriority(priority);
        logger.debug("Starting the RequestReceiver.");
        t.start();
        return requestReceiver;
    }

    public void run() {
        logger.debug("Called run."+Thread.currentThread());
        while (true) {

            synchronized(this) {
                while (ready == true) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            if (shutdown) {
                logger.debug("Shutdown = true. Breaking the loop.");
                break;
            }

            logger.debug(Thread.currentThread()+"Going!");
            logger.info("Handling Requests from: "+clientSocket.getInetAddress()+":"+clientSocket.getPort());

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);

                logger.debug("Getting Client Protocol Version");
                String clientVersion = in.readLine();
                logger.debug("Client Protocol Version = "+clientVersion);

                if (Version.isCompatible(clientVersion)) {
                    logger.debug("Protocol Version is compatible.");
                    out.write("OK");
                    out.write('\n');

                    logger.debug("Sending list of operation");
                    out.write(String.valueOf(delegateOperations.length));
                    out.write('\n');
                    for (int i = 0; i < delegateOperations.length; i++) {
                        out.write(delegateOperations[i]);
                        out.write('\n');
                        int numOfParams = handlerDelegate.getNumberOfInputParameters(i);
                        out.write(String.valueOf(numOfParams));
                        out.write('\n');
                    }
                    out.flush();

                    logger.debug("Getting initialization string.");
                    String initializationString = in.readLine();
                    logger.debug("Initialization string = "+initializationString);
                    logger.debug("Initializing Handler Delegate.");
                    try {
                        handlerDelegate.init(initializationString);
                        out.write("OK");
                        out.write('\n');
                        out.flush();
                    } catch (InitializationException e) {
                        logger.debug("InitializationException", e);
                        out.write(e.getMessage());
                        out.write('\n');
                        out.flush();
                        throw e;
                    }

                    int[] opCodes = null;
                    String[][] parametersArrays = null;
                    String[] retValues = null;

                    while (true) {
                        if (shutdown) {
                            logger.debug("Shutdown = true. Breaking the communication.");
                            break;
                        }

                        String input = in.readLine();
                        if (input == null) {
                            break;
                        }

                        int numOfEvents = Integer.parseInt(input);

                        if ((opCodes == null) || (opCodes.length < numOfEvents)) {
                            opCodes = new int[numOfEvents];
                        }

                        if ((parametersArrays == null) || (parametersArrays.length < numOfEvents)) {
                            parametersArrays = new String[numOfEvents][];
                        }

                        if ((retValues == null) || (retValues.length < numOfEvents)) {
                            retValues = new String[numOfEvents];
                        }

//                        handlerDelegate.beginBlock(numOfEvents);

//                        int operationCode = -1;
                        int clientNumOfParam = -1;
//                        String[] parameters;
                        for (int i = 0; i < numOfEvents; i++) {
                            try {
//                                operationCode = Integer.parseInt(in.readLine());
                                opCodes[i] = Integer.parseInt(in.readLine());
                                clientNumOfParam = Integer.parseInt(in.readLine());

//                                parameters = handlerDelegate.getInputParameterArray(operationCode);
                                parametersArrays[i] = handlerDelegate.getInputParameterArray(opCodes[i], i);
                                if (parametersArrays[i].length == clientNumOfParam) {
                                    for (int p = 0; p < parametersArrays[i].length; p++) {
                                        input = in.readLine();
                                        if (!input.equals(LUX4.NULL_PARAMETER)) {
                                            parametersArrays[i][p] = input;
                                        }
                                        else {
                                            parametersArrays[i][p] = null;
                                        }
                                    }

                                    retValues[i] = null;

                                }
                                else {
                                    // Wrong number of parameters from the client side.
                                    for (int p = 0; p < clientNumOfParam; p++) {
                                        input = in.readLine();
                                    }
                                    retValues[i] = LUX4.RESPONSE_EXCEPTION + IllegalArgumentException.class.getName() + LUX4.RESPONSE_EXCEPTION_SEPARATOR + "Wrong Number of Parameters";
                                }
                            }
                            catch (OperationNotExistent e) {
                                logger.warn("OperationNotExistent", e);
                                // Wrong operation index from the client side.
                                for (int p = 0; p < clientNumOfParam; p++) {
                                    input = in.readLine();
                                }
                                retValues[i] = LUX4.RESPONSE_EXCEPTION + e.getClass().getName() + LUX4.RESPONSE_EXCEPTION_SEPARATOR + e.getMessage();
                            }

                            eventsHandled++;
                            if (yield) {
                                Thread.yield();
                            }
                        }

                        // Reads and Writes End of Requests control line
                        out.write(LUX4.END_REQUESTS);
                        out.write('\n');
                        out.flush();

                        input = in.readLine();

                        for (int i = 0; i < numOfEvents; i++) {
                            if (retValues[i] == null) {
                                retValues[i] = invokeHandlerDelegate(opCodes[i], parametersArrays[i]);
                            }
                        }

//                        handlerDelegate.endBlock();

                        for (int i = 0; i < numOfEvents; i++) {
                            out.write(retValues[i]);
                            out.write('\n');
                        }
                        out.flush();
                    }
                }
                else {
                    logger.warn("Protocol Version is *NOT* compatible.");
                    logger.warn("Client Protocol Version = "+clientVersion);
                    logger.warn("Server Protocol Version = "+Version.asString());

                    out.write("Incompatible Version. Server Version = "+Version.asString());
                    out.write('\n');
                    out.flush();
                }
            } catch (java.net.SocketTimeoutException e) {
                logger.debug("SocketTimeoutException", e);
                if (shutdown) {
                    logger.debug("Shutdown = true. Breaking the loop.");
                    break;
                }
            } catch (java.net.SocketException e) {
                logger.debug("SocketException", e);
                if (shutdown) {
                    logger.debug("Shutdown = true. Breaking the loop.");
                    break;
                }
            } catch (IOException e) {
                logger.warn("IOException", e);
                if (shutdown) {
                    logger.debug("Shutdown = true. Breaking the loop.");
                    break;
                }
            } catch (InitializationException e) {
                logger.warn("InitializationException", e);
            } finally {
                logger.info("Closed connection from: "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
                try {
                    clientSocket.close();
                } catch (IOException e) {
                }

                clientSocket = null;
            }

            handlerDelegate.close();
            ready = true;
            logger.debug(Thread.currentThread()+"Finished!");

            synchronized(this) {
                notify();
            }

            if (shutdown) {
                logger.debug("Shutdown = true. Breaking the loop.");
                break;
            }
        }

        logger.debug("Exit from run.");

    }

    private String invokeHandlerDelegate(int operationCode, String[] parameters) {
        String out;
        try {
            String result = handlerDelegate.invoke(operationCode, parameters);
            if (result == null) {
                out = "" + LUX4.RESPONSE_NULL;
            }
            else {
                out = LUX4.RESPONSE_OK + result;
            }
        } catch (OperationNotExistent e) {
            logger.warn("OperationNotExistent:"+e.getMessage());
            out = LUX4.RESPONSE_EXCEPTION + e.getClass().getName() + LUX4.RESPONSE_EXCEPTION_SEPARATOR + e.getMessage();
        }
        catch (InvocationTargetException e) {
            logger.debug("InvocationTargetException:"+e.getMessage());
            Throwable cause = e.getTargetException();
            out = LUX4.RESPONSE_EXCEPTION + cause.getClass().getName()+ LUX4.RESPONSE_EXCEPTION_SEPARATOR +
                    cause.getMessage() + LUX4.RESPONSE_EXCEPTION_SEPARATOR ;
        }
        return out;
    }

    public synchronized void handle(Socket socket) {
        logger.info("Accepted Request from: "+socket.getInetAddress()+":"+socket.getPort());
        while (ready == false) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        clientSocket = socket;
        ready = false;
        notify();
    }

    public boolean isReady() {
        return ready;
    }

    public long getNumberOfEventsHandled() {
        return eventsHandled;
    }

    public synchronized void shutdown() {
        logger.debug("Shutdown called.");
        shutdown = true;
        ready = false;
        notify();
    }

}