package com.hhoss.aspi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

@SuppressWarnings("rawtypes")
public class SPIManager  {
	private static Map<Factory,Collection> factories ;
	private static Map<String,Provider> providers ;
	
	/**
	 * @return map<Factory, supports> defined in meta-inf/services/
	 */
	public final static Map<Factory,Collection> getFactories(){
		if( factories==null ){
			Map<Factory,Collection> map = new HashMap<>();
			Iterator<Factory> itr = ServiceLoader.load(Factory.class).iterator();
			while(itr.hasNext()){
				Factory f = itr.next();
				map.put(f,f.supports());
			}
			factories = map;
		}
		return factories;
	}
	
	/**
	 * @return Map<name,Provider> defined in meta-inf/services/
	 */
	public final static Map<String,Provider> getProviders(){
		if( providers==null ){
			Map<String,Provider> map = new HashMap<String,Provider>();
			Iterator<Provider> itr = ServiceLoader.load(Provider.class).iterator();
			while(itr.hasNext()){
				Provider f = itr.next();
				if(f.getName()!=null) {map.put(f.getName(),f);}
			}
			providers = map;
		}
		return providers;
	}
	
}
