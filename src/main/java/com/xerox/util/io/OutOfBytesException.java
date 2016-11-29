package com.xerox.util.io;


public class OutOfBytesException extends Exception {
	private static final long serialVersionUID = 8315328156620484940L;

	public OutOfBytesException(String message, Throwable cause) {
		super(message, cause);
	}

	public OutOfBytesException(String message) {
		super(message);
	}

	public OutOfBytesException() {
	}
}
