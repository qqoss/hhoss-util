package com.hhoss.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.hhoss.jour.Logger;
import com.hhoss.util.convert.Strings;


/**
 * for holder named Object.
 * @author Kejun
 *
 */
public class HMap<T extends Serializable> extends LinkedHashMap<String,T> implements Serializable {
	private static final long serialVersionUID = 5619316962367898253L;
	private static final Logger logger = Logger.get();

	public HMap(){ };
	public HMap(Map<String, T> m){super(m);};
	
	public HMap(String n1, T v1){
		let(n1, v1);
	}
	public HMap(String n1, T v1, String n2, T v2){
		let(n1, v1, n2, v2);
	}
	public HMap(String n1, T v1, String n2, T v2, String n3, T v3){
		let(n1, v1, n2, v2, n3, v3);
	}
	public HMap(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4){
		let(n1, v1, n2, v2, n3, v3, n4, v4);
	}
	public HMap(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5){
		let(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5);
	}
	public HMap(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5, String n6, T v6){
		let(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6);
	}
	public HMap(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5, String n6, T v6, String n7, T v7){
		let(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6, n7, v7);
	}
	public HMap(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5, String n6, T v6, String n7, T v7, String n8, T v8){
		let(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6, n7, v7, n8, v8);
	}
	public HMap(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5, String n6, T v6, String n7, T v7, String n8, T v8, String n9, T v9){
		let(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6, n7, v7, n8, v8, n9, v9);
	}
	
	public HMap<T> let(String n1, T v1){
		put(n1,v1);	return this;
	}
	public HMap<T> let(String n1, T v1, String n2, T v2){
		put(n1,v1);	put(n2,v2);	return this;
	}
	public HMap<T> let(String n1, T v1, String n2, T v2, String n3, T v3){
		put(n1,v1);	put(n2,v2);	put(n3,v3);	return this;
	}
	public HMap<T> let(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4){
		return let(n1,v1,n2,v2).let(n3,v3,n4,v4);	
	}
	public HMap<T> let(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5){
		return let(n1,v1,n2,v2,n3,v3).let(n4,v4,n5,v5);
	}
	public HMap<T> let(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5, String n6, T v6){
		return let(n1,v1,n2,v2,n3,v3).let(n4,v4,n5,v5,n6,v6);
	}
	public HMap<T> let(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5, String n6, T v6, String n7, T v7){
		return let(n1,v1,n2,v2,n3,v3).let(n4,v4,n5,v5).let(n6,v6,n7,v7);
	}
	public HMap<T> let(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5, String n6, T v6, String n7, T v7, String n8, T v8){
		return let(n1,v1,n2,v2,n3,v3).let(n4,v4,n5,v5,n6,v6).let(n7,v7,n8,v8);
	}
	public HMap<T> let(String n1, T v1, String n2, T v2, String n3, T v3, String n4, T v4, String n5, T v5, String n6, T v6, String n7, T v7, String n8, T v8, String n9, T v9){
		return let(n1,v1,n2,v2,n3,v3).let(n4,v4,n5,v5,n6,v6).let(n7,v7,n8,v8,n9,v9);
	}	
	
	/**
	 * merge map.entries, will overwrite the entry if exists
	 * @param map
	 * @return 
	 */
	public HMap<T> put(Map<String,T> map){
		putAll(map);return this;
	}
	
	/**
	 * merge map.entries, will ignore the entry if exists
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HMap<T> add(Map<String,?> map){
		for(Map.Entry<String,?> ent : map.entrySet()){
			if(!containsKey(ent.getKey()))try{
				put(ent.getKey(), (T)ent.getValue());
			}catch(ClassCastException e){
				logger.info("[{}={}] can't cast to target.",ent.getKey(),ent.getValue());
			}
		}
		return this;
	}
	
	public <V> Map<String,V> toMap(){
		return Wrapper.wrap(this);
		//return new HashMap<String,Object>(this);
	}
	
	
	public String getString(String key) {
		T val = get(key);	
		return val==null?null:val.toString();
	}
	
	public Long getLong(String key) {
		T val = get(key);
		if(val==null) {
			return null;
		}else if (val instanceof Number) {
			return ((Number)val).longValue();
		}else {
			return Strings.toLong(val.toString());
		}
	}
	
	public Integer getInteger(String key) {
		T val = get(key);
		if(val==null) {
			return null;
		}else if (val instanceof Number) {
			return ((Number)val).intValue();
		}else {
			return Strings.toInteger(val.toString());
		}
	}

	public Double getDouble(String key) {
		T val = get(key);
		if(val==null) {
			return null;
		}else if (val instanceof Number) {
			return ((Number)val).doubleValue();
		}else {
			return Strings.toDouble(val.toString());
		}
	}

	public Float getFloat(String key) {
		T val = get(key);
		if(val==null) {
			return null;
		}else if (val instanceof Number) {
			return ((Number)val).floatValue();
		}else {
			return Strings.toFloat(val.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String,Serializable> getMap(String key) {
		T val = get(key);
		if (val instanceof Map) {
			return (Map<String,Serializable>)val;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public HMap<Serializable> getHMap(String key) {
		T val = get(key);
		if (val instanceof HMap) {
			return (HMap<Serializable>)val;
		}
		return null;
	}
	
	/**
	 * @param src
	 * @return HMap<S>, new HMap if can't cast or null. 
	 */
	public static <S extends Serializable> HMap<S> from(Map<String,S> src) {
		if(src==null) {return new HMap<S>();}
		if(HMap.class.isInstance(src)) {// src!=null && src instanceof HMap
			return (HMap<S>)src;
		}else {
			return new HMap<S>(src);
		}
	}

}
