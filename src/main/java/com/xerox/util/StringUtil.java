package com.xerox.util;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {
    /** The Constant EBCDIC_CHARSET. */
    public static final String EBCDIC_CHARSET = "Cp1047";

    /** The Constant EMPTY_STRING. */
    public static final String EMPTY_STRING = "";

    // Added due to threading issues that occurred with encoder/decoder for EBCIDIC: 
    /** The ebcdic encoder. */
    private static ThreadLocal<CharsetEncoder> ebcdicEncoder = new ThreadLocalEncoder();

    /** The ebcdic decoder. */
    private static ThreadLocal<CharsetDecoder> ebcdicDecoder = new ThreadLocalDecoder();

    /**
     * Converts <code>value</code> to a String, left-padding with zeros until the string is
     * <code>length</code> characters wide.
     *
     * @param value The value to convert
     * @param length the length
     * @return the string
     */
    public static String longToString(long value, int length) {
        return leftPadWithZeros(String.valueOf(value), length);
    }

    /**
     * Pads the string with <code>maxFieldLength - string.length()</code> blanks.
     *
     * @param string the string
     * @param maxFieldLength the max field length
     * @return the string
     */
    public static String rightPadWithBlanks(String string, int maxFieldLength) {
        if (string == null) {
            return EMPTY_STRING;
        } else {
            StringBuilder stringBuilder = new StringBuilder(string);
            int numOfBlanksToPad = maxFieldLength - string.length();

            for (int i = 0; i < numOfBlanksToPad; i++) {
                stringBuilder.append(' ');
            }
            string = stringBuilder.toString();
            return string;
        }
    }

    /**
     * Left-pads the given <code>numberString</code> with <code>0</code>'s until the string
     * length is <code>length</code> characters long.
     *
     * @param numberString the number string
     * @param length the length
     * @return the string
     */
    public static String leftPadWithZeros(String numberString, int length) {
        StringBuffer ret = new StringBuffer(numberString);

        while (ret.length() < length) {
            ret.insert(0, '0');
        }
        return ret.toString();
    }

    /**
     * Left-pads the given <code>numberString</code> with <code>0</code>'s until the string
     * length is <code>length</code> characters long.
     *
     * @param number the number
     * @param length the length
     * @return the string
     */
    public static String leftPadWithZeros(long number, int length) {
        StringBuffer ret = new StringBuffer(EMPTY_STRING + number);

        while (ret.length() < length) {
            ret.insert(0, '0');
        }
        return ret.toString();
    }

    /**
     * Returns a instance of <code>CharsetEncoder</code> that uses the Cp1047 (or IBM1047)
     * code-page (that is, EBCDIC).
     *
     * @return the eBCDIC encoder
     */
    public static CharsetEncoder getEBCDICEncoder() {
        // if (ebcdicEncoder == null) {
        // try {
        // Charset ebcdicCharset = Charset.forName("Cp1047");
        // ebcdicEncoder = ebcdicCharset.newEncoder();
        // } catch (UnsupportedCharsetException e) {
        // throw new RuntimeException("UnsupportedCharsetException " + e.toString());
        // }
        // }
        // return ebcdicEncoder;
        return ebcdicEncoder.get();
    }

    /**
     * Returns a instance of <code>CharsetEncoder</code> that uses the Cp1047 (or IBM1047)
     * code-page (that is, EBCDIC).
     *
     * @return the eBCDIC decoder
     */
    public static CharsetDecoder getEBCDICDecoder() {
        // if (ebcdicDecoder == null) {
        // try {
        // Charset ebcdicCharset = Charset.forName("Cp1047");
        // ebcdicDecoder = ebcdicCharset.newDecoder();
        // } catch (UnsupportedCharsetException e) {
        // throw new RuntimeException("UnsupportedCharsetException " + e.toString());
        // }
        // }
        // return ebcdicDecoder;
        return ebcdicDecoder.get();
    }

    /**
     * Converts the given <code>asciiString</code> to an EBCDIC byte-array.
     *
     * @param asciiString The String to conver to EBCDIC.
     * @return An array of bytes.
     */
    public static byte[] convertToEBCDIC(String asciiString) {
        try {
            CharBuffer charBuffer = CharBuffer.wrap(asciiString.toCharArray());
            ByteBuffer ebcdicBytes = StringUtil.getEBCDICEncoder().encode(charBuffer);

            return ebcdicBytes.array();
        } catch (CharacterCodingException e) { // N/A 
        }
        return null;
    }

    /**
     * Converts the given <code>asciiString</code> to an EBCDIC byte-array.
     *
     * @param ebcdicBytes the ebcdic bytes
     * @return An array of bytes.
     */
    public static String decodeFromEBCDIC(byte[] ebcdicBytes) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(ebcdicBytes);
            CharBuffer decode = StringUtil.getEBCDICDecoder().decode(byteBuffer);

            return decode.toString();
        } catch (CharacterCodingException e) { // N/A 
        }
        return null;
    }

    /**
     * Constructs a new String that consists of <code>numberOfSpaces</code> many
     * <code>spacer</code> characters.
     *
     * @param numberOfSpaces the number of spaces
     * @param spacer the spacer
     * @return the string
     */
    public static String makeString(int numberOfSpaces, char spacer) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < numberOfSpaces; i++) {
            sb.append(spacer);
        }
        return sb.toString();
    }

    /**
     * Constructs a new blank string (that is, a string containing all spaces) such that the
     * returned string contains <code>numberOfSpaces</code> space characters.
     *
     * @param numberOfSpaces the number of spaces
     * @return the string
     */
    public static String blank(int numberOfSpaces) {
        return makeString(numberOfSpaces, ' ');
    }

    /**
     * (Right) Pads the given string with spaces such that the returned string is <code>width</code>
     * characters long.
     *
     * @param string the string
     * @param width the width
     * @return the string
     */
    public static String pad(String string, int width) {
        return string + makeString(width - string.length(), ' ');
    }

    /**
     * (Right) Pads the given string with <code>padder</code> char such that the returned string
     * is <code>width</code> characters long.
     *
     * @param string the string
     * @param width the width
     * @param padder the padder
     * @return the string
     */
    public static String padWithChar(String string, int width, char padder) {
        return string + makeString(width - string.length(), padder);
    }

    /**
     * (Left) Pads the given string with spaces such that the returned string is <code>width</code>
     * characters long.
     *
     * @param string the string
     * @param width the width
     * @return the string
     */
    public static String leftPad(String string, int width) {
        return makeString(width - string.length(), ' ') + string;
    }

    /**
     * (Left) Pads the given string with <code>padder</code> char such that the returned string
     * is <code>width</code> characters long.
     *
     * @param string the string
     * @param width the width
     * @param padder the padder
     * @return the string
     */
    public static String leftPadWithChar(String string, int width, char padder) {
        return makeString(width - string.length(), padder) + string;
    }

    /**
     * Returns a blank string if the given <code>string</code> is null, otherwise returns
     * <code>string</code>.
     *
     * @param string the string
     * @return the string
     */
    public static String blankIfNull(String string) {
        if (string == null) {
            return EMPTY_STRING;
        } else {
            return string;
        }
    }

    /**
     * Given a string, replace the before with after.
     *
     * @param source the given source string
     * @param from the before value in the source string
     * @param to the new value in the source string
     * @return the string
     */
    public static String replace(String source, String from, String to) {
        if (source.indexOf(from.charAt(0)) == -1) {
            return source;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = 0;

        do {
            int j = source.indexOf(from, i);

            if (j == -1) {
                stringbuffer.append(source.substring(i, source.length()));
                return stringbuffer.toString();
            }
            stringbuffer.append(source.substring(i, j)).append(to);
            i = j + from.length();
        } while (true);
    }

    /**
     * To map.
     *
     * @param pairs the pairs
     * @return the map
     */
    public static Map<String, String> toMap(String[][] pairs) {
        Map<String, String> map = new HashMap<String, String>();

        for (String[] pair : pairs) {
            map.put(pair[0], pair[1]);
        }
        return map;
    }

    /**
     * Denormalize the IPD "magic 2-character HEX char-value" "encoding".
     *
     * @param normalized the normalized
     * @return the string
     */
    public static String denormalize(String normalized) {
        byte[] bytes = normalized.getBytes();

        if (bytes.length % 2 != 0) {
            throw new RuntimeException("String bytes must be a multiple of 2");
        }

        StringBuilder denormalized = new StringBuilder();

        for (int i = 0; i < bytes.length; i += 2) {
            String normalizedCharCode = new String(new byte[] { bytes[i], bytes[i + 1] });
            int charCode = Integer.parseInt(normalizedCharCode, 16);
            char c = (char) charCode;

            denormalized.append(c);
        }
        return denormalized.toString();
    }

    /**
     * Normalize a string using the IPD "magic 2-character HEX char-value" "encoding".
     *
     * @param denormalized the denormalized
     * @return the string
     */
    public static String normalize(String denormalized) {
        StringBuilder normalized = new StringBuilder();

        for (int i = 0; i < denormalized.length(); i++) {
            int charCode = denormalized.charAt(i);

            normalized.append(leftPadWithZeros(Integer.toHexString(charCode), 2));
        }
        return normalized.toString();
    }

    /**
     * Returns the string specified as a JavaName.
     *
     * @param s the s
     * @return the string
     */
    public static String asJavaName(String s) {
        StringBuffer stringbuffer = new StringBuffer();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (i == 0 && Character.isJavaIdentifierStart(c) || Character.isJavaIdentifierPart(c)) {
                stringbuffer.append(c);
            }
        }
        return stringbuffer.toString();
    }

    /**
     * This method returns false / true if the object is empty, null / otherwise.
     *
     * @param strCheck the str check
     * @return boolean
     */

    public static boolean isStringNotEmptyAndNull(String strCheck) {
        return (strCheck != null && !strCheck.trim().equals(EMPTY_STRING));
    }

    /**
     * This method validates the pattern of the unparsed string,
     * and returns true if it matches the pattern.
     * and returns false otherwise.
     *
     * @param valueString the value string
     * @param patternString the pattern string
     * @return boolean
     */
    public static boolean isPatternCorrect(String valueString, String patternString) {
        boolean resultVal = false;
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(valueString);

        if (matcher.matches()) {
            resultVal = true;
        }
        return resultVal;
    }

    /**
     * This method removes the leading spaces from a string, and returns the new string.
     *
     * @param sourceString the source string
     * @return String
     */
    public static String removeLeadingSpaces(String sourceString) {
        if (sourceString != null && !sourceString.trim().equals(EMPTY_STRING)) {
            return sourceString.replaceAll("^\\s+", EMPTY_STRING);
        } else {
            return sourceString;
        }
    }

    /**
     * This method is used for string compare.
     * 
     * @param leftString string to be compared against
     * @param rightString string to be compared
     * @param operator greater than, less than, equal 
     * 
     * @return true if the comparison is true
     */
    public static boolean stringCompare(String leftString, String rightString, char operator) {
        switch (operator) {
        case 'G':
            if (leftString.compareTo(rightString) > 0) {
                return true;
            }
            break;

        case 'L':
            if (leftString.compareTo(rightString) < 0) {
                return true;
            }
            break;

        case 'E':
            if (leftString.equals(rightString)) {
                return true;
            }
            break;
        }
        return false;
    }

    /**
     * This method is used for returning the last 'n' characters of a given string.
     * 
     * @param inputString string to be compared against.
     * @param numberOfChars string to be compared.
     * @return outputString - only the last 'numberOfChars' of the input string.
     */
    public static String getCharFromLast(String inputString, int numberOfChars) {
        String outputString = null;
        int length = inputString.length();

        if (length <= numberOfChars) {
            outputString = inputString;
        } else {
            int startIndex = length - numberOfChars;

            outputString = inputString.substring(startIndex);
        }
        return outputString;
    }

    /**
     * A simple numeric validation.
     *
     * @param inputString the input string
     * @return true, if is numeric
     */
    public static boolean isNumeric(String inputString) {
        boolean result = false;

        if (StringUtil.isStringNotEmptyAndNull(inputString)) {
            result = inputString.trim().matches("[0-9]*");
        }
        return result;
    }

    /**
     * A simple alphabet validation.
     *
     * @param inputString the input string
     * @return true, if is alphabet
     */
    public static boolean isAlphabet(String inputString) {
        boolean result = false;

        if (StringUtil.isStringNotEmptyAndNull(inputString)) {
            result = inputString.trim().matches("[a-zA-Z]*");
        }
        return result;
    }

    /**
     * A simple method to give the cycle number from an alphabet.
     * Note: this just gives the number value of the alphabets,
     * example for A - 1, B - 2, C - 3 and so on.
     *
     * @param inputString the input string
     * @return the numeric value
     */
    public static int getNumericValue(String inputString) {
        int resultNum = 0;
        char beginChar = 'A';

        inputString = inputString.trim().toUpperCase();
        if (StringUtil.isAlphabet(inputString) && inputString.trim().length() == 1) {
            resultNum = (int) inputString.charAt(0) - (int) beginChar + 1;
        }
        return resultNum;
    }

    /**
     * Use this method to get the entire stack trace to our Logs,
     * we don't have to see any messy stack traces in the console
     * anymore.
     *
     * @param objAnyException the obj any exception
     * @return result.toString() - represents the entire stack trace.
     */
    public static String getStackTraceString(Throwable objAnyException) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        objAnyException.printStackTrace(printWriter);
        return result.toString();
    }

    /**
     * The Class ThreadLocalEncoder.
     */
    private static final class ThreadLocalEncoder extends ThreadLocal<CharsetEncoder> {
        protected CharsetEncoder initialValue() {
            return Charset.forName(EBCDIC_CHARSET).newEncoder();
        }
    }

    /**
     * The Class ThreadLocalDecoder.
     */
    private static final class ThreadLocalDecoder extends ThreadLocal<CharsetDecoder> {
        protected CharsetDecoder initialValue() {
            return Charset.forName(EBCDIC_CHARSET).newDecoder();
        }
    }
    
    /**
	 * Produce a new file name using specified file name and suffix
	 * 
	 * @param oldFileName Specified file name
	 * @param suffix New suffix
	 * @return New file full name.
	 */
	public static String produceFileName(String oldFileName, String newSuffix) {

		if (newSuffix.charAt(0) != '.') {
			newSuffix = "." + newSuffix;
		}

		String newFileName = oldFileName + newSuffix;

		return newFileName;
	}
}

