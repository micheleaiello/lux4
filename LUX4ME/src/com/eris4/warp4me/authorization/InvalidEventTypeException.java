/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 11, 2003.
*/

package com.eris4.warp4me.authorization;

public class InvalidEventTypeException extends Exception {
    public InvalidEventTypeException() {
    }

    public InvalidEventTypeException(String message) {
        super(message);
    }
}
