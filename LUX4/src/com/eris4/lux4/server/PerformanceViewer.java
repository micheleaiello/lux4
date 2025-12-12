/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/10 13:40:21 $
* $Name:  $ - $Revision: 1.5 $
*/
package com.eris4.lux4.server;

import com.eris4.lux4.util.LUX4Properties;
import com.eris4.lux4.util.PerformanceMonitor;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;

public class PerformanceViewer implements Runnable {

    static private Logger logger = Logger.getLogger(PerformanceViewer.class);

    private RequestReceiver[] receiversList;
    PerformanceMonitor monitor;
    NumberFormat formatter = NumberFormat.getInstance();

    static private boolean shutdown = false;

    static private double lifeTimeRate;
    static private double currentRate;
    static private long numOfProcessedEvents;

    static public void createPerformanceViewer(RequestReceiver[] receivers) {
        logger.debug("Creating a PerformanceViewer.");
        PerformanceViewer viewer = new PerformanceViewer(receivers);
        Thread viewerThread = new Thread(viewer);
        int priority = Thread.MIN_PRIORITY;
        logger.debug("Setting PerformanceViewer priority = "+priority+".");
        viewerThread.setPriority(priority);
        logger.debug("Setting PerformanceViewer to Deamon Thread Mode.");
        viewerThread.setDaemon(true);
        logger.debug("Starting the PerformanceViewer.");
        viewerThread.start();
    }

    static public double getLifeTimeRatePerSec() {
        return lifeTimeRate;
    }

    static public double getCurrentRatePerSec() {
        return currentRate;
    }

    static public long getNumberOfProcessedEvents() {
        return numOfProcessedEvents;
    }

    private PerformanceViewer(RequestReceiver[] receiversList) {
        this.receiversList = receiversList;
        this.monitor = new PerformanceMonitor();
        this.shutdown = false;
    }

    public static void shutdownPerformanceViewer() {
        shutdown = true;
    }

    public void run() {
        monitor.start();
        long prevEvents = 0;
        long events = 0;
        int numOfIter = 0;

        int interval = 0;
        String intervalStr = LUX4Properties.getProperty("PerformanceViewerInterval");
        if (intervalStr != null) {
            interval = Integer.parseInt(intervalStr);
            logger.debug("PerformanceViewerInterval = "+interval);
        }

        int frequency = 10;
        String frequencyStr = LUX4Properties.getProperty("LifeTimeFrequency");
        if (frequencyStr != null) {
            frequency = Integer.parseInt(frequencyStr);
            logger.debug("LifeTimeFrequency = "+frequency);
        }

        PrintStream out = System.out;
        String outputStreamName = LUX4Properties.getProperty("PerformanceViewerOutputFile");
        logger.debug("PerformanceViewerOutputFile = "+outputStreamName);
        if (outputStreamName != null) {
            if (!outputStreamName.equals("console")) {
                try {
                    FileOutputStream outStream = new FileOutputStream(outputStreamName, true);
                    out = new PrintStream(outStream, true);
                } catch (FileNotFoundException e) {
                }
            }
        }

        while (!shutdown) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
            }

            if (shutdown) {
                break;
            }

            numOfIter++;

            events = 0;

            for (int i = 0; i < receiversList.length; i++) {
                events += receiversList[i].getNumberOfEventsHandled();
            }

            numOfProcessedEvents = events;

            if (events != prevEvents) {
                out.println("Total number of events processed = "+formatter.format(events));
                out.println("Current performances:");
                currentRate = monitor.lap(events-prevEvents, out);
                out.println();
                out.println();
                prevEvents = events;
            }
            else {
                currentRate = 0;
            }

            if ((numOfIter % frequency) == 0) {
                numOfIter = 0;
                out.println("LifeTime performances:");
                lifeTimeRate = monitor.print(events, out);
                out.println();
                out.println();
            }
        }
    }

}
