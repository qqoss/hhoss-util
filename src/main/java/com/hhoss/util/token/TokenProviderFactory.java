package com.hhoss.util.token;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hhoss.aspi.AbstractFactory;
import com.hhoss.conf.ResLocale;

public class TokenProviderFactory extends AbstractFactory<TokenProvider>  {
  	private static Set<String> supports; 
 
	// more efficiency than reflect
	@Override public TokenProvider get(String name, Object... params){
		if(name.endsWith(".system")){
			return SystemProvider.instance;
		}else if(name.endsWith(".rooter")){
			return RooterProvider.instance;
		}
		
		if(params==null||params.length<1){
			throw new IllegalArgumentException("param object should not be empty.");
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
		}else if(name.endsWith(".crypto")){
			return new CryptoProvider((String[])params);
			//return new CryptoProvider((String[])ConvertUtils.convert(params,String[].class));
		}		
		return null;
	};
	
	@Override public String getName() {
		return "spi.token.provider.factory";
	}

	@Override public Set<String> supports() {
		if(supports==null){
			String[] namesArr = {"rooter","system","locale","hasher","mapper","parent","finale","crypto"};
			Set<String> names = new HashSet<>();
			for(String name:namesArr){
				names.add(TokenProvider.PREFIX+name);
			}
			supports=Collections.unmodifiableSet(names);
		}
		return supports;
	}
	
}
