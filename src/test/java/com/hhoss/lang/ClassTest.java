package com.hhoss.lang;

import com.hhoss.jour.Logger;
//can't named a ClassesTest ??
public class ClassTest {
	private static final Logger LOG = Logger.get();

	public static void main(String[] args) {
		test();
	}
	
	private static void test(){
		LOG.info("caller :{}",ClassMeta.caller());
		LOG.info("caller :{}",Classes.caller());
	}

}
