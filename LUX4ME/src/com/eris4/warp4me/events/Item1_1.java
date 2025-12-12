/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Sep 25, 2003
*/
package com.eris4.warp4me.events;

import com.eris4.warp4me.RatingEvent;

public class Item1_1 extends RatingEvent{

    public void setNickname(String ID) {
        setValue(0,ID);
    }

    public void setLevel(int level) {
        setValue(1,Integer.toString(level));
    }

    public void setItem(String item) {
        setValue(2,item);
    }

    public void setQuantity(int points) {
        setValue(3,Integer.toString(points));
    }

    protected final int getSize() {
        return 4;
    }

}
