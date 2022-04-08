package com.hhoss.util.convert;

import java.text.SimpleDateFormat;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * date converter for struts2
 * @author kejun
 *
 */
public class DateConverter implements Converter {
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss

	public DateConverter() {
	}

	public Object convert(Class type, Object value) {
		if (value == null)
			return null;
		if (String.class.isAssignableFrom(value.getClass()) && ((String) value).trim().length() > 0) {
			try {
				return df.parse((String) value);
			} catch (Exception ex) {
				throw new ConversionException("yyyy-MM-dd" + value.getClass());
			}
		} else {
			return value;
		}
	}
}
