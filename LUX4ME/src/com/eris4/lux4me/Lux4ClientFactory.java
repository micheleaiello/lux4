/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Author: Stefano Antonelli
 * Date: Sep 24, 2003
 */
package com.eris4.lux4me;

import com.eris4.lux4me.impl.ClientImplFactory;


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
  static public RequestInvoker createRequestInvoker(String hostname, int port, String initializationString) {
    return ClientImplFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
  }

  /**
   * Factory method to instantiate a Request.
   * @return a new Request.
   */
  static public Request createRequest() {
    return ClientImplFactory.createRequest();
  }

}
