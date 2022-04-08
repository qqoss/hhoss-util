package com.hhoss.aspi;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFactory<F> implements Factory<String, F> {
	private static final Object NULL = null;

	public F get(String name) {
		return get(name, NULL);
	};

	@Override public String getName() {
		return "spi.abstract.factory";
	}

	@Override public Set<String> supports() {
		return new HashSet<String>();
	}

	@Override public boolean support(Class<?> klass) {
		return false;
	}

	@Override public F get(Class<F> klass, Object... params) {
		return null;
	}

	@Override public abstract F get(String name, Object... params);

}
