/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.3 $
*/
package com.eris4.lux4.util.timeutil;

public interface StopWatch {

	/**
	 * Start the StopWatch
	 */
	void start();

	/**
	 * Stop the StopWatch
	 */
	void stop();

	/**
	 * @return the milliseconds elapsed between last start() stop() call pair.
	 * @throws IllegalStateException if never started or not stopped after last start.
	 */
	double getDuration();

}
