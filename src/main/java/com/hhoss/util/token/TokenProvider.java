package com.hhoss.util.token;

import java.util.Collection;
import java.util.Map.Entry;

import com.hhoss.aspi.AbstractProvider;
import com.hhoss.aspi.Factory;
import com.hhoss.aspi.Provider;
import com.hhoss.aspi.SPIManager;
import com.hhoss.jour.Logger;

public abstract class TokenProvider extends AbstractProvider<String, String> {
	private static Logger logger;
	private static final long serialVersionUID = 1883565609968095675L;
	public static final String PREFIX = "spi.token.provider.";
	
	@SuppressWarnings("rawtypes")
	public static final TokenProvider from(String simpleName, Object...  params){
		String spiName = PREFIX+simpleName;
		try{
			Provider spi = SPIManager.getProviders().get(spiName);
			if( spi!=null ){
				return (TokenProvider)spi;
			}
			for(Entry<Factory,Collection> ent :SPIManager.getFactories().entrySet() ){
				if(ent.getValue().contains(spiName)){
					@SuppressWarnings("unchecked")
					Object obj = ent.getKey().get(spiName,params);
					if(obj instanceof TokenProvider){
						return (TokenProvider)obj;
					}
				}
			}
			getLog().warn("can't get Provider[{}].",simpleName);
		}catch(Exception e){
			getLog().warn("Fail when get Provider[{}],exception:{}",spiName,e.getMessage());
		}
		return null;		
	}
	
	public String get(String key, String def){
		String val = get(key);
		return val==null?def:val;
	}
	
	private static Logger getLog(){
		return logger==null?logger=Logger.get():logger;
	}

}
