package com.hhoss.util;

import java.util.List;

public class Strings {
	
	/**
     * Accepts decimal, hexadecimal, and octal numbers given by the following grammar:
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i>
     * <p>
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
	 * @param key of the prop
	 * @return Long
	 */
	public static Long toLong(String nm) {
		return (nm==null||nm.isBlank())?null:Long.decode(nm);
	}
	/**
     * Accepts decimal, hexadecimal, and octal numbers given by the following grammar:
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i>
     * <p>
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
	 * @param key of the prop
	 * @return Integer
	 */
	public static Integer toInteger(String nm) {
		return (nm==null||nm.isBlank())?null:Integer.decode(nm);
	}

	public static Short toShort(String nm) {
		return (nm==null||nm.isBlank())?null:Short.decode(nm);
	}

	public static Byte toByte(String nm) {
		return (nm==null||nm.isBlank())?null:Byte.decode(nm);
	}
	/**
	 * support radix, 0x...
     * <dl>
     * <dt><i>HexSignificand:</i>
     * <dd>{@code 0x} <i>HexDigits<sub>opt</sub>
     *     </i>{@code .}<i> HexDigits</i>
     * <dd>{@code 0X}<i> HexDigits<sub>opt</sub>
     *     </i>{@code .} <i>HexDigits</i>
     * </dl>
	 * @param key of the prop
	 * @return float
	 * @see Double#valueOf(String s)
	 */
	public static Float toFloat(String nm) {
		return (nm==null)?null:Float.parseFloat(nm);
	}
	/**
	 * support radix, 0x...
     * <dl>
     * <dt><i>HexSignificand:</i>
     * <dd>{@code 0x} <i>HexDigits<sub>opt</sub>
     *     </i>{@code .}<i> HexDigits</i>
     * <dd>{@code 0X}<i> HexDigits<sub>opt</sub>
     *     </i>{@code .} <i>HexDigits</i>
     * </dl>
	 * @param key of the prop
	 * @return double
	 * @see Double#valueOf(String s)
	 */
	public static Double toDouble(String nm) {
		return (nm==null||nm.isBlank())?null:Double.parseDouble(nm);
	}

	public static Boolean toBoolean(String str) {
		return (str==null||str.isBlank())?null:Boolean.valueOf(str);
	}

	public static Character toChar(String str) {
		return (str==null||str.isEmpty())?null:str.charAt(0);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T convert(String str, Class<T> cls) {
		Object obj=null;
		if(cls.isAssignableFrom(Integer.class)) {
			obj = toInteger(str);
		}else if(cls.isAssignableFrom(Long.class)) {
			obj = toLong(str);
		}else if(cls.isAssignableFrom(Byte.class)) {
			obj = toByte(str);
		}else if(cls.isAssignableFrom(Short.class)) {
			obj = toShort(str);
		}else if(cls.isAssignableFrom(Double.class)) {
			obj = toDouble(str);
		}else if(cls.isAssignableFrom(Boolean.class)) {
			obj = toBoolean(str);
		}else if(cls.isAssignableFrom(Float.class)) {
			obj = toFloat(str);
		}else if(cls.isAssignableFrom(Character.class)) {
			obj = toChar(str);
		//}else if(cls.isAssignableFrom(Date.class)) {
		}		
		return (T)obj;
		
	}

	public static String zeros(int n) {
		return repeat('0', n);
	}

	public static String repeat(char value, int n) {
		return new String(new char[n]).replace("\0", String.valueOf(value));
	}
	public static String toCsv(List<String> src) {
		// return src == null ? null : String.join(", ", src.toArray(new String[0]));
		return join(src, ", ");
	}

	public static String join(List<String> src, String delimiter) {
		return src == null ? null : String.join(delimiter, src.toArray(new String[0]));
	}

	public static String upperFirst(String string) {
		if (string == null || string.length() == 0) {
			return string;
		} else {
			return string.substring(0, 1).toUpperCase() + string.substring(1);
		}
	}

	public static String lowerFirst(String string) {
		if (string == null || string.length() == 0) {
			return string;
		} else {
			return string.substring(0, 1).toLowerCase() + string.substring(1);
		}
	}

}
