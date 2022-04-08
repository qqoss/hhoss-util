package com.hhoss.auth;

import java.util.Map;

public class Auth4Text extends AuthProvider {
	public Auth4Text(String name) {
		super(name);
	}
	
	@Override
	public  boolean verify(Map map){
		String inputValue = (String)map.get(INPUT_KEY);
		if(inputValue==null||map.get("DATA")==null){
			return false;
		};
		return inputValue.equals(map.get("DATA"));
    }

}
