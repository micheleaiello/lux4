/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: antonelli $ - $Date: 2005/11/07 17:25:18 $
* $Name:  $ - $Revision: 1.3 $
*/
package com.eris4.lux4.client;

import java.io.IOException;

/**
 * A thread-unsafe RequestInvoker that can be used by a single client thread.
 */
public interface MultiRequestInvoker extends RequestInvoker {

	/**
	 * Executes a request on the connected LUX4 server.
     * On return from the method, the input request will contain the associated response from the server.
	 *
	 * @param request a not null properly set Request to be sent to the server.
	 *
	 * @throws InvokerNotConnectedException if not connected.
	 * @throws ConnectionException if something goes wrong in the communication.
	 */
	void invoke(Request request)
	        throws InvokerNotConnectedException, ConnectionException;

	/**
     * Executes a multiRequest on the connected LUX4 server.
     * On return from the method, every request in the multiRequest will contain the associated response from the server.
	 *
	 * @param multiRequest a not null properly set MultiRequest to be sent to the server.
	 *
	 * @throws InvokerNotConnectedException if not connected.
	 * @throws ConnectionException if something goes wrong in the communication.
	 */
	void invoke(MultiRequest multiRequest)
	        throws InvokerNotConnectedException, ConnectionException;
}
