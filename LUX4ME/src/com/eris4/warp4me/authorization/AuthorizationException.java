/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 16, 2003.
*/

package com.eris4.warp4me.authorization;

public class AuthorizationException extends Exception{
    public AuthorizationException() {
    }

    public AuthorizationException(String message) {
        super(message);
    }
}
