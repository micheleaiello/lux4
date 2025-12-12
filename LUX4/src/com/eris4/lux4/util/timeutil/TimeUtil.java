/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2007/03/27 15:46:46 $
* $Name:  $ - $Revision: 1.5 $
*/
package com.eris4.lux4.util.timeutil;

import org.apache.log4j.Logger;

public class TimeUtil {

	static private Logger logger = Logger.getLogger(TimeUtil.class);
	/*
	 * This is supposed to return a fractional count of milliseconds elapsed
	 * since some indeterminate moment in the past.
	 *
	 * JNI code in HRTIMER_LIB library is supposed to implement this.
	 */
	static native double getTime();

	private static final String HRTIMER_LIB = "hrtimer";

	private static boolean nativeTimer;

	static {
		try {
			System.loadLibrary(HRTIMER_LIB);
			nativeTimer = true;
		}
		catch (UnsatisfiedLinkError e) {
			nativeTimer = false;
			logger.info("native lib '" + HRTIMER_LIB
			                   + "' not found in 'java.library.path': "
			                   + System.getProperty("java.library.path")
												 + ". Continue using System.currentTimeMillis()");
		}
	}

	public static boolean isNativeTimerAvailable() {
		return nativeTimer;
	}

	public static long currentTimeMillis() {
		if (nativeTimer) {
			return (long) getTime();
		}
		else {
			return System.currentTimeMillis();
		}
	}

	public static double currentTimeMillisAsDouble() {
		if (nativeTimer) {
			return getTime();
		}
		else {
			return System.currentTimeMillis();
		}
	}


	public static void workForMillis(double milliSeconds) {
		double stop = currentTimeMillisAsDouble() + milliSeconds;
		while (currentTimeMillisAsDouble() < stop) {
			Thread.currentThread().yield();
		}
//        try {
//            Thread.sleep((long)milliSeconds);
//        } catch (InterruptedException e) {
//        }
	}

	public static void testWorkForMillis() {
		StopWatch stopWatch = makeNewWarmedStopWatch();
		for (double d = 0.01 ; d < 10 ; d += 0.01) {
			double sum = 0.0;
			double valueMin = Double.MAX_VALUE;
			double valueMax = 0;
			for (int j = 0 ; j < 100 ; j++) {
				stopWatch.start();
					TimeUtil.workForMillis(d);
				stopWatch.stop();
				valueMin = Math.min(valueMin, stopWatch.getDuration());
				valueMax = Math.max(valueMax, stopWatch.getDuration());
				sum += stopWatch.getDuration();
			}

			if (sum > 0){
				System.out.println("milliSeconds = " + d);
				System.out.println("valueMin = " + valueMin);
				System.out.println("valueMax = " + valueMax);
				System.out.println("errorAvg % = " + 100.0 * (sum / 100.0 - d) / d );
				System.out.println();
			}
		}
	}

	public static StopWatch makeNewStopWatch() {
		if (nativeTimer) return new StopWatchNative();
		return new StopWatchVM();
	}

	public static StopWatch makeNewWarmedStopWatch() {
		StopWatch stopWatch = makeNewStopWatch();
		for (int i = 0 ; i < 3000 ; ++i) {
			stopWatch.start();
			stopWatch.stop();
			stopWatch.getDuration();
		}
		return stopWatch;
	}

}

