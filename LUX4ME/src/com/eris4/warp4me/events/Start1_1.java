/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Oct 1, 2003
*/
package com.eris4.warp4me.events;

import com.eris4.warp4me.RatingEvent;

public class Start1_1  extends RatingEvent{

    public void setNickname(String ID) {
        setValue(0,ID);
    }

    public void setSessionId(String sessionID) {
        setValue(1,sessionID);
    }
    protected int getSize() {
        return 2;
    }
}
