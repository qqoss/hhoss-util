package com.hhoss.util.token;

import com.hhoss.jour.Logger;

public class TestProvider extends TokenProvider{
	private static final long serialVersionUID = 245091875474810L;
	@Override public String get(String name) {
	    return name;
	}
	@Override public String getName(){return TokenProvider.PREFIX+"Test";}	
	public static void main(String[] args) {
		new TokenResolver(TokenProvider.from("Test","")).get("anystr");
		String aa = new ProviderFactory().getName();
		String bb = new TokenResolver(null).getName();
		Logger.get().info("{},{}",aa,bb);
	}
}
