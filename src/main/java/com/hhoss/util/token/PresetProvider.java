package com.hhoss.util.token;


/**
 * @author kejun
 * return alway return the preset val, even the name is null
 *
 */
public class PresetProvider extends TokenProvider{
	private static final long serialVersionUID = 2450918523025264810L;
	private String val;
	public PresetProvider(){}
	public PresetProvider(String val) {
	    this.val = val;
	}
	
	@Override public String getName() {return PREFIX+"preset";}
	@Override public String get(String name) {
	    return val;
	}
}

