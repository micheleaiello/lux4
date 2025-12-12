/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.4 $
*/
package com.eris4.lux4.client;

import com.eris4.lux4.client.impl.ClientImplFactory;

/**
 * Factory class used to instantiate the different kinds of RequestInvoker and the Request and MultiRequest.
 * Use only the Request and MultiRequest instances obtained by this factory class as parameters to the different kinds of RequestInvoker instantiated by this factory class.
 */
public class Lux4ClientFactory {

  /**
   * Factory method to instantiate a RequestInvoker to be used by a single thread.
   * @param hostname the hostname of the LUX4 server that has to be connected.
   * @param port the port number of the LUX4 server that has to be connected.
   * @param initializationString the initialization string that is used to configure the Handler
   * that will handle all the requests comming from this client.
   * @return a new MultiRequestInvoker.
   */
  static public MultiRequestInvoker createSingleThreadRequestInvoker(String hostname, int port, String initializationString) {
    return ClientImplFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
  }

  /**
   * Factory method to instantiate a thread-safe RequestInvoker to be used by multiple client threads.
   * This method instantiates a pool of connectionCount MultiRequestInvoker.
   * The MultiRequestInvoker in the pool are used to carry out the requests on behalf of the client threads.
   * @param hostname the hostname of the LUX4 server that has to be connected.
   * @param port the port number of the LUX4 server that has to be connected.
   * @param initializationString the initialization string that is used to configure the Handler
   * that will handle all the requests comming from this client.
   * @param connectionCount the number of threads in the pool.
   * @return a new RequestInvokerMT that can be used by multiple threads.
   */
  static public RequestInvoker createMultiThreadConnectionPoolRequestInvoker(String hostname, int port, String initializationString, int connectionCount) {
    return ClientImplFactory.createMultiThreadConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
  }

  /**
   * Factory method to instantiate a thread-safe RequestInvoker to be used by multiple client threads.
   * The returned RequestInvoker aggregates the requests coming from many clients into packets which are then sent to the server.
   * @param hostname the hostname of the LUX4 server that has to be connected.
   * @param port the port number of the LUX4 server that has to be connected.
   * @param initializationString the initialization string that is used to configure the Handler
   * that will handle all the requests comming from this client.
   * @param maxAggregatedCount the max dimension of the packet of requests to send to the server.
   * @param connectionCount the number of actual connections to the server.
   * @return a new RequestInvokerMT that can be used by multiple threads.
   */
  static public RequestInvoker createMultiThreadAggregatingRequestInvoker(String hostname, int port, String initializationString, int maxAggregatedCount, int connectionCount) {
    return ClientImplFactory.createMultiThreadAggregatingRequestInvoker(hostname, port, initializationString, maxAggregatedCount, connectionCount);
  }

  /**
   * Factory method to instantiate a Request.
   * @return a new Request.
   */
  static public Request createRequest() {
    return ClientImplFactory.createRequest();
  }

  /**
   * Factory method to instantiate a MultiRequest.
   * @return a new MultiRequest.
   */
  static public MultiRequest createMultiRequest() {
    return ClientImplFactory.createMultiRequest();
  }

}
