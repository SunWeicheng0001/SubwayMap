package com.sudalv.subway.util;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by SunWe on 2015/10/7.
 */
public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    private final Context myContext;

    public UncaughtExceptionHandler(Context context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        System.err.println(stackTrace);
        System.exit(10);
    }
}