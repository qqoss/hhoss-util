package com.hhoss.util.token;

import java.util.Map;
import java.util.Properties;

public class FinaleProvider extends TokenProvider {
	private static final long serialVersionUID = 2293609528800797902L;
	private Map<?, ?> map;
	private String def;
	public FinaleProvider(Map<?,?> map) {
		this.map = map;
	}
	public FinaleProvider(Map<?,?> map, String def ) {
		this.map = map;
		this.def = def;
	}

	@Override public String getName() {return PREFIX+"finale";}
	@Override public String get(String name) {
		if(map==null||name==null){
			return def;
		}
		Object v = null;
	    if(map instanceof Properties) {
			v = ((Properties) map).getProperty(name);
		} else {
			v = map.get(name);
		}
		return v==null?def:v.toString();
	}
}
