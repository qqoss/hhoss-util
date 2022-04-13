package com.hhoss.util;

import java.util.Comparator;

import com.hhoss.lang.Beans;

/**
 * sort order handle
 * @author kejun
 *
 */
public class ComparatorUtil {
	private String orderName;
	private String orderType;

	public Comparator getComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				int result = get(o1, orderName).compareTo(get(o2, orderName));
				if (orderType != null && orderType.equals("desc")) {
					result = result * (-1);
				}
				return result;
			}
		};
	}

	private static String get(Object obj, String fieldName) {
		Object objValue = "";
		try {
			//objValue = BeanUtils.getProperty(obj, fieldName);
			objValue = Beans.getFieldValue(obj, fieldName);
		} catch (Exception e) {
		}
		return (objValue == null) ? "" : objValue.toString();
	}

	public ComparatorUtil(String orderName, String orderType) {
		this.orderName = orderName;
		this.orderType = orderType;
	}
}
