package com.xerox.util;

import org.apache.commons.logging.Log;


public class Assertion {
    /**
     * If <code>object</code> is <code>null</code>, writes the given <code>errorMessage</code>
     * to <code>log</code> as a <i>severe</i> message and then throws a RuntimeException (using
     * <code>errorMessage</code> again in the exception).
     *
     * @param object the object
     * @param log the log
     * @param errorMessage the error message
     */
    public static void failIfNull(Object object, Log log, String errorMessage) {
        if (object == null) {
            logAndFail(log, errorMessage);
        }
    }

    /**
     * Fail if null.
     *
     * @param value the value
     * @param log the log
     * @param errorMessage the error message
     * @return the object
     */
    public static Object failIfNull(Object value, Log log, Message errorMessage) {
        failIfNull(value, log, errorMessage.toString());
        return value;
    }

    /**
     * Fail if null.
     *
     * @param value the value
     * @param log the log
     * @param errorMessage the error message
     * @return the string
     */
    public static String failIfNull(String value, Log log, Message errorMessage) {
        failIfNull(value, log, errorMessage.toString());
        return value;
    }

    /**
     * Fail if null or blank.
     *
     * @param value the value
     * @param log the log
     * @param errorMessage the error message
     * @return the string
     */
    public static String failIfNullOrBlank(String value, Log log, Message errorMessage) {
        failIfNull(value, log, errorMessage.toString());
        failIfTooShort(1, value.trim().length(), log, errorMessage);
        return value;
    }

    /**
     * Fail if null or too short.
     *
     * @param value the value
     * @param log the log
     * @param mininumFieldLength the mininum field length
     * @param errorMessage the error message
     * @return the string
     */
    public static String failIfNullOrTooShort(String value, Log log, int mininumFieldLength, String errorMessage) {
        failIfNull(value, log, errorMessage.toString());
        failIfTooShort(mininumFieldLength, value.length(), log, errorMessage);
        return value;
    }

    /**
     * Fail if too short.
     *
     * @param requiredLength the required length
     * @param actualLength the actual length
     * @param log the log
     * @param errorMessage the error message
     */
    public static void failIfTooShort(int requiredLength, int actualLength, Log log, Message errorMessage) {
        failIfTooShort(requiredLength, actualLength, log, errorMessage.toString());
    }

    /**
     * If <code>requiredLength</code> is less than <code>requiredLength</code>, writes the
     * given <code>errorMessage</code> to <code>log</code> as a <i>severe</i> message and then
     * throws a RuntimeException (using <code>errorMessage</code> again in the exception).
     *
     * @param requiredLength the required length
     * @param actualLength the actual length
     * @param log the log
     * @param errorMessage the error message
     */
    public static void failIfTooShort(int requiredLength, int actualLength, Log log, String errorMessage) {
        if (actualLength < requiredLength) {
            logAndFail(log, errorMessage);
        }
    }

    /**
     * If <code>failureOccurred</code> is true, then the given <code>errorMessage</code> is
     * written to the log as a severe error message and a RuntimeException is thrown.
     *
     * @param condition the condition
     * @param log the log
     * @param errorMessage the error message
     */
    public static void failIfTrue(boolean condition, Log log, String errorMessage) {
        if (condition) {
            logAndFail(log, errorMessage);
        }
    }

    /**
     * Fail if true.
     *
     * @param condition the condition
     * @param log the log
     * @param errorMessage the error message
     */
    public static void failIfTrue(boolean condition, Log log, Message errorMessage) {
        failIfTrue(condition, log, errorMessage.toString());
    }

    /**
     * If <code>failureOccurred</code> is true, then the given <code>errorMessage</code> is
     * written to the log as a severe error message and a RuntimeException is thrown.
     *
     * @param condition the condition
     * @param log the log
     * @param errorMessage the error message
     */
    public static void failIfFalse(boolean condition, Log log, String errorMessage) {
        if (!condition) {
            logAndFail(log, errorMessage);
        }
    }

    /**
     * Fail if false.
     *
     * @param condition the condition
     * @param log the log
     * @param errorMessage the error message
     */
    public static void failIfFalse(boolean condition, Log log, Message errorMessage) {
        failIfTrue(!condition, log, errorMessage.toString());
    }

    /**
     * Fail if not in list.
     *
     * @param value the value
     * @param validValues the valid values
     * @param log the log
     * @param propertyName the property name
     */
    public static void failIfNotInList(String value, String[] validValues, Log log, String propertyName) {
        for (String validValue : validValues) {
            if (validValue != null && validValue.equals(value)) {
                return;
            }
        }
        logAndFail(log, "Value '" + value + "' is not a valid value for property " + propertyName + ".");
    }

    /**
     * Writes the given <code>errorMessage</code> to the <code>log</code> and then throws a
     * RuntimeException (containing the same <code>errorMessage</code>).
     *
     * @param log the log
     * @param errorMessage the error message
     */
    public static void logAndFail(Log log, String errorMessage) {
        RuntimeException runtimeException = new RuntimeException(errorMessage);

        log.fatal(errorMessage, runtimeException);
        throw runtimeException;
    }

    /**
     * Log and fail.
     *
     * @param log the log
     * @param errorMessage the error message
     */
    public static void logAndFail(Log log, Message errorMessage) {
        logAndFail(log, errorMessage.toString());
    }

    /**
     * Log and fail.
     *
     * @param log the log
     * @param e the e
     */
    public static void logAndFail(Log log, Exception e) {
        logAndFail(log, e, "An exception occurred.");
    }

    /**
     * Writes the given <code>errorMessage</code> to the <code>log</code> and then throws a
     * RuntimeException (containing the same <code>errorMessage</code>).
     *
     * @param log the log
     * @param e the e
     * @param errorMessage the error message
     */
    public static void logAndFail(Log log, Exception e, String errorMessage) {
        String message = errorMessage + " Cause was: " + e.getClass().getName() + " - " + e.getMessage();

        log.fatal(message, e);
        throw new RuntimeException(message, e);
    }

    /**
     * Log and fail.
     *
     * @param log the log
     * @param e the e
     * @param errorMessage the error message
     */
    public static void logAndFail(Log log, Exception e, Message errorMessage) {
        logAndFail(log, e, errorMessage.toString());
    }
}

