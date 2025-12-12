/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/09 19:58:39 $
* $Name:  $ - $Revision: 1.5 $
*/
package com.eris4.lux4.client;

/**
 * Interface describing the functionality of a request to be executed on a LUX4 server.
 * It is used to contain the input data (operation index and operatione arguments) and
 * the output data returned by the LUX4 server (result value or error description).
 * The standard idiom for using the request should be something like:
 * <pre>
 * 	 Request request = Lux4ClientFactory.createRequest();
 *	 while (some condition) {
 *	   request.set(operation, inputParams);
 *     try {
 *		 invoker.invoke(request);
 *	   }
 *	   catch (InvokerNotConnectedException e) {
 *          ....
 *     }
 *     catch (IOException e) {
 *          ....
 *     }
 *     if (request.isResponseValid()) {
 *        String result = request.getResponseResult();
 *        // use result
 *      }
 *      else {
 *        String errorClassName = request.getResponseErrorClassName();
 *        String errorMessage = request.getResponseErrorMessage();
 *        // use errorClassName and errorMessage;
 *      }
 * </pre>
 *
 * The instances should be obtained by the factory method {@link Lux4ClientFactory#createRequest}.
 */
public interface Request {

	/**
	 * Sets the parameters for the Request to be executed on the LUX4 server.
	 * It also resets the Response associated to this Request.
	 * Overloading used when the operation to invoke has more than three parameter.
	 * @param operation the index of the operation on the server.
	 * @param inputParams the input parameters of the operation to be executed.
	 * @throws IllegalArgumentException if at least one of the inputParams contains a '\n' character.
	 */
	void set(int operation, String[] inputParams);

	/**
	 * Sets the parameters for the Request to be executed on the LUX4 server.
	 * It also resets the Response associated to this Request.
	 * Overloading used when the operation to invoke has no parameter.
	 * @param operation the index of the operation on the server.
	 * @throws IllegalArgumentException if at least one of the inputParams contains a '\n' character.
	 */
	void set(int operation);

	/**
	 * Sets the parameters for the Request to be executed on the LUX4 server.
	 * It also resets the Response associated to this Request.
	 * Overloading used when the operation to invoke has one parameter.
	 * @param operation the index of the operation on the server.
	 * @param inputParam the input parameters of the operation to be executed.
	 * @throws IllegalArgumentException if at least one of the inputParams contains a '\n' character.
	 */
	void set(int operation, String inputParam);

	/**
	 * Sets the parameters for the Request to be executed on the LUX4 server.
	 * It also resets the Response associated to this Request.
	 * Overloading used when the operation to invoke has two parameter.
	 * @param operation the index of the operation on the server.
	 * @param inputParam1 the first input parameter of the operation to be executed.
	 * @param inputParam2 the second input parameter of the operation to be executed.
	 * @throws IllegalArgumentException if at least one of the inputParams contains a '\n' character.
	 */
	void set(int operation, String inputParam1, String inputParam2);

	/**
	 * Sets the parameters for the Request to be executed on the LUX4 server.
	 * It also resets the Response associated to this Request.
	 * Overloading used when the operation to invoke has three parameter.
	 * @param operation the index of the operation on the server.
	 * @param inputParam1 the first input parameter of the operation to be executed.
	 * @param inputParam2 the second input parameter of the operation to be executed.
	 * @param inputParam3 the third input parameter of the operation to be executed.
	 * @throws IllegalArgumentException if at least one of the inputParams contains a '\n' character.
	 */
	void set(int operation, String inputParam1, String inputParam2, String inputParam3);

    /**
     * Sets the parameters for the Request to be executed on the LUX4 server.
     * It also resets the Response associated to this Request.
     * Overloading used when the operation to invoke has four parameter.
     * @param operation the index of the operation on the server.
     * @param inputParam1 the first input parameter of the operation to be executed.
     * @param inputParam2 the second input parameter of the operation to be executed.
     * @param inputParam3 the third input parameter of the operation to be executed.
     * @param inputParam4 the fourth input parameter of the operation to be executed.
     * @throws IllegalArgumentException if at least one of the inputParams contains a '\n' character.
     */
    void set(int operation, String inputParam1, String inputParam2, String inputParam3, String inputParam4);

  /**
   * Returns the operation index associated to the Request.
   * @return the operation index.
   * @throws IllegalStateException if the Request is not set.
   */
  int getOperation();

  /**
   * Returns the input parameters associated to the Request.
   * @return the input parameters.
   * @throws IllegalStateException  if the Request is not set.
   */
  String[] getInputParams();

  /**
   * Returns true if the Request is set.
   * A Request is set if the set method has been called before.
   * @return true if the Request is set.
   */
  boolean isRequestSet();

  /**
   * Returns true if the Response associated to this Request is set.
   * A Response is set if one of the invoke() methods has been called passing this Request.
   * @return true if the Response is set.
   */
  boolean isResponseSet();

  /**
   * Returns true if the Response associated to this Request is set and contains a valid return value supplied by the LUX4 server.
   * @return true if the Response is valid.
   * @throws IllegalStateException  if the Response is not set.
   */
  boolean isResponseValid();

  /**
   * Returns a string containing the valid Response associated to this Request.
   * The string has been set by the LUX4 server.
   * @return the Response supplied by the LUX4 server.
   * @throws IllegalStateException if the Response is not valid or not set.
   */
  String getResponseResult();

  /**
   * Returns a string containing a description of the error Response associated to this Request.
   * The string has been set by the LUX4 server.
   * @return the Response supplied by the LUX4 server.
   * @throws IllegalStateException if the Response is valid or not set.
   */
  String getResponseErrorMessage();

	/**
	 * Returns a string containing the class name of the exception of the error Response associated to this Request.
	 * The string has been set by the LUX4 server.
	 * @return the Response supplied by the LUX4 server.
	 * @throws IllegalStateException if the Response is valid or not set.
	 */
	String getResponseErrorClassName();
}
