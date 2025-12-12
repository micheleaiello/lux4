/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/11 13:53:19 $
* $Name:  $ - $Revision: 1.24 $
*/
package com.eris4.lux4.client.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.eris4.lux4.Version;
import com.eris4.lux4.LUX4;
import com.eris4.lux4.util.LUX4Properties;
import com.eris4.lux4.client.*;

final class STMultiRequestInvoker extends AbstractRequestInvoker implements MultiRequestInvoker {
    private static Logger logger = Logger.getLogger(STMultiRequestInvoker.class);
    private static final int DEFAULT_RETRIES_NUMBER = 5;
    private static final int DEFAULT_SLEEPING_TIME = 10000;

    private Socket socket;
    private OutputStreamWriter out;
    private BufferedReader in;


    STMultiRequestInvoker(String hostname, int port, String initializationString) {
        super(hostname, port, initializationString);
    }

    protected OperationsData doConnect() throws CannotConnectException {
        String hostname = getHostname();
        int port = getPort();
        String initializationString = getInitializationString();

        OperationsData operationsData;

        logger.info("Connecting to the server on " + hostname + ":" + port);

        try {
            this.socket = new Socket(hostname, port);

            int clientSocketSoTimeout = 0;
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
            this.socket.setSoTimeout(clientSocketSoTimeout);

            this.socket.setTcpNoDelay(true);

            this.out = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // Version Checking
            out.write(Version.asString());
            out.write('\n');
            out.flush();
            String canTalk = in.readLine();
            if (canTalk == null) {
                disconnect();
                logger.error("CannotConnect. Too Many Clients. Raising an Exception.");
                throw new CannotConnectException("Too Many Clients.");
            }

            if (!canTalk.equals("OK")) {
                disconnect();
                logger.error("CannotConnect. " + canTalk + "Raising an Exception.");
                throw new CannotConnectException(canTalk);
            }

            int numOfOperations = Integer.parseInt(in.readLine());
            logger.debug("The server implements " + numOfOperations + " operations");
            operationsData = new OperationsData(numOfOperations);
            for (int i = 0 ; i < numOfOperations ; i++) {
                operationsData.operationNameArray[i] = in.readLine();
                operationsData.numberOfInputParametersArray[i] = Integer.parseInt(in.readLine());
                logger.debug("\t" + i + ") " + operationsData.operationNameArray[i] + " (" + operationsData.numberOfInputParametersArray[i] + " parameters)");
            }

            out.write(initializationString);
            out.write('\n');
            out.flush();
            String initializationOK = in.readLine();
            if (!initializationOK.equals("OK")) {
                logger.error("CannotConnect, problems with initialization string. " + initializationOK);
                logger.error("Disconnecting and raising an Exception.");
                disconnect();
                throw new CannotConnectException(initializationOK);
            }
        }
        catch (IOException e) {
            logger.error("IOException.", e);
            logger.error("Disconnecting and raising an Exception.");
            disconnect();
            throw new CannotConnectException();
        }
        return operationsData;
    }

    protected void doDisconnect() {
        if (this.out != null) {
            try {
                this.out.close();
            }
            catch (IOException e) {
                logger.error("Exception while closing the output stream", e);
            }
        }

        if (this.in != null) {
            try {
                this.in.close();
            }
            catch (IOException e) {
                logger.error("Exception while closing the input stream", e);
            }
        }

        if (this.socket != null) {
            try {
                this.socket.close();
            }
            catch (IOException e) {
                logger.error("Exception while closing the socket", e);
            }
        }

        this.out = null;
        this.in = null;
        this.socket = null;
    }

    public void invoke(Request request)
            throws InvokerNotConnectedException, ConnectionException
    {
        if (!super.isConnected()) throw new InvokerNotConnectedException();

        try {
            send(request);
        } catch (IOException e) {
            logger.warn("IOException. Trying to reconnect...");
            int retries = getNumberOfRetries();
            int retriesSleepingTime = getRetriesSleepingTime();
            while (retries > 0) {
                logger.warn("Trying to connect...["+retries+" tries left]");
                retries--;
                try {
                    disconnect();
                    connect();
                    send(request);
                    break;
                } catch (IOException e1) {
                    if (retries <= 0) {
                        logger.error("Connection failed after "+getNumberOfRetries()+" retries.", e1);
                        throw new ConnectionException("IOException:"+e1.getMessage(), ConnectionException.COMPLETED_NO);
                    }
                } catch (CannotConnectException e1) {
                    if (retries <= 0) {
                        logger.error("Connection failed after "+getNumberOfRetries()+" retries.", e1);
                        throw new ConnectionException(e.getMessage(), ConnectionException.COMPLETED_NO);
                    }
                }
                try {
                    logger.warn("Sleeping... ["+retriesSleepingTime+"ms]");
                    Thread.sleep(retriesSleepingTime);
                } catch (InterruptedException e1) {
                }
            }
        }

        try {
            receiveOne(request);
        } catch (IOException e) {
            reconnectAndRaiseConnectionException(e);
        }
    }

    public void invoke(MultiRequest multiRequest)
            throws InvokerNotConnectedException, ConnectionException
    {
        if (!super.isConnected()) throw new InvokerNotConnectedException();

        try {
            send(multiRequest);
        } catch (IOException e) {
            logger.warn("IOException. Trying to reconnect...");
            int retries = getNumberOfRetries();
            int retriesSleepingTime = getRetriesSleepingTime();
            while (retries > 0) {
                logger.warn("Trying to connect...["+retries+" tries left]");
                retries--;
                try {
                    disconnect();
                    connect();
                    send(multiRequest);
                    break;
                } catch (IOException e1) {
                    if (retries <= 0) {
                        logger.error("Connection failed after "+getNumberOfRetries()+" retries.", e1);
                        throw new ConnectionException("IOException:"+e1.getMessage(), ConnectionException.COMPLETED_NO);
                    }
                } catch (CannotConnectException e1) {
                    if (retries <= 0) {
                        logger.error("Connection failed after "+getNumberOfRetries()+" retries.", e1);
                        throw new ConnectionException(e.getMessage(), ConnectionException.COMPLETED_NO);
                    }
                }
                try {
                    logger.warn("Sleeping... ["+retriesSleepingTime+"ms]");
                    Thread.sleep(retriesSleepingTime);
                } catch (InterruptedException e1) {
                }
            }
        }

        try {
            for (int i = 0 ; i < multiRequest.getRequestCount() ; i++) {
                receiveOne(multiRequest.get(i));
            }
        } catch (IOException e) {
            reconnectAndRaiseConnectionException(e);
        }
    }

    private void send(Request request) throws InvokerNotConnectedException, IOException {
        logger.debug("START SEND");
        logger.debug("Sending start...");
        sendStart(1);
        logger.debug("Sending request...");
        sendRequest(request);
        logger.debug("Sending end...");
        sendEnd();
        logger.debug("Receiving ack...");
        receiveEndRequests();
        logger.debug("END SEND");
    }

    private void send(MultiRequest multiRequest) throws InvokerNotConnectedException, IOException {
        sendStart(multiRequest.getRequestCount());
        for (int i = 0 ; i < multiRequest.getRequestCount() ; i++) {
            sendRequest(multiRequest.get(i));
        }
        sendEnd();
        receiveEndRequests();
    }

    private void reconnectAndRaiseConnectionException(IOException e) throws ConnectionException {
        logger.warn("IOException. Trying to reconnect...");
        int retries = getNumberOfRetries();
        int retriesSleepingTime = getRetriesSleepingTime();

        while (retries > 0) {
            logger.warn("Trying to connect...["+retries+" tries left]");
            retries--;
            try {
                disconnect();
                connect();
                break;
            } catch (CannotConnectException e1) {
                if (retries <= 0) {
                    logger.error("Connection failed after "+getNumberOfRetries()+" retries.", e1);
                    throw new ConnectionException(e.getMessage(), ConnectionException.COMPLETED_MAYBE);
                }
            }
            try {
                logger.warn("Sleeping... ["+retriesSleepingTime+"ms]");
                Thread.sleep(retriesSleepingTime);
            } catch (InterruptedException e1) {
            }
        }
        throw new ConnectionException(e, ConnectionException.COMPLETED_MAYBE);
    }

    private void sendStart(int toSendCount) throws InvokerNotConnectedException, IOException {
        if (socket == null) {
            throw new InvokerNotConnectedException();
        }
        out.write(String.valueOf(toSendCount));
        out.write('\n');
    }

    void sendPacket(Packet packet) throws IOException, InvokerNotConnectedException {
        if (!isConnected()) throw new InvokerNotConnectedException();

        int count = packet.getRequestCount();
        sendStart(count);
        for (int i = 0 ; i < count ; i++) {
            sendRequest(packet.getRequest(i));
        }
        sendEnd();
        receiveEndRequests();
    }


    /**
     * Sends a request to the Lux4 server.
     * If the input request's operation is not supported from the server, sets
     * the response error and returns without writing to the socket.
     * @param request
     * @throws IOException
     */
    private void sendRequest(Request request) throws IOException {
        int operationIndex = request.getOperation();
        int numOfParams;
        String[] inputParams;
        int missingParams;
        if (isOperationSupported(operationIndex)) {
            numOfParams = getNumberOfInputParameters(operationIndex);
            inputParams = request.getInputParams();
            missingParams = numOfParams - inputParams.length;
        }
        else {
            numOfParams = 0;
            inputParams = new String[0];
            missingParams = 0;
        }
        int availableParams = Math.min(numOfParams, inputParams.length);

        out.write(String.valueOf(operationIndex));
        out.write('\n');
        out.write(String.valueOf(numOfParams));
        out.write('\n');
        for (int p = 0 ; p < availableParams ; p++) {
            if (inputParams[p] != null) {
                out.write(inputParams[p]);
            }
            else {
                out.write(LUX4.NULL_PARAMETER);
            }
            out.write('\n');
        }

        for (int p = 0 ; p < missingParams ; p++) {
            out.write(LUX4.NULL_PARAMETER);
            out.write('\n');
        }
    }

    private void sendEnd() throws IOException {
        out.flush();
    }

    private void receiveEndRequests() throws IOException {
        String value = in.readLine();
        if (value == null) {
            throw new IOException("Invalid End Of Stream");
        }

        out.write(LUX4.END_REQUESTS);
        out.write('\n');
        out.flush();
    }

    /**
     * Receives a response from the Lux4 server and sets the request's response.
     * @param request
     * @throws InvokerNotConnectedException
     * @throws IOException
     */
    protected void receiveOne(Request request)
            throws InvokerNotConnectedException, IOException {
        logger.debug("START RECEIVEONE");
        if (socket == null) {
            throw new InvokerNotConnectedException();
        }

        RequestImpl requestImpl = (RequestImpl) request;

        logger.debug("Receiving value...");
        String value = in.readLine();
        logger.debug("Received value...");

        if (value == null) {
            throw new IOException("Invalid End Of Stream");
        }

        char code = value.charAt(0);
        switch (code) {
            case LUX4.RESPONSE_NULL:
                requestImpl.setResponse(null, ResponseType.valid);
                break;
            case LUX4.RESPONSE_OK:
                requestImpl.setResponse(value.substring(1), ResponseType.valid);
                break;
            case LUX4.RESPONSE_EXCEPTION:
                requestImpl.setResponse(value.substring(1), ResponseType.serverException);
                break;
            default :
                logger.error("Unknown response code: " + code);
                throw new RuntimeException("Unknown response code");
        }
        logger.debug("END RECEIVEONE");
    }

    private int getNumberOfRetries() {
        int retries;
        try {
            retries = Integer.parseInt(LUX4Properties.getProperty("NumberOfRetries"));
        } catch (NumberFormatException e1) {
            logger.warn("Undefind NumberOfRetries: using default ["+DEFAULT_RETRIES_NUMBER+"]");
            retries = DEFAULT_RETRIES_NUMBER;
        }
        return retries;
    }

    private int getRetriesSleepingTime() {
        int retriesSleepingTime;
        try {
            retriesSleepingTime = Integer.parseInt(LUX4Properties.getProperty("RetriesSleepingTime"));
        } catch (NumberFormatException e1) {
            logger.warn("Undefind RetriesSleepingTime: using default ["+DEFAULT_SLEEPING_TIME+"]");
            retriesSleepingTime = DEFAULT_SLEEPING_TIME;
        }
        return retriesSleepingTime;
    }
}
