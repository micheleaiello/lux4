/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.11 $
*/
package com.eris4.lux4.client.impl;

import org.apache.log4j.Logger;

import com.eris4.lux4.client.CannotConnectException;
import com.eris4.lux4.client.OperationNotExistent;
import com.eris4.lux4.client.RequestInvoker;
import com.eris4.lux4.Version;

abstract class AbstractRequestInvoker implements RequestInvoker {

	static private Logger logger = Logger.getLogger(AbstractRequestInvoker.class);

	private String hostname;
	private int port;
	private String initializationString;

	private boolean connected;

	private String[] operations;
	private int[] numberOfInputParametersArray;
	private OperationsData operationsData;

	protected AbstractRequestInvoker(String hostname, int port, String initializationString) {
		logger.debug("Creating a RequestInvoker");
		logger.debug("Communication Protocol Version = " + Version.asString());
		this.hostname = hostname;
		this.initializationString = initializationString;
		this.port = port;
		operations = new String[0];
		numberOfInputParametersArray = new int[0];
		connected = false;
	}

	public synchronized final void connect()
	        throws CannotConnectException {
        if (!connected) {
            operationsData = doConnect();

            this.operations = operationsData.operationNameArray;
            for (int i = 0 ; i < operations.length ; i++) {
                String operation = operations[i];
                for (int j = i + 1 ; j < operations.length ; j++) {
                    if (operation.equals(operations[j])) {
                        throw new IllegalStateException("Duplicate operation name from server. Overloading not allowed. Operation name = " + operation);
                    }
                }
            }

            this.numberOfInputParametersArray = operationsData.numberOfInputParametersArray;
            connected = true;
        }
	}

	protected abstract OperationsData doConnect() throws CannotConnectException;

	public synchronized final void disconnect() {
        if (connected) {
            doDisconnect();
            connected = false;
        }
	}

	protected abstract void doDisconnect();

	public synchronized final String[] getOperations() {
		if (!isConnected()) throw new IllegalStateException("Not isConnected()");
		String[] out = new String[operations.length];
		System.arraycopy(operations, 0, out, 0, operations.length);
		return out;
	}

	public synchronized final boolean isOperationSupported(int operationIndex) {
		if (!isConnected()) throw new IllegalStateException("Not isConnected()");
		return !((operationIndex < 0) || (operationIndex >= operations.length));
	}

	public synchronized final int getNumberOfInputParameters(int operation) {
		if (!isConnected()) throw new IllegalStateException("Not isConnected()");
		if ((operation < 0) || (operation >= numberOfInputParametersArray.length)) {
			throw new IllegalArgumentException("Illegal operation. index = " + operation);
		}
		else {
			return numberOfInputParametersArray[operation];
		}
	}

	public synchronized final int getNumberOfInputParameters(String operationName)
	        throws OperationNotExistent {
		if (!isConnected()) throw new IllegalStateException("Not isConnected()");
		return getNumberOfInputParameters(findOperationIndex(operationName));
	}

	public synchronized final int[] getNumberOfInputParameters() {
		if (!isConnected()) throw new IllegalStateException("Not isConnected()");
		int[] out = new int[numberOfInputParametersArray.length];
		System.arraycopy(numberOfInputParametersArray, 0, out, 0, numberOfInputParametersArray.length);
		return out;
	}

	public synchronized final boolean isConnected() {
		return connected;
	}

	public synchronized final String getHostname() {
		return hostname;
	}

	public synchronized final int getPort() {
		return port;
	}

	public synchronized final String getInitializationString() {
		return initializationString;
	}

	public int findOperationIndex(String operationName)
	        throws OperationNotExistent {
        // URGENT remove this throwable and use an exception
        if (!isConnected()) throw new IllegalStateException("Not isConnected()");
		int i = 0;
		int operationIndex = -1;
		while (i < operations.length) {
			if (operations[i].equals(operationName)) {
				operationIndex = i;
				break;
			}
			else {
				i++;
			}
		}

		if (operationIndex < 0) {
			logger.warn("Operation \"" + operationName + "\" not found.");
			throw new OperationNotExistent(operationName);
		}

		return operationIndex;
	}

	public int[] findOperationArrayIndexes(String[] operationNameArray)
	        throws OperationNotExistent {
		if (!isConnected()) throw new IllegalStateException("Not isConnected()");
		int[] out = new int[operationNameArray.length];
		for (int i = 0 ; i < out.length ; i++) {
			out[i] = findOperationIndex(operationNameArray[i]);
		}
		return out;
	}

	static Logger getLogger() {
		return logger;
	}

	protected OperationsData getOperationsData() {
		if (!isConnected()) throw new IllegalStateException("Not isConnected()");
		return operationsData;
	}
}

