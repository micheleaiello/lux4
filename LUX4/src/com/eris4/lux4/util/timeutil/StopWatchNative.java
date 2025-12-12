/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.2 $
*/
package com.eris4.lux4.util.timeutil;

class StopWatchNative implements StopWatch {

	private double startTime = Double.MAX_VALUE;
	private double stopTime = 0;
	private boolean running;

	public void start() {
		running = true;
		startTime = TimeUtil.getTime();
	}

	public void stop() {
		stopTime = TimeUtil.getTime();
		running = false;
	}

	public double getDuration() {
		if (running) throw new IllegalStateException("Not stopped after last start.");
		if (stopTime < startTime) throw new IllegalStateException("Never started.");
		return stopTime - startTime;
	}

}
