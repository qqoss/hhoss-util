package com.hhoss.auth;

import java.util.Map;

import com.hhoss.util.coder.Digester;

public class Auth4Hash extends AuthProvider {
	public Auth4Hash(String name) {
		super(name);
	}

	public  boolean verify(Map map){
		String inputValue = (String)map.get(INPUT_KEY);
		if(inputValue==null||map.get("DATA")==null){
			return false;
		};
		String md5=Digester.md5Hex(inputValue);
		return md5.equalsIgnoreCase((String)map.get("DATA"));
    }

}
