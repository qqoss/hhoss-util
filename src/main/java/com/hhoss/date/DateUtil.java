package com.hhoss.date;

import java.sql.Date;
import java.text.DecimalFormat;

/**
 * date convert util
 * @author kejun
 *
 */
public class DateUtil {

	/**
	 * @param s date string
	 * @return date
	 */
	public static Date toDate(String s) {
		if (null == s || "".equals(s)) {
			return null;
		} else {
			if (s.indexOf(":") < 0) {
				return Date.valueOf(s);
			} else if (s.indexOf(":") != s.lastIndexOf(":")) {
				return new Date(java.sql.Timestamp.valueOf(s).getTime());
			} else {
				return new Date(java.sql.Timestamp.valueOf(s.concat(":0")).getTime());
			}
		}
	}

	public static String FormateNumber(String formate, String num) {
		DecimalFormat df = new DecimalFormat(formate);
		String myNumber = (num.trim().length() == 0)?"0":num.trim();
		return df.format(Double.parseDouble(myNumber));
	}

}
