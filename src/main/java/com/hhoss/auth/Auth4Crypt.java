package com.hhoss.auth;

import java.security.MessageDigest;
import java.util.Map;

import com.hhoss.jour.Logger;
import com.hhoss.util.coder.HexCoder;

public class Auth4Crypt extends AuthProvider {
	private static final Logger logger = Logger.get();
	public Auth4Crypt(String name) {
		super(name);
	}

	public  boolean verify(Map map){
		String data = (String)map.get(INPUT_KEY);
		if(data==null||map.get("DATA")==null){
			return false;
		};
		
		try {
			byte[] b = MessageDigest.getInstance((String)map.get("HASH")).digest(data.getBytes());
			return HexCoder.toHex(b).equalsIgnoreCase((String)map.get("DATA"));
		} catch (Exception e) {
			logger.warn("auth has exception.", e);
		}
		return false;
    }

}
