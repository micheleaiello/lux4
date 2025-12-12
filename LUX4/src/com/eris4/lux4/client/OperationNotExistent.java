/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.3 $
*/
package com.eris4.lux4.client;

/**
 * Exception raised if the client tries to execute an operation not existent on the LUX4 server.
 * The getMessage method returns either the index or the name of the operation not existent.
 */
public class OperationNotExistent extends Exception {

    public OperationNotExistent() {
    }

    public OperationNotExistent(String message) {
        super(message);
    }

}
