/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.14 $
*/
package com.eris4.lux4.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import com.eris4.lux4.client.OperationNotExistent;

/**
 * This is the base class for all the server request handler.
 * Subclasses of this class can implements any number (this version is limited to 255)
 * of public methods with a return value of type String and any number (this version is limited to 255)
 * of parameters of type String.
 * The client will be able to know all the operations implemented in the handler via the getOpertaions method.
 */
public abstract class RequestHandler {

    /**
     * This method is called when a client starts a connection and is assigned to a RequestReceiver.
     * This method gives to the RequestHandler a way to be configured depending on the clients will.
     *
     * @param initializationString the string sent by the client to inzialize the Handler.
     * @throws InitializationException if the something goes wrong during the initialization.
     */
    public abstract void init(String initializationString) throws InitializationException;

// TODO Methods for blocks handling? se si tolgono togliere anche HandlerException
    /**
     * This method is called when a client receive a new block of requests to handle.
     * This method gives to the RequestHandler a way to perform operations before starting the
     * handling of the requests.
     *
     * @param blocksize the size of the block that is going to be handled.
     * @throws HandlerException if something goes wrong. If this exception is raised by the RequestHandler
     * the handling of the request will *not* take place, and the client will receive an exception.
     */
//    public void beginBlock(int blocksize) throws HandlerException;

    /**
     * This method is called when a client has finished the handling of a requests block.
     * This method gives to the RequestHandler a way to perform operations after the
     * handling of the requests.
     *
     * @throws HandlerException if something goes wrong. If this exception is raised by the RequestHandler
     * the results will *not* be sent back, and the client will receive an exception.
     */
//    public void endBlock() throws HandlerException;

    /**
     * This is the main method of the RequestHandler interface. This method is called for the
     * handling of all the incoming requests.
     *
     * @param inputString the request made by the client.
     * @return the aswer to the input request.
     */
//    public abstract String handle(String inputString);

    /**
     * This method is called when the connection with the client is terminated.
     */
    public abstract void close();

    protected Class actualClass = this.getClass();
    protected Method[] operations;
    protected int[] numOfInputParameters;
    protected String[][] inputParameterArrays;
    protected String[][][] inputParameterArraysPool;

    /**
     * Default constructor.
     */
    public RequestHandler() {
        initOperations();
    }

    private void initOperations() {
        Vector tmpMethods = new Vector();

        Method[] methods = actualClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (!methods[i].getReturnType().equals(String.class)) {
                continue;
            }

            Class[] params = methods[i].getParameterTypes();

            if (methods[i].getName().equals("toString")) {
                continue;
            }

            boolean good = true;
            for (int j = 0; j < params.length; j++) {
                if (!params[j].equals(String.class)) {
                    good = false;
                    continue;
                }
            }

            if (good) {
                tmpMethods.add(methods[i]);
            }
        }
        operations = new Method[tmpMethods.size()];
        operations = (Method[]) tmpMethods.toArray(operations);

        numOfInputParameters = new int[operations.length];
        for (int i = 0; i < numOfInputParameters.length; i++) {
            numOfInputParameters[i] = operations[i].getParameterTypes().length;
        }

        inputParameterArrays = new String[operations.length][];
        for (int i = 0; i < operations.length; i++) {
            inputParameterArrays[i] = new String[numOfInputParameters[i]];
        }

        inputParameterArraysPool = new String[operations.length][1][];
        for (int i = 0; i < operations.length; i++) {
            inputParameterArraysPool[i][0] = new String[numOfInputParameters[i]];
        }
    }

    /**
     * This method returns the names of the operations defined in the server.
     *
     * @return an array containing the ordered list of operation name.
     */
    public String[] getOperations() {
        String[] retValue = new String[operations.length];
        for (int i = 0; i < operations.length; i++) {
            retValue[i] = operations[i].getName();
        }
        return retValue;
    }

    /**
     * This method returns the number of parameters of a given operation.
     *
     * @param operation the index of the operation.
     *
     * @return the number of input parameters (all of type String) for the operation.
     */
    public int getNumberOfInputParameters(int operation) {
        if ((operation < 0) || (operation >= numOfInputParameters.length)) {
            return -1;
        }

        return numOfInputParameters[operation];
    }

    /**
     * This method return an array of String ...
     *
     * @param operation the index of the operation.
     * @return
     * @throws OperationNotExistent if the operation doesn't exist.
     */
    String[] getInputParameterArray(int operation)
            throws OperationNotExistent
    {
        if ((operation < 0) || (operation >= numOfInputParameters.length)) {
            throw new OperationNotExistent("Operation index = " + operation);
        }

        return inputParameterArrays[operation];
    }

    String[] getInputParameterArray(int operation, int index)
            throws OperationNotExistent
    {
        if ((operation < 0) || (operation >= numOfInputParameters.length)) {
            throw new OperationNotExistent("Operation index = " + operation);
        }

        if (index >= inputParameterArraysPool[operation].length) {
            inputParameterArraysPool[operation] = new String[index+1][];
            for (int i = 0; i < inputParameterArraysPool[operation].length; i++) {
                inputParameterArraysPool[operation][i] = new String[numOfInputParameters[operation]];
            }
        }

        return inputParameterArraysPool[operation][index];
    }

//    public String invoke(int operation, String inputValues) {
//        if ((operation < 0) || (operation >= operations.length)) {
//            return "Operation not existent";
//        }
//
//        Method m = operations[operation];
//        args[0] = inputValues;
//        String retValue = null;
//        try {
//            retValue = (String) m.invoke(this, args);
//        } catch (IllegalAccessException e) {
//        } catch (IllegalArgumentException e) {
//        } catch (InvocationTargetException e) {
//        }
//        return retValue;
//    }

    /**
     * This method invokes the given operation with the given list of input values.
     *
     * @param operation the index of the operation.
     * @param inputValues the array of String used as input parameters for the operation.
     *
     * @return the result of the operation invoked on the server.
     * @throws OperationNotExistent
     * @throws InvocationTargetException wraps the exception thrown by the invoked method. The exception can be accessed by the getCause() method.
     */
    public String invoke(int operation, String[] inputValues)
            throws OperationNotExistent, InvocationTargetException
    {
        if ((operation < 0) || (operation >= operations.length)) {
	          throw new OperationNotExistent("Operation index = " + operation);
        }
        Method m = operations[operation];

	    String out = null;
	    try {
		    out = (String) m.invoke(this, inputValues);
	    }
	    catch (IllegalAccessException e) {
		    throw new RuntimeException(e.getMessage());
	    }
	    return out;
    }

}
