package com.hhoss.auth;

import java.io.Serializable;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

import com.hhoss.util.coder.Digester;

//@see java.security.AuthProvider;
public class AuthProvider extends Provider {
	private static final long serialVersionUID = 154795434548679874L;
	public static final String INPUT_KEY="inputValue";
	private static Map<Integer,AuthProvider> providers = getProviders();
	
	private static Map<Integer,AuthProvider> getProviders(){
		Map<Integer,AuthProvider> providers  = new HashMap<>();
		providers.put(101, new Auth4Text("plainText"));
		providers.put(103, new Auth4Ldap("LDAP"));
		providers.put(102, new AuthProvider("MD5"){
			public  boolean verify(Map map){
				String inputValue = (String)map.get(INPUT_KEY);
				if(inputValue==null||map.get("DATA")==null){
					return false;
				};
				String md5=Digester.md5Hex(inputValue);
				return md5.equalsIgnoreCase((String)map.get("DATA"));
		    }
		});
		
		return providers;
	}	
	
	
	public static AuthProvider getProvider(int k){
		return providers.get(k);
	}
	
	public AuthProvider(String name){
		super(name,1.0,"auth provider:"+name);
	}
	
    public boolean verify(Map<String,Serializable> map) {
    	return false;
    }


}
