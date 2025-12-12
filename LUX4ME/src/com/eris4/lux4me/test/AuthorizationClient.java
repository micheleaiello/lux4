/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * User: MK
 * Date: 25-mar-2003
 */
package com.eris4.lux4me.test;

import com.eris4.lux4me.*;
import com.eris4.warp4me.authorization.SessionManager;
import com.eris4.warp4me.RatingEvent;
import com.eris4.warp4me.events.BricksGaming;

import java.io.IOException;


//todo farne una versione per l'SDK
public class AuthorizationClient {

	public static void main(String[] args) throws Exception
	{
		String hostname = "localhost";//todo usare property
		int port = 3333;//todo usare property

        SessionManager sessionManager=new SessionManager(hostname,port,"BricksGaming");

        String sid=sessionManager.createSession();

        BricksGaming e=(BricksGaming) RatingEvent.createEvent("BricksGaming");

        e.setGamerTag("GamerID");
        e.setLevel(0);
        e.setEarnedPoints(0);

        sessionManager.init(sid,e);

        sessionManager.start(sid,e);
        e.setLevel(1);
        sessionManager.goOn(sid,e);
        e.setLevel(2);
        sessionManager.goOn(sid,e);
        sessionManager.end(sid,e);
	}

}
