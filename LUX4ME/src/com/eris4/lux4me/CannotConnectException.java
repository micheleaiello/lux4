/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Sep 24, 2003
*/
package com.eris4.lux4me;

/**
 * Exception raised if the client can't connect to the LUX4 server.
 * The getMessage method returns a description of the reason why the connection failed.
 */
public class CannotConnectException extends Exception {

    public CannotConnectException() {
        super();
    }

    public CannotConnectException(String message) {
        super(message);
    }

}
