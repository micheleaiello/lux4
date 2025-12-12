/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.4 $
*/
package com.eris4.lux4.util;

import java.io.PrintStream;
import java.text.NumberFormat;

public final class PerformanceMonitor {
    private long t0;
    private long prev;

    public PerformanceMonitor() {
        t0 = System.currentTimeMillis();
        prev = t0;
    }

    public final void start() {
        t0 = System.currentTimeMillis();
        prev = t0;
    }

    public final double lap(long n, PrintStream out) {
        long now = System.currentTimeMillis();
        long delta = now - prev;
        prev = now;
        NumberFormat nf = NumberFormat.getInstance();
        out.println("Duration: " + nf.format(delta) + "ms");
        out.println("Events  : " + nf.format(n));
        out.println("Rate    : " + nf.format((double) n / (double) delta) + " KEvents/s, "
                + nf.format((double) n / ((double) delta) * 3.6) + " MEvents/h");
        return ((double) n / (double) delta) * (double) 1000;
    }

    public final double stop(long n, PrintStream out) {
        long delta = System.currentTimeMillis() - t0;
        NumberFormat nf = NumberFormat.getInstance();
        out.println("Duration: " + nf.format(delta) + "ms");
        out.println("Events  : " + nf.format(n));
        out.println("Rate    : " + nf.format((double) n / (double) delta) + " KEvents/s, "
                + nf.format((double) n / ((double) delta) * 3.6) + " MEvents/h");
        return ((double) n / (double) delta) * (double) 1000;
    }

    public final double print(long n, PrintStream out) {
        long delta = System.currentTimeMillis() - t0;
        NumberFormat nf = NumberFormat.getInstance();
        out.println("Duration: " + nf.format(delta) + "ms");
        out.println("Events  : " + nf.format(n));
        out.println("Rate    : " + nf.format((double) n / (double) delta) + " KEvents/s, "
                + nf.format((double) n / ((double) delta) * 3.6) + " MEvents/h");
        return ((double) n / (double) delta) * (double) 1000;
    }

}