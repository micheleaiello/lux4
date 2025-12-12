/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.4 $
*/
package com.eris4.lux4.client;

/**
 * Exception raised if the client called a method on the RequestInvoker which required a connection to a LUX4 server but the RequestInvoker was not connected.
 */
public class InvokerNotConnectedException extends Exception {
}
