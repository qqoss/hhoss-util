package com.hhoss.util.token;

import java.util.HashMap;
import java.util.Map;

public class Tokens {
	
	public static TokenProvider from(Map<?,?> vars){
		//return get
		return new TokenResolver(TokenProvider.from("finale",vars));
	}
	/**
	 * @param map
	 * @param recurse, true to recurse get the value
	 * @return val of the subst token
	 */
	public static TokenProvider from(Map<?,?> map, boolean recurse) {		
		return new TokenResolver(TokenProvider.from(recurse?"finale":"mapper",map));
	}
	
	public static String from(String expr, Map<?,?> vars) {
		 return from(vars).get(expr);
	}

	public static void main(String[] args){
		Map<String,String> map = new HashMap<>();
		map.put("test.final.String", "12345");
		map.put("test.key1", "final.String");
		map.put("test.key2", "${test.key1}");
		map.put("test.key3", "${test.key2}");
		map.put("test.key4", "${test.${test.key3}}");
		System.out.print(from(map.get("test.key4"), map));
	}

}
