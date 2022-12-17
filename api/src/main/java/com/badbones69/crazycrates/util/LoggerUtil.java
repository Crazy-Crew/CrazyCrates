package com.badbones69.crazycrates.util;

import com.badbones69.crazycrates.CrazyCrates;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerUtil {

    private static final CrazyLogger crazyLogger = new CrazyLogger("CrazyLogger");

    /**
     * Sends a debug message.
     *
     * @param message message to send
     */
    public static void debug(String message) {
        info("<yellow>[DEBUG]</yellow> " + message);
    }

    /**
     * Sends a debug message.
     *
     * @param message message to send
     * @param exception exception to check
     */
    public static void debug(String message, Exception exception) {
        info("<yellow>[DEBUG]</yellow> " + message);

        if (exception != null) exception.printStackTrace();
    }

    /**
     * Sends an info message.
     *
     * @param message message to send
     */
    public static void info(String message) {
        crazyLogger.info(message);
    }

    /**
     * Sends a warning message.
     *
     * @param message message to send
     */
    public static void warn(String message) {
        crazyLogger.warning(message);
    }

    /**
     * Sends a severe message.
     *
     * @param message message to send
     */
    public static void severe(String message) {
        crazyLogger.severe(message);
    }

    public static class CrazyLogger extends Logger {

        public CrazyLogger(String loggerName) {
            super(loggerName, null);

            LogManager.getLogManager().addLogger(this);
        }

        public void info(String message) {
            CrazyCrates.api().getConsole().send(AdventureUtil.parse(message));
        }

        @Override
        public void info(Supplier<String> msgSupplier) {
            info(msgSupplier.get());
        }

        private void doLog(LogRecord record) {
            if (record.getLevel() == Level.INFO) {
                info(record.getMessage());
                return;
            }

            record.setLoggerName(getName());

            log(record);
        }

        /*
         * The rest of this is just copying methods in order to use the modified doLog private method above..
         */
        @Override
        public void log(Level level, String msg) {
            if (!isLoggable(level)) return;

            LogRecord record = new LogRecord(level, msg);

            doLog(record);
        }

        @Override
        public void log(Level level, Supplier<String> msgSupplier) {
            if (!isLoggable(level)) return;

            LogRecord record = new LogRecord(level, msgSupplier.get());

            doLog(record);
        }

        @Override
        public void log(Level level, String msg, Object param1) {
            if (!isLoggable(level)) return;

            LogRecord record = new LogRecord(level, msg);

            Object[] params = {param1};

            record.setParameters(params);

            doLog(record);
        }

        @Override
        public void log(Level level, String msg, Object[] params) {
            if (!isLoggable(level)) return;

            LogRecord record = new LogRecord(level, msg);

            record.setParameters(params);

            doLog(record);
        }

        @Override
        public void log(Level level, String msg, Throwable thrown) {
            if (!isLoggable(level)) return;

            LogRecord record = new LogRecord(level, msg);

            record.setThrown(thrown);

            doLog(record);
        }

        @Override
        public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
            if (!isLoggable(level)) return;

            LogRecord record = new LogRecord(level, msgSupplier.get());

            record.setThrown(thrown);

            doLog(record);
        }

        @Override
        public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
            if (!isLoggable(Level.FINER)) return;

            LogRecord record = new LogRecord(Level.FINER, "THROW");

            record.setSourceClassName(sourceClass);
            record.setSourceMethodName(sourceMethod);
            record.setThrown(thrown);

            doLog(record);
        }
    }
}