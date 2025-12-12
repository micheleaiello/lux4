/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 12, 2003.
*/

package com.eris4.warp4me;

public class RatingAccountAlreadyExists extends Exception {
    public RatingAccountAlreadyExists() {
    }

    public RatingAccountAlreadyExists(String message) {
        super(message);
    }
}
