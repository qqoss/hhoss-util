package com.hhoss.util;

import java.io.Serializable;
import java.util.HashMap;

/**
 * ensure the resultset map with uppercase key 
 * @author kejun
 *
 * @param <V> the value
 */
public class UpperKeyMap<V extends Serializable> extends HashMap<String,V> {
 	private static final long serialVersionUID = 5239177553497211426L;

 	@Override
 	public V put(String key, V value) {
	  return super.put(key.toUpperCase(), value);
   }
}
