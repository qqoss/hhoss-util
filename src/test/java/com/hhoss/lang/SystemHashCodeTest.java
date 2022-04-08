package com.hhoss.lang;

import java.util.Arrays;
import java.util.Map;

import com.hhoss.jour.Logger;

public class SystemHashCodeTest {
	private static final Logger logger = Logger.get();
	private static void test1(){
		String string1 = "wxg";
		String string2 = "wxg";

		logger.info("identityHashCode1:{}",System.identityHashCode(string1));
		logger.info("identityHashCode2:{}",System.identityHashCode(string2));
		logger.info("HashCode1:{}",string1.hashCode());
		logger.info("HashCode2:{}",string2.hashCode());
		
	}
	private static void test2(){
		String string1 = new String("wxg");
		String string2 = new String("wxg");

		logger.info("identityHashCode1:{}",System.identityHashCode(string1));
		logger.info("identityHashCode2:{}",System.identityHashCode(string2));
		logger.info("HashCode1:{}",string1.hashCode());
		logger.info("HashCode2:{}",string2.hashCode());
	}
	private static void test3(){
		String string1 = new String("wxg");
		String string2 = new String("wxg");

		logger.info("identityHashCode1:{}",System.identityHashCode(string1.toString()));
		logger.info("identityHashCode2:{}",System.identityHashCode(string2.toString()));
		logger.info("identityHashCode1:{}",System.identityHashCode(String.valueOf(string1)));
		logger.info("identityHashCode2:{}",System.identityHashCode(String.valueOf(string2)));
	}

	public static void main(String[] args) {
		test1();
		test2();
		test3();
	}
}
