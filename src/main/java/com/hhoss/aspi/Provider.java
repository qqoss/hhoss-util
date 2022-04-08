package com.hhoss.aspi;

import java.io.Serializable;
import java.util.Set;

public interface Provider<K, V> extends Serializable {
	String getName();
	void set(final K key, final V value);
	V get(K key);
	Set<V> keySet();
	boolean isEmpty();
}
