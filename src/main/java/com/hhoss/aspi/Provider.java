package com.hhoss.aspi;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public interface Provider<K, V> extends Serializable {
	default Set<V> keySet(){return Collections.emptySet();}
	default void set(final K key, final V value) {}
	default boolean isEmpty(){return true;}
	String getName();
	V get(K key);
}
