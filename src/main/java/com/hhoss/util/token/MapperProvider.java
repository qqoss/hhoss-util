package com.hhoss.util.token;

import java.util.Map;
import java.util.Properties;

import com.hhoss.conf.ResHolder;

/**
 * @author kejun
 * return the map value, if map value isn't String, return toString().
 *
 */
public class MapperProvider extends TokenProvider{
	private static final long serialVersionUID = 2450918523025264810L;
	private Map<?,?> map;
	public MapperProvider(Map<?,?> map) {
	    this.map = map;
	}
	
	@Override public String getName() {return PREFIX+"mapper";}
	@Override public String get(String name) {
	    if (name==null || map == null){
	    	return null;
	    }else if(map instanceof ResHolder){
	    	return ((ResHolder)map).getProperty(name,false);
	    }else if(map instanceof Properties){
	    	return ((Properties)map).getProperty(name);
	    }
	    Object obj =  map.get(name);
	    return obj==null?null:obj.toString();
	}
}

