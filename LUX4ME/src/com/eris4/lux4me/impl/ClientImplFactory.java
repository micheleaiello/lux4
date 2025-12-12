/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Author: Stefano Antonelli
 * Date: Sep 24, 2003
 */

package com.eris4.lux4me.impl;

import com.eris4.lux4me.RequestInvoker;

public class ClientImplFactory {

	static public RequestInvoker createSingleThreadRequestInvoker(String hostname, int port, String initializationString) {
		return new RequestInvokerImpl(hostname, port, initializationString);
	}

	static public com.eris4.lux4me.Request createRequest() {
		return new RequestImpl();
	}

}
