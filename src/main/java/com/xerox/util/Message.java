package com.xerox.util;


import java.io.File;

import org.apache.commons.lang.StringUtils;


public class Message {
    /** The message. */
    private transient String message;

    /**
     * Instantiates a new message.
     *
     * @param message the message
     */
    public Message(String message) {
        this.message = message;
    }

    /**
     * Subst.
     *
     * @param name the name
     * @param value the value
     * @return the message
     */
    public Message subst(String name, String value) {
        return new Message(StringUtils.replace(message, placeholder(name), value));
    }

    /**
     * Subst.
     *
     * @param name the name
     * @param value the value
     * @return the message
     */
    public Message subst(String name, File value) {
        return new Message(StringUtils.replace(message, placeholder(name), value.getAbsolutePath()));
    }

    /**
     * Subst.
     *
     * @param name the name
     * @param value the value
     * @return the message
     */
    public Message subst(String name, int value) {
        return subst(name, "" + value);
    }

    /**
     * Subst.
     *
     * @param name the name
     * @param value the value
     * @return the message
     */
    public Message subst(String name, long value) {
        return subst(name, "" + value);
    }

    /**
     * Constructs the string <code>"${" + fieldName + "}"</code>.
     *
     * @param fieldName the field name
     * @return the string
     */
    private static String placeholder(String fieldName) {
        return "${" + fieldName + "}";
    }

    public String toString() {
        return this.message;
    }
}

