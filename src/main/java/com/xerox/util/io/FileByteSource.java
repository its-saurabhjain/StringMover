package com.xerox.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class FileByteSource implements ByteSource { 
	private static Log log = LogFactory.getLog(FileByteSource.class);

	private static final int DEFAULT_BUFFER_SIZE = 2000;
	protected ByteBuffer buffer = null;
	private ByteBuffer tmp = null;
	protected FileChannel inputChannel = null;
	protected long bytesDispensed = 0;

	/**
	 * Save off the record length for later X9 file writing
	 */
	private byte[] recordLengthBytes = {0x00, 0x00, 0x00, 0x50};
	
	public byte[] getRecordLengthBytes() {
		return recordLengthBytes;
	}

	public void setRecordLengthBytes(byte[] recordLengthBytes) {
		this.recordLengthBytes = recordLengthBytes;
	}

	/**
	 * If true then the end of file has been reached.
	 */
	private boolean atEndOfFile;

	private final FileInputStream fileInputStream;

	public FileByteSource(String filename) throws FileNotFoundException {
		this(filename, DEFAULT_BUFFER_SIZE);
	}

	public FileByteSource(String filename, int bufferSize) throws FileNotFoundException {
		this(new FileInputStream(filename), bufferSize);
	}

	public FileByteSource(File input, int bufferSize) throws FileNotFoundException {
		this(new FileInputStream(input), bufferSize);
	}

	public FileByteSource(File input) throws FileNotFoundException {
		this(input, DEFAULT_BUFFER_SIZE);
	}

	public FileByteSource(FileInputStream fin) {
		this(fin, DEFAULT_BUFFER_SIZE);
	}

	public FileByteSource(FileInputStream fin, int bufferSize) {
		buffer = ByteBuffer.allocate(bufferSize);
		tmp = ByteBuffer.allocate(bufferSize);
		buffer.limit(0);
		fileInputStream = fin;
		inputChannel = fileInputStream.getChannel();
	}

	public byte[] getBytes(int numberOfBytes) throws OutOfBytesException {
		failIfNumberOfBytesIsNegative(numberOfBytes);
		log.debug("getBytes() - " + numberOfBytes + " bytes requested.");
		if (buffer.remaining() >= numberOfBytes) {
			// Enough bytes in buffer to immediately fulfill the request.
			log.debug("getBytes() - buffer.remaining() == " + buffer.remaining() + ", so fulfilling from buffer.");
			byte[] bytes = new byte[numberOfBytes];
			buffer.get(bytes);
			log.debug("getBytes() - position (for next get): " + buffer.position() + " (0x" + Integer.toHexString(buffer.position()).toUpperCase() +
					"), Remaining: " + buffer.remaining() + ", Capacity: " + buffer.capacity());
			bytesDispensed += numberOfBytes;
			return bytes;
		}

		log.debug("getBytes() - Not enough bytes in buffer.  Loading more...");
		loadMoreBytesFromFile(numberOfBytes);

		/*
		 * Now check if we have enough bytes (we might have reached the end of the file and so the
		 * buffer might not be full enough to fufill the request for bytes)
		 */
		if (buffer.remaining() >= numberOfBytes) {
			byte[] bytes = new byte[numberOfBytes];
			buffer.get(bytes);
			return bytes;
		}
		String message = numberOfBytes + " bytes were requested, but only " + buffer.remaining() + ((buffer.remaining() <= 1) ? " was " : " were ") +
				"available.";
		log.error(message);
		throw new OutOfBytesException(message);
	}

	private void failIfNumberOfBytesIsNegative(int numberOfBytes) throws OutOfBytesException {
		if (numberOfBytes < 0) {
			String msg = "getBytes was called with a negative value for 'numberOfBytes'.  Value was: " + numberOfBytes;
			OutOfBytesException e = new OutOfBytesException(msg);
			log.fatal(msg, e);
			throw e;
		}
	}

	private void loadMoreBytesFromFile(int numberOfBytes) throws OutOfBytesException {
		log.debug("loadMoreBytesFromFile() - trying to supply " + numberOfBytes + " bytes.");
		if (numberOfBytes > buffer.capacity()) {
			log.debug("loadMoreBytesFromFile() - current buffer size is " + buffer.capacity() + ", so resizing to " + numberOfBytes + " bytes.");
			/*
			 * numberOfBytes is greater than current buffer capacity, so increase buffer size
			 */
			storeRemainingBytesInTmp();
			buffer = ByteBuffer.allocate(numberOfBytes);
			buffer.put(tmp); // Copy bytes from tmp into the new buffer
			// Resize tmp so it can store the contents of buffer next time we need to
			tmp = ByteBuffer.allocate(numberOfBytes);
		} else {
			/*
			 * Buffer size is ok, but it's holding too few bytes. So, move current bytes to the
			 * start of buffer, and then fill up with more bytes from the file.
			 */
			if (buffer.remaining() > 0) {
				storeRemainingBytesInTmp();
				buffer.clear();
				buffer.put(tmp);
			} else {
				buffer.clear();
			}
		}
		try {
			int availableSlots = buffer.remaining();
			int bytesRead = inputChannel.read(buffer);
			log.debug("loadMoreBytesFromFile() - read " + bytesRead + " bytes from file.");
			atEndOfFile = (bytesRead == -1) || (bytesRead < availableSlots);
		} catch (IOException e) {
			String msg = "loadMoreBytesFromFile() - Failure occurred while trying to load bytes from file.  Reason was: " + e.getMessage();
			log.fatal(msg, e);
			throw new OutOfBytesException(msg, e);
		}
		buffer.flip(); // Switch to read mode
	}

	/**
	 * Copies the bytes currently in buffer into tmp.
	 */
	private void storeRemainingBytesInTmp() {
		tmp.clear();
		tmp.put(buffer);
		tmp.flip();
	}

	public boolean moreBytesAvailable() {
		return moreBytesAvailable(1);
	}

	public boolean moreBytesAvailable(int numBytes) {
		log.debug("moreBytesAvailable() called - numBytes=" + numBytes);
		if (buffer.remaining() >= numBytes) {
			log.debug("moreBytesAvailable() - returning true (can fulfill from buffer)");
			return true;
		}

		// Since remaining == 0, first check for more bytes in the file
		if (atEndOfFile) {
			log.debug("moreBytesAvailable() - at end of file and not enough bytes in buffer, so returning false");
			return false; // No luck, since we're at the end of the file
		}

		// Not at EOF, so see if there are more bytes in the file
		try {
			log.debug("moreBytesAvailable() - try load more bytes from file and see if there are enough bytes left...");
			loadMoreBytesFromFile(numBytes);
		} catch (OutOfBytesException e) {
			return false;
		}
		boolean enoughBytes = buffer.remaining() >= numBytes;
		log.debug("moreBytesAvailable() - Enough bytes after loading from file: " + enoughBytes);
		return (enoughBytes);
	}

	public void close() {
		if (this.fileInputStream != null) {
			try {
				this.fileInputStream.close();
			} catch (IOException e) {
				log.info("Error closing file: " + e.getMessage(), e);
			}
		}
	}
	
	public int readInt(boolean isBigEndian) throws OutOfBytesException {
		byte[] bytes = getBytes(4);
		//save this value off for possible writing later:
		recordLengthBytes = bytes;
		
		int i0 = unsigned(bytes[0]);
		int i1 = unsigned(bytes[1]);
		int i2 = unsigned(bytes[2]);
		int i3 = unsigned(bytes[3]);
		int value = 0;

		if (isBigEndian)
			value = (i0 << 24)  + (i1 << 16) + (i2 << 8) + (i3 << 0);
		else 
			value = (i3 << 24)  + (i2 << 16) + (i1 << 8) + (i0 << 0);

		return value;
	}

	/**
	 * Handling for Little Endian/Intel byte order
	 */
	public int readIntLittleEndian() throws OutOfBytesException {
		return readInt(false);
	}
	
	/**
	 * Default is Big Endian/Motorola byte order
	 */
	public int readIntBigEndian() throws OutOfBytesException {
		return readInt(true);
	}

	/**
	 * Default is Big Endian/Motorola byte order
	 */
	public int readInt() throws OutOfBytesException {
		return readInt(true);
	}

	/**
	 * Returns an unsigned value from the given <code>b</code>.
	 */
	private int unsigned(byte b) {
		if (b < 0) {
			return 256 + b;
		}
		return b;
	}
}
