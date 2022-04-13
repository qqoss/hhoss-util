/*
 * Created on 2008-4-11
 * 
 * Copyright (c) 2006 Prudential Services Asia. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Prudential Services Asia. ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Prudential.
 */
package com.hhoss.util;

import java.sql.Date;
import java.text.SimpleDateFormat;


/**
 * String util
 * @author kejun
 *
 */
public class StringUtil {
	public static final int SPLIT_BITS = 3; // this number for split

	/**
	 * 
	 * reverse the input string and return the reversed string
	 * 
	 * @param args
	 * 
	 */
	public static String reverse(String srcStr) {
		if (srcStr == null) {
			return null;
		}
		String str = srcStr.trim();
		int len = str.length();
		char[] array = new char[len];
		for (int i = len - 1, j = 0; i >= 0; i--, j++) {
			array[j] = str.charAt(i);
		}
		return new String(array);
	}

	/**
	 * return current date format example "yyyy-MM-dd","yyyy/MM/dd"...,default
	 * is yyyy-MM-dd
	 * 
	 * @param format
	 * @return
	 */
	public static Date getCurrentDate(String format) {
		if (format == null) {
			return Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
		} else {
			return Date.valueOf(new SimpleDateFormat(format).format(new java.util.Date()));
		}
	}

	/**
	 * format string as appointed formatter, especially for number;cut every
	 * Constant.SPLIT_BITS add a signal for example 1234567 if signal is
	 * ',',should be output as 1,234,567
	 * 
	 * @param str
	 * @return
	 * 
	 */
	public static String formatNumber(String str, String signal) {
		int bit = SPLIT_BITS;
		String s = "";
		if (str.length() > bit) {
			int mod = str.length() % bit;
			if (mod != 0) {
				s = s + str.substring(0, mod) + signal;
			}
			for (int i = mod; i < str.length(); i += bit) {
				s = s + str.substring(i, i + bit) + signal;
			}
			return s.substring(0, s.length() - 1);
		} else {
			return str;
		}
	}

}
