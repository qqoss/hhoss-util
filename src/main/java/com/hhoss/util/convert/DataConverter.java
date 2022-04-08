package com.hhoss.util.convert;

import java.io.Serializable;
import java.sql.Types;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

public class DataConverter implements Converter{
	private static ConvertUtilsBean converter = BeanUtilsBean.getInstance().getConvertUtils();
	
	public static Serializable get( Object value, int dataType){
		/**
		 * java.sql.Types: BIT=-7 TINYINT=-6(8b) BIGINT=-5(64b) NULL=0 CHAR=1 NUMERIC=2 DECIMAL=3 INTEGER=4(32b) SMALLINT=5(16b) FLOAT=6(32b) REAL=7 DOUBLE=8(64b) VARCHAR=12 BOOLEAN=16
		 * DATE =91 TIME=92 TIMESTAMP=93 OTHER=1111 JAVA_OBJECT=2000 ARRAY=2003 BLOB=2004 CLOB=2005 SQLXML=2009
		 */

		switch(dataType){
			case Types.NULL://0 default as long;
			case Types.BIGINT:
				value = get(value,Long.TYPE);
			break;
			case Types.BIT:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
				value = get(value,Integer.TYPE); 
				break;
			case Types.NUMERIC:
			case Types.DECIMAL:
				Double d = get(value,Double.TYPE);
				value = (String.valueOf(value).indexOf('.')>-1)?d:d==null?null:d.longValue();
				break;
			case Types.FLOAT:
				value = get(value,Float.TYPE);
				break;
			case Types.REAL:
			case Types.DOUBLE:
				value = get(value,Double.TYPE);
				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				value = get(value,Date.class);
				break;
			default: 
		}
		
		return (Serializable)value;
	}
	
	@SuppressWarnings("unchecked")
    public static <T> T get(Object value, Class<T> type){
	   return (T)converter.convert(value,type);
    }
	
	@Override
    public <T> T convert(Class<T> type, Object value){
	   return get(value,type);
    }


}
