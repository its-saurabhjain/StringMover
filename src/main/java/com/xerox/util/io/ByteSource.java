package com.xerox.util.io;


public interface ByteSource {
	/**
	 * Get <code>numberOfBytes</code> from this ByteSource. It the requested number of bytes
	 * cannot be provided, then an OutOfBytesException is thrown.
	 */
	public byte[] getBytes(int numberOfBytes) throws OutOfBytesException;

	/**
	 * Reads 4 bytes from this ByteSource, converting them to a 32-bit integer
	 * using the first byte read as the most significant byte of the integer, and the last byte read
	 * as the least-significant byte of the integer.
	 */
	public int readInt() throws OutOfBytesException;

	/**
	 * Returns true if more bytes are available, or false otherwise.
	 *
	 * @return
	 */
	public boolean moreBytesAvailable();

	/**
	 * Returns true if at least <code>numBytes</code> bytes are available.
	 */
	public boolean moreBytesAvailable(int numBytes);

	/**
	 * Close the underlying physical byte source, if any.
	 */
	public void close();
}
