/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.4 $
*/
package com.eris4.lux4.client.impl;

import com.eris4.lux4.client.MultiRequestInvoker;
import com.eris4.lux4.client.Request;
import com.eris4.lux4.client.MultiRequest;
import com.eris4.lux4.client.RequestInvoker;

public class ClientImplFactory {

	static public MultiRequestInvoker createSingleThreadRequestInvoker(String hostname, int port, String initializationString) {
		return new STMultiRequestInvoker(hostname, port, initializationString);
	}

	static public RequestInvoker createMultiThreadConnectionPoolRequestInvoker(String hostname, int port, String initializationString, int connectionCount) {
		return new MTConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
	}

	static public RequestInvoker createMultiThreadAggregatingRequestInvoker(String hostname, int port, String initializationString, int maxAggregatedCount, int connectionCount) {
		return new MTAggregatingRequestInvoker(hostname, port, initializationString, maxAggregatedCount, connectionCount);
	}

	static public Request createRequest() {
		return new RequestImpl();
	}

	static public MultiRequest createMultiRequest() {
		return new MultiRequestImpl();
	}

}
