package com.hhoss.util;

import java.util.Enumeration;
import java.util.Iterator;

public class Iterators<E> implements Iterable<E>, Iterator<E> {
	private final Enumeration<E> enums;

	public Iterators(Enumeration<E> enums) {
		this.enums = enums;
	}

	@Override
	public Iterator<E> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return enums.hasMoreElements();
	}

	@Override
	public E next() {
		return enums.nextElement();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Wrapped is Enumeration.");
	}

	public static <T> Iterators<T> of(Enumeration<T> enums) {
		return new Iterators<T>(enums);
	}

}
