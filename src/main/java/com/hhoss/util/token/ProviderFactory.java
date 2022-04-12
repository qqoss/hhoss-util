package com.hhoss.util.token;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hhoss.aspi.Factory;
import com.hhoss.conf.ResLocale;
import com.hhoss.lang.Classes;

public class ProviderFactory implements Factory<String,TokenProvider> {
  	private static Set<String> supports; 
 
	// more efficiency than reflect
	@Override public TokenProvider get(String name, Object... params){
		if(name==null||name.isBlank()) {
			throw new IllegalArgumentException("name should not be blank.");
		}else if(name.endsWith(".system")){
			return SystemProvider.instance;
		}else if(name.endsWith(".rooter")){
			return RooterProvider.instance;
		}
		if(params==null||params.length<1){
			throw new IllegalArgumentException("name,param object should not be empty.");
		}	
		if(name.endsWith(".locale")){
			return new ResLocale((Map<?,?>)params[0]);
		}else if(name.endsWith(".hasher")){
			return new HasherProvider((Map<?,?>)params[0]);
		}else if(name.endsWith(".mapper")){
			return new MapperProvider((Map<?,?>)params[0]);
		}else if(name.endsWith(".parent")){
			return new MapperProvider((Map<?,?>)params[0]);
		}else if(name.endsWith(".finale")){
			return new FinaleProvider((Map<?,?>)params[0],params.length>1?(String)params[1]:"");
		}		
		return null;
	};

	@Override public Set<String> supports() {
		if(supports==null){
			String[] namesArr = {"rooter","system","locale","hasher","mapper","parent","finale"};
			Set<String> names = new HashSet<>();
			for(String name:namesArr){
				names.add(TokenProvider.PREFIX+name);
			}
			supports=Collections.unmodifiableSet(names);
		}
		return supports;
	}
	
	@Override public String getName() {
		return Classes.referName();//"spi.coder.provider.factory";
	}
	
}
