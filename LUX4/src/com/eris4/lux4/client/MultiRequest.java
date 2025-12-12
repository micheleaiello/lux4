/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/09 19:58:39 $
* $Name:  $ - $Revision: 1.5 $
*/
package com.eris4.lux4.client;

/**
 * Interface representing a collection of Requests.
 * The user can change the content of the collection either by the set method, which
 * replaces the current collection with a new one, or by the add method which adds a Request to the current collection.
 * The empty method can be used to clear the collection of Requests.
 * Not Thread-safe. To be used with {@link MultiRequestInvoker#invoke}
 * The instances can be obtained by the factory method {@link Lux4ClientFactory#createMultiRequest}.
 */
public interface MultiRequest {
  /**
   * Sets the parameters for the MultiRequest to be executed on the LUX4 server.
   * operationArray and inputParamsArray must have the same length.
   * For each pair operation-inputParams a Request is instantiated (if necessary), set and added to this MultiRequest.
   * @param operationArray the array containing the indexes of the operations to be executed on the server.
   * @param inputParamsArray the array containing the input parameters of the operations to be executed.
   * @throws IllegalArgumentException if operationArray and inputParamsArray don't have the same length.
   * @throws IllegalArgumentException if at least one of the inputParams contains a '\n' character.
   * @see Request#set
   */
  void set(int[] operationArray, String[][] inputParamsArray);

  /**
   * Empties out the MultiRequest.
   * After the call to this method the MultiRequest contains no Request.
   */
  void empty();

  /**
   * Adds a Request to the MultiRequest.
   * @param operation the index of the operation to be executed on the server.
   * @param inputParams the array containing the input parameters of the operation to be executed.
   * @throws IllegalArgumentException if at least one of the inputParams contains a '\n' character.
   * @see Request
   */
  void add(int operation, String[] inputParams);

  /**
   * Returns the i-th Request contained in this MultiRequest.
   * @param index the index of the Request to return.
   * @return the Request at position index in the MultiRequest.
   * @throws ArrayIndexOutOfBoundsException if index is &lt; 0 || index &gt;= getRequestCount().
   */
  Request get(int index);

  /**
   * Returns the number of Requests contained in this MultiRequest.
   * @return the number of Requests.
   */
  int getRequestCount();

  /**
   * Returns true if the MultiRequest is set, that is it contains at least one Request.
   * @return true if getRequestCount() > 0
   */
  boolean isSet();

}
