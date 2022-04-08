package com.hhoss.util.token;

import java.util.Map;
/**
 * @author kejun
 * return the map value, if map value isn't String, return null.
 *
 */

public class HasherProvider extends TokenProvider{
	private static final long serialVersionUID = 4046355171903493723L;
	private Map<?,?> map ;
	public HasherProvider(Map<?,?> map){
		this.map = map;
	}
	@Override public String getName() {return PREFIX+"hasher";}
	@Override public String get(String key) {
		Object oval = map.get(key);
		String sval = (oval instanceof String) ? (String) oval : null;
		return sval;
	}
}