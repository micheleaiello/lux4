/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Sep 25, 2003
*/
package com.eris4.warp4me.events;

import com.eris4.warp4me.RatingEvent;

public class Gaming1_1 extends RatingEvent{

    public final void setNickname(String gamerTag) {
        setValue(0,gamerTag);
    }

    public final void setSessionID(String sessionID) {
        setValue(1,sessionID);
    }

    public final void setLevel(int level) {
        setValue(2,Integer.toString(level));
    }

    public final void setEarnedPoints(int points) {
        setValue(3,Integer.toString(points));
    }

    public final void setLives(int lives) {
        setValue(4,Integer.toString(lives));
    }

    public final void setDuration(int duration) {
        setValue(5,Integer.toString(duration));
    }

    protected final int getSize() {
        return 6;
    }
}
