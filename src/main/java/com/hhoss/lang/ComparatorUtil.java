package com.hhoss.lang;

import java.util.Comparator;

import org.apache.commons.beanutils.BeanUtils;

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

	public static String get(Object obj, String fieldName) {
		String objValue = "";
		try {
			objValue = BeanUtils.getProperty(obj, fieldName);
		} catch (Exception e) {
		}
		return (objValue == null) ? "" : objValue;
	}

	public ComparatorUtil(String orderName, String orderType) {
		this.orderName = orderName;
		this.orderType = orderType;
	}
}
