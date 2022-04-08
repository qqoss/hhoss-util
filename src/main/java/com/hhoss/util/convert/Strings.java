package com.hhoss.util.convert;

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
		return (nm==null||nm.trim().length()==0)?null:Long.decode(nm);
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
		return (nm==null||nm.trim().length()==0)?null:Integer.decode(nm);
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
	public static float toFloat(String nm) {
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
	public static double toDouble(String nm) {
		return (nm==null)?null:Double.parseDouble(nm);
	}


}
