package com.clayoverwind.simpleioc.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */

public class LogUtil {
    private static Logger globalLogger = Logger.getGlobal();
    public static void setLogger(Logger logger) {
        globalLogger = logger;
    }

    public static Logger getLogger(final Class<?> c) {
        return Logger.getLogger(c.getName());
    }

    public static void info(final String msg) {
        globalLogger.log(Level.INFO, msg);
    }

    public static String getKVLogString(final String mainMsg, final Object... objects) {
        if (mainMsg == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder("[".concat(mainMsg).concat("]"));
        if (objects.length != 0) {
            sb.append("\t");
            int i;
            for (i = 0; i < objects.length - 1; i += 2) {
                sb.append(objects[i]).append(":").append(objects[i + 1]).append("; ");
            }
            if (i == objects.length - 1)
                sb.append(objects[objects.length - 1]);
        }
        return sb.toString();
    }
}
