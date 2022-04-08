package com.hhoss.lang;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConcurrentDateFormat extends ThreadLocal<DateFormat>{

	private String pattern ;
	
	/**
	 * using default pattern "yyyyMMdd-HHmmss";
	 */
	public ConcurrentDateFormat(){
		this("yyyyMMdd-HHmmss");
	}
	
	public ConcurrentDateFormat(String pattern){
		this.pattern=pattern;
	}
	
	protected synchronized DateFormat initialValue() {
		return new SimpleDateFormat(pattern);
	}

	public Date parse(String text) throws ParseException {
		return get().parse(text);
	}

	public String format(Date date) {
		return get().format(date);
	}
}