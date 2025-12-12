/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Sep 24, 2003
*/
package com.eris4.lux4me;

/**
 * Exception raised if the client called a method on the RequestInvoker which required a connection to a LUX4 server but the RequestInvoker was not connected.
 */
public class InvokerNotConnectedException extends Exception {
}
