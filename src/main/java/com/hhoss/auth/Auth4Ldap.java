package com.hhoss.auth;

import java.util.Map;

import javax.naming.Context;

import com.hhoss.boot.App;
import com.hhoss.ldap.LDAPInfo;

public class Auth4Ldap extends AuthProvider {
	private static final long serialVersionUID = 7370416932344328533L;
	private static String ldapUrl = getLdapUrl();
	private static String userDN = getUserBase();
	
	public Auth4Ldap(String name) {
		super(name);
	}
	
	public  boolean verify(Map map){
		String udn="uid="+map.get("CODE");
		if(userDN!=null){
			udn=udn+","+userDN;
		}
		return LDAPInfo.auth(ldapUrl,udn, (String)map.get(INPUT_KEY));
    }
	
	private static String getLdapUrl(){
		return App.getProperty("res.app.module.ldap",Context.PROVIDER_URL);
	}

	private static String getUserBase(){
		String base =  App.getProperty("res.app.module.ldap","config.auth.ldap.base");
		if(base==null){
			int idx = ldapUrl.indexOf("/",ldapUrl.indexOf("//")+2);
			base = idx>0?ldapUrl.substring(idx+1):null;			
		}
		return base;
	}

}
