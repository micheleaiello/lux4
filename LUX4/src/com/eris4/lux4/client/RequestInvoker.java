/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/07 17:58:55 $
* $Name:  $ - $Revision: 1.23 $
*/
package com.eris4.lux4.client;

import java.io.IOException;

/**
 * Interface defining the functionality common to all kinds of RequestInvokers.
 * The functionality comprises methods to connect and disconnect from the server and
 * methods for obtaining informations about the server and the operations it supplies.</p>
 * The methods receiving an operation name as parameter throw the checked exception OperationNotExistent,
 * while the methods receiving an operation index as parameter throw the unchecked exception IllegalArgumentException.
 * The standard usage is to find the operation indexes of all the operation of interest using findOperationIndex(operationName)
 * during startup after connection; in the rest of the code, for performance reason,
 * do not use the methods receiving an operation name as parameter.</p>
 * It is extended by {@link MultiRequestInvoker}, thread-unsafe to be used by single thread clients
 * that needs to use the invoke() method with a multiRequest.
 */
public interface RequestInvoker {

	/**
	 * Connects a RequestInvoker to a LUX4 server.
	 * The connect method takes care of a number of initialization tasks.
	 * First of all the conenct method sends the LUX4 Protocol Version number
	 * to the LUX4 server. If the two versions of the LUX4 Protocol are not
	 * compatible an exception is raised.
	 * Otherwise, if the client and the server are using compatible versions of the
	 * LUX4 Protocol, the communication can take place and is started sending
	 * an initialization string to the server.
	 * The initialization string is used to initialize the Handler that will be
	 * associate with this client.
	 * The connection parameters used to open the connection are those supplied to the
	 * create method of {@link Lux4ClientFactory} used to intantiate this request invoker.
     *
	 * @throws CannotConnectException if the communication cannot take place. This can happen
	 * for a number of reasons: the client and the server are using different versions of
	 * the LUX4 Protocol that are not compatible, it is not possible to create a socket
	 * to the server machime, and so on. The exception contains a message with a desctiption
	 * of the problem.
	 */
	void connect()
	        throws CannotConnectException;

	/**
	 * Closes the connection with the server.
	 */
	void disconnect();

	/**
	 * Returns a String array containing the names of all the methods implemented in the currently connected server.
	 * The array is ordered by operation index.
	 *
	 * @return an array of operation names.
	 * @throws IllegalStateException if not connected.
	 */
	String[] getOperations();

	/**
	 * Returns the number of input parameter for the given method.
	 *
	 * @param operation the index of the operation as defined by the getOperations method.
	 *
	 * @return the number of parameters of the given operation.
	 * @throws IllegalStateException if not connected.
	 * @throws IllegalArgumentException if the given operation doesn't exist.
	 */
	int getNumberOfInputParameters(int operation);

	/**
	 * Returns the number of input parameter for the given method.
	 *
	 * @param operationName the name of the operation.
	 *
	 * @return the number of parameters of the given operation.
	 * @throws IllegalStateException if not connected.
	 * @throws OperationNotExistent if the given operation doesn't exist.
	 */
	int getNumberOfInputParameters(String operationName) throws OperationNotExistent;

	/**
	 * Returns an int array containing the number of input parameters for each method in the server.
     * The returned array is ordered in the same way of the array returned by the getOperations method.
	 *
	 * @return an array containing the number of input parameters for each method in the server.
	 * @throws IllegalStateException if not connected.
	 */
	int[] getNumberOfInputParameters() ;

    /**
     * Returns true if this RequestInvoker is connected to a LUX4 server.
	 * @return true if connected.
	 */
	boolean isConnected();

	/**
     * Returns the Hostname of the LUX4 server used by this RequestInvoker.
     * The value returned is the one passed to the factory method of {@link Lux4ClientFactory} used to instantiate this RequestInvoker.
	 * @return the Hostname of the LUX4 server.
	 */
	String getHostname();

	/**
     * Returns the port number of the LUX4 server used by this RequestInvoker.
     * The value returned is the one passed to the factory method of {@link Lux4ClientFactory} used to instantiate this RequestInvoker.
	 * @return the Port number of the LUX4 server.
	 */
	int getPort();

  /**
   * Returns the initialization string of the LUX4 server used by this RequestInvoker.
   * The value returned is the one passed to the factory method of {@link Lux4ClientFactory} used to instantiate this RequestInvoker.
   * @return the initialization String supplied to the LUX4 server.
   */
	String getInitializationString();

	/**
	 * Returns the index of a given operation.
	 * @param operationName the name of the operation.
	 * @return the index of the given operation
	 * @throws OperationNotExistent if the given operation name does't exist on the server.
	 * @throws IllegalStateException if not connected.
	 */
	int findOperationIndex(String operationName)
	    throws OperationNotExistent;

  /**
   * Returns an array of int containing the indexes of all the operations specified in the given operationNameArray.
   * @param operationNameArray an array containing the names of the operations whose indexes are to be found.
   * @return the array of indexes of the given operations.
   * @throws OperationNotExistent if at least one of the given operation names doesn't exist on the server.
   * @throws IllegalStateException if not connected.
   */
	int[] findOperationArrayIndexes(String[] operationNameArray)
	    throws OperationNotExistent;

	void invoke(Request request)
	        throws InvokerNotConnectedException, ConnectionException;


}
