/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Sep 30, 2003
*/
package com.eris4.warp4me.events;

import com.eris4.warp4me.RatingEvent;

public class Credit1_1 extends RatingEvent{
    public void setNickname(String ID) {
        setValue(0,ID);
    }

    public void setCredit(long credit) {
        setValue(1,Long.toString(credit));
    }

    protected int getSize() {
        return 2;
    }
}
