/*
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Author: Stefano Antonelli
 * Date: Sep 24, 2003
 */

package com.eris4.lux4me;

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
