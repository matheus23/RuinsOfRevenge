package org.matheusdev.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 * Author: matheusdev
 * Date: 3/17/13
 * Time: 12:39 PM
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static String stackTraceToString(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        e.printStackTrace(pwriter);
        return writer.toString();
    }
}
