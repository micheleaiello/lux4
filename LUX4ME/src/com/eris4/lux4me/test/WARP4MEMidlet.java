/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Sep 24, 2003
*/
package com.eris4.lux4me.test;

import com.eris4.lux4me.*;
import com.eris4.warp4me.authorization.*;
import com.eris4.warp4me.events.BricksGaming;
import com.eris4.warp4me.events.BricksItem;
import com.eris4.warp4me.RatingEvent;
import com.eris4.warp4me.ConnectionException;
import com.eris4.warp4me.RatingAccountNotFoundException;
import com.eris4.warp4me.RatingValue;
import com.eris4.warp4me.rating.RatingException;
import com.eris4.warp4me.rating.RatingManager;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import java.io.IOException;

public class WARP4MEMidlet extends MIDlet{

    protected void startApp() throws MIDletStateChangeException {
        try {
            String hostname = "localhost";//todo usare property
            int port = 4444;//todo usare property

            RatingManager ratingManager=new RatingManager(hostname,port,"BricksItem");


            BricksItem e=(BricksItem) RatingEvent.createEvent("BricksItem");

            e.setGamerTag("ste");
            e.setItem("");
            e.setLevel(0);
            e.setQuantity(1);
            RatingValue res=ratingManager.rateActual(e);

            System.out.println("Result:"+res.getAmount()+"@"+res.getUnit());

        } catch (InvalidEventTypeException e1) {
           e1.printStackTrace();
        } catch (ConnectionException e1) {
            e1.printStackTrace();

        } catch (RatingException e1) {
            e1.printStackTrace();
        } catch (RatingAccountNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException {
    }

    private static String[] getInputParam(int numOfInputParams) {
        String[] out = new String[numOfInputParams];
        for (int i = 0 ; i < out.length ; i++) {
            out[i] = "Param" + i;
        }
        return out;
    }
}
