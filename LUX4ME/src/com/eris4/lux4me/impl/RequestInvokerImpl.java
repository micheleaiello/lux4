/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Author: Stefano Antonelli
 * Date: Sep 24, 2003
 */

package com.eris4.lux4me.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;


import com.eris4.lux4.Version;
import com.eris4.lux4.LUX4;
import com.eris4.lux4me.CannotConnectException;
import com.eris4.lux4me.InvokerNotConnectedException;
import com.eris4.lux4me.Request;
import com.eris4.lux4me.RequestInvoker;

import javax.microedition.io.StreamConnection;
import javax.microedition.io.Connector;

final class RequestInvokerImpl extends AbstractRequestInvoker implements RequestInvoker {

    private StreamConnection connection;
	private OutputStreamWriter out;
	private ReadLineReader in;


	RequestInvokerImpl(String hostname, int port, String initializationString) {
		super(hostname, port, initializationString);
	}

	protected OperationsData doConnect() throws CannotConnectException {
		String hostname = getHostname();
		int port = getPort();
		String initializationString = getInitializationString();

		OperationsData operationsData;


		try {
            connection=(StreamConnection) Connector.open("socket://"+hostname+":"+port);

			out = new OutputStreamWriter(connection.openDataOutputStream(), "UTF-8");
			in = new ReadLineReader(connection.openDataInputStream(), "UTF-8");

			// Version Checking
			out.write(Version.asString());
			out.write('\n');
			out.flush();
			String canTalk = in.readLine();
			if (canTalk == null) {
				disconnect();
				throw new CannotConnectException("Too Many Clients.");
			}

			if (!canTalk.equals("OK")) {
				disconnect();
				throw new CannotConnectException(canTalk);
			}

			int numOfOperations = Integer.parseInt(in.readLine());
			operationsData = new OperationsData(numOfOperations);
			for (int i = 0 ; i < numOfOperations ; i++) {
				operationsData.operationNameArray[i] = in.readLine();
				operationsData.numberOfInputParametersArray[i] = Integer.parseInt(in.readLine());
			}

			out.write(initializationString);
			out.write('\n');
			out.flush();
			String initializationOK = in.readLine();
			if (!initializationOK.equals("OK")) {
				disconnect();
				throw new CannotConnectException(initializationOK);
			}
		}
		catch (IOException e) {
			disconnect();
			throw new CannotConnectException();
		}
		return operationsData;
	}

	protected void doDisconnect() {
		if (out != null) {
			try {
				out.close();
			}
			catch (IOException e) {
			}
		}

		if (in != null) {
			try {
				in.close();
			}
			catch (IOException e) {
			}
		}

		if (connection!= null) {
			try {
				connection.close();
			}
			catch (IOException e) {
			}
		}

		out = null;
		in = null;
		connection = null;
	}

	protected void doDisconnectAllThreads() {
		doDisconnect();
	}

	public void invoke(Request request)
	        throws InvokerNotConnectedException, IOException {
		if (!super.isConnected()) throw new InvokerNotConnectedException();
		sendStart(1);
		sendRequest(request);
		sendEnd();
		receiveOne(request);
	}


	private void sendStart(int toSendCount) throws InvokerNotConnectedException, IOException {
		if (connection == null) {
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

	/**
	 * Receives a response from the Lux4 server and sets the request's response.
	 * @param request
	 * @throws InvokerNotConnectedException
	 * @throws IOException
	 */
	protected void receiveOne(Request request)
	        throws InvokerNotConnectedException, IOException {
		if (connection == null) {
			throw new InvokerNotConnectedException();
		}

		RequestImpl requestImpl = (RequestImpl) request;

		String value = in.readLine();
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
				throw new RuntimeException("Unknown response code");
		}
	}

}
