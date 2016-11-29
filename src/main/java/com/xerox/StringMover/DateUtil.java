package com.xerox.StringMover;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	//////////////////////////////////////////   sec     min  hr   day  wk  mon  year
	private static final long MILLIS_PER_YEAR  = 1000L * 60 * 60 * 24 *          365;
	private static final long MILLIS_PER_MONTH = 1000L * 60 * 60 * 24 *     30;
	private static final long MILLIS_PER_WEEK  = 1000L * 60 * 60 * 24 * 7;
	private static final long MILLIS_PER_DAY   = 1000L * 60 * 60 * 24;
	private static final long MILLIS_PER_HOUR  = 1000L * 60 * 60;
	private static final long MILLIS_PER_MIN   = 1000L * 60;
	private static final String DIVIDER = ", ";
	
	public static String millisToString(long millis) {
		String result = "";
		
		while( millis > 0 ) {
			if( millis > MILLIS_PER_YEAR ) {
				if( !result.equals("") )
					result += DIVIDER;
				long count = millis / MILLIS_PER_YEAR;
				result += count + " year" + (count>1?"s":"");
				millis -= count * MILLIS_PER_YEAR;
			} else if( millis > MILLIS_PER_MONTH ) {
				if( !result.equals("") )
					result += DIVIDER;
				long count = millis / MILLIS_PER_MONTH;
				result += count + " month" + (count>1?"s":"");
				millis -= count * MILLIS_PER_MONTH;
			} else if( millis > MILLIS_PER_WEEK ) {
				if( !result.equals("") )
					result += DIVIDER;
				long count = millis / MILLIS_PER_WEEK;
				result += count + " week" + (count>1?"s":"");
				millis -= count * MILLIS_PER_WEEK;
			} else if( millis > MILLIS_PER_DAY ) {
				if( !result.equals("") )
					result += DIVIDER;
				long count = millis / MILLIS_PER_DAY;
				result += count + " day" + (count>1?"s":"");
				millis -= count * MILLIS_PER_DAY;
			} else if( millis > MILLIS_PER_HOUR ) {
				if( !result.equals("") )
					result += DIVIDER;
				long count = millis / MILLIS_PER_HOUR;
				result += count + " hour" + (count>1?"s":"");
				millis -= count * MILLIS_PER_HOUR;
			} else if( millis > MILLIS_PER_MIN ) {
				if( !result.equals("") )
					result += DIVIDER;
				long count = millis / MILLIS_PER_MIN;
				result += count + " minute" + (count>1?"s":"");
				millis -= count * MILLIS_PER_MIN;
			} else {
				if( !result.equals("") )
					result += DIVIDER;
				double count = millis / 1000.0;
				result += count + " second" + (count!=1.0?"s":"");
				millis = 0;
			} 
		}
		
		return result;
	}
	
	public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
}
