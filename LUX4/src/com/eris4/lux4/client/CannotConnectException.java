/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.4 $
*/
package com.eris4.lux4.client;

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
