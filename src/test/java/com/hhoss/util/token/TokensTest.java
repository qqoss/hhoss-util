package com.hhoss.util.token;

import java.util.Properties;

import com.hhoss.conf.ResHolder;

/**
* @author kejun
* 
*         allow recurse and replace with substitution
* 
*/
public class TokensTest {

	
	private static void test_val() {
		Properties p = new ResHolder("test");
		p.put("level1", "0");
		p.put("level2", "${level1}/l2");
		p.put("level3", "${level2}/l3");
		p.put("level4", "abc/${level3}");
		p.put("a.b.a1", "b123");
		p.put("a.b.a2", "b231");
		p.put("a.b.a3", "b334");
		p.put("a.c.a1", "c123");
		p.put("a.c.a2", "c231");
		p.put("a.c.a3", "c334");
		p.put("a.v", "a.b");
		
		TokenProvider t = Tokens.from(p);
		System.out.println(t.get("${level4}"));
		System.out.println(p.getProperty("${a.v}.a1"));
	}

	public static void main(String[] args) {
		test_val();
	}

}
