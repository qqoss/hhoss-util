package com.hhoss.aspi;

import java.util.Collections;
import java.util.Set;


public abstract class AbstractProvider<K, V> implements Provider<K,V> {
	private static final long serialVersionUID = 2452019420006744125L;
	@Override public abstract V get(K key);
	@Override public void set(K key, V value) {}
	@Override public String getName(){return null;}	
	@Override public boolean isEmpty(){return true;}	
	@Override public Set<V> keySet(){return Collections.emptySet();}
}
