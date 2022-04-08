package com.hhoss.lang;


import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.hhoss.jour.Logger;

/**
 * TimeUtil for period handle
 * @author kejun
 *
 */
public class TimeUtil {
	private static long[] units = new long[15];
	private static final ConcurrentDateFormat DF = new ConcurrentDateFormat("yyyy-MM-dd HH:mm:ss");
	//protected static DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static{
		initUnits();
		setTimeZone("GMT+8");
		//DF.setTimeZone(TimeZone.getDefault());
	}
	
	private static void initUnits(){
		units[Calendar.MILLISECOND]=1; //14
		units[Calendar.SECOND]=1000; //13
		units[Calendar.MINUTE]=60*1000;//12
		units[Calendar.HOUR_OF_DAY] =60*60*1000;//11
		units[Calendar.DAY_OF_MONTH]=24*60*60*1000;//5
		units[Calendar.WEEK_OF_YEAR]=7*24*60*60*1000; //3

		//below is unfixed value, need to handle;
		units[Calendar.MONTH]=30*units[Calendar.DAY_OF_MONTH]; //2
		units[Calendar.YEAR]=365*units[Calendar.DAY_OF_MONTH]; //1
	}
	
	public static void setTimeZone(String tzId){
		//"GMT+8";
		TimeZone.setDefault(TimeZone.getTimeZone(tzId));
	}
	
	public static String getTime(){
		return DF.format(new Date(System.currentTimeMillis()));
	}
	
	public static Date getLast(String configTime, String intervals, String field){
		return getLast(configTime, Integer.parseInt(intervals),Integer.parseInt(field));
	}
	
	public static Date getNext(String configTime, String intervals, String field){
		return getNext(configTime, Integer.parseInt(intervals),Integer.parseInt(field));
	}
	
	public static Date getLast(String configTime, int intervals, int field){
		Calendar configCalendar = Calendar.getInstance();
		Calendar systemCalendar = Calendar.getInstance();
		
		try {
			//String currentTime = DF.format(new Date(System.currentTimeMillis()));
			//systemCalendar.setTimeInMillis(System.currentTimeMillis());
			//systemCalendar.setTime(DF.parse(currentTime));
			configCalendar.setTime(DF.parse(configTime));
		} catch (ParseException e) {
			return null;
		}
/*
		if(systemCalendar.before(configCalendar)){
			return new Date(configCalendar.getTimeInMillis());
		}
*/		
		int passedPeriods = getPeriods(configCalendar, systemCalendar, field, intervals);
		int periods = passedPeriods<0?-1:passedPeriods;
		
		return getDate(configCalendar, field, periods, intervals);
	}
	
	public static Date getNext(String configTime, int intervals, int field){
		Calendar configCalendar = Calendar.getInstance();
		Calendar systemCalendar = Calendar.getInstance();
		
		try {
			//String currentTime = DF.format(new Date(System.currentTimeMillis()));
			//systemCalendar.setTime(DF.parse(currentTime));
			//systemCalendar.setTime(new Date(System.currentTimeMillis()));
			//systemCalendar.setTimeInMillis(System.currentTimeMillis());
			//systemCalendar.setTimeInMillis(DF.parse(currentTime).getTime());
			//systemCalendar.setTime(DF.parse(currentTime));
			configCalendar.setTime(DF.parse(configTime));
		} catch (ParseException e) {
			return null;
		}
/*
		if(systemCalendar.before(configCalendar)){
			return new Date(configCalendar.getTimeInMillis());
		}
*/		
		int passedPeriods = getPeriods(configCalendar, systemCalendar, field, intervals);
		int periods = passedPeriods<0?0:passedPeriods+1;
		
		return getDate(configCalendar, field, periods, intervals);
	}
	
	private static int getPeriods(Calendar from,Calendar end, int field, int intervals){
		int passedPeriods;
		switch(field){
		  case Calendar.YEAR:
			int difYears1 = end.get(field)-from.get(field);
			passedPeriods = ((int)(difYears1/intervals));
			break;
		  case Calendar.MONTH:
			int difYears2 = end.get(Calendar.YEAR)-from.get(Calendar.YEAR);
			int difMonths = difYears2*12 + end.get(field)-from.get(field);
			passedPeriods = (int)(difMonths/intervals);
			break;
		  case Calendar.WEEK_OF_YEAR:
		  case Calendar.DAY_OF_MONTH:
		  case Calendar.HOUR_OF_DAY:
		  case Calendar.MINUTE:
		  case Calendar.SECOND:
		  case Calendar.MILLISECOND:
		  default:
			long difMillis = end.getTimeInMillis()-from.getTimeInMillis();
		  	passedPeriods = (int)(difMillis/(intervals*units[field]));
		}
		return passedPeriods;

	}
	
	private static Date getDate(Calendar cal, int field, int peroids, int intervals){
		cal.add(field, peroids*intervals);
		return new Date(cal.getTimeInMillis());
	}

	public static String getString(Date date){
		return DF.format(date);
	}	
	
	private static Logger logger = Logger.get();
	public static void main(String[] args){
		String intervals = "3";
		int field = Calendar.HOUR_OF_DAY;
		//TimeZone tz = TimeZone.getTimeZone("GMT+8");
		//TimeZone.setDefault(tz);
		//String time = DF.format(new Date(System.currentTimeMillis()-1*units[field]));
		String time = "2011-1-19 13:55:21";
		logger.info("getLast:{}",DF.format(getLast(time,intervals,String.valueOf(field))));
		logger.info("getNext:{}",DF.format(getNext(time,intervals,String.valueOf(field))));
		//System.out.println(getNext2(time,intervals,String.valueOf(field))[0]);
	}
	
}
