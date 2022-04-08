package com.hhoss.boot;

import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import com.hhoss.jour.Logger;
import com.hhoss.util.Iterators;

public class AppTest {
	private static final Logger logger = Logger.get();

	public static void test(){
		Properties props = App.subProperties("res.dev.test1","h2net");
		
		logger.info("{}={}","${dbms}.driver",props.getProperty("${dbms}.driver"));
		for(Object s: Iterators.of(props.keys())){
			logger.info("{}={}",s,props.getProperty((String)s));
		}
	}
	
	public static void main(String[] args) throws Exception {
		logger.info(App.getLocalMachineName());
		logger.info(InetAddress.getLocalHost().getHostName());
		logger.info(InetAddress.getLocalHost().getHostAddress());
		logger.info(InetAddress.getLocalHost().getCanonicalHostName());
		test();

		int a = 1<<30;
		int b= 1<<29;
		int c =a+b;
		int d= c*2;
		int e = 1<<31;
		int f = d&e;
		
		logger.info("min integer={};",Integer.MIN_VALUE);
		logger.info("max integer={};",Integer.MAX_VALUE);
		logger.info("a={};b={};c={};d={};e={};f={};",a,b,c,d,e,f);
		logger.info("2*(1+e)=2:{}",2*(1+e));
	}

}
