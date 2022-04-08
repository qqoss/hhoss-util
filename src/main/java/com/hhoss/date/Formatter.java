package com.hhoss.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hhoss.util.thread.ThreadHold;

public class Formatter {
	private static final String STANDARD ="yyyy-MM-dd HH:mm:ss";
	
	/**
	 * thread safe date format, don't pass the result to another thread.
	 * @param pattern
	 * @return
	 */
	private static DateFormat get(String pattern){
		// SimpleDateFormat.getDateTimeInstance(3,3);("yyyy-M-d HH:mm:ss.SSS");
		DateFormat df = ThreadHold.get(pattern);
		if( df==null ){
			df=ThreadHold.get(pattern,new SimpleDateFormat(pattern));
		}
		return df;
	}
	
	/**
	 *  thread safe date format; with standard "yyyy-MM-dd HH:mm:ss"
	 * @param date
	 * @return value by pattern 
	 */
	public static String format(Date date){
 		return format(date,STANDARD);
 	}
	/**
	 *  thread safe date format; with standard "yyyy-MM-dd HH:mm:ss"
	 * @param date
	 * @return date from pattern
	 */
	public static Date parse(String date){
 		return parse(date,STANDARD);
 	}

 	/**
 	 * thread safe date format;
 	 * @param pattern
 	 * @param date
 	 * @return string of formatted 
 	 */
 	public static String format(Date date,String pattern){
 		return get(pattern).format(date);
 	}

 	/**
	 * thread safe date parse;
 	 * @param date
 	 * @param pattern
 	 * @return
 	 */
 	public static Date parse(String date,String pattern){
 		try {
			return get(pattern).parse(date);
		} catch (ParseException e) {
			return null;
		}
 	}


}
