package com.hhoss.util.token;

import com.hhoss.boot.App;

public class RooterProvider extends TokenProvider {
	private static final long serialVersionUID = 6739494777031858198L;
	protected static final RooterProvider instance = new RooterProvider();

	@Override public String getName() {return PREFIX+"rooter";}
	@Override public String get(String key) {
		return App.getRootEnv(key);
	}

}
