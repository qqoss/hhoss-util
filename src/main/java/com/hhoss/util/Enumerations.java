package com.hhoss.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class Enumerations<E> implements Enumeration<E> {
	private final Iterator<E> iterator;

	public Enumerations(Iterator<E> iterator) {
		this.iterator = iterator;
	}

	public Enumerations(Collection<E> col) {
		this.iterator = col.iterator();
	}

	public E nextElement() {
		return iterator.next();
	}

	public boolean hasMoreElements() {
		return iterator.hasNext();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Enumeration<T> of(Collection<T> col){
		return new Enumerations(col);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Enumeration<T> of(Iterator<T> itr){
		return new Enumerations(itr);
	}

}
