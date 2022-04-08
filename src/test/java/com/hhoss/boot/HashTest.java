package com.hhoss.boot;

import java.net.InetAddress;
import java.util.Properties;

import com.hhoss.jour.Logger;
import com.hhoss.ksid.SeqNum;
import com.hhoss.util.Iterators;

public class HashTest {
	private static final Logger logger = Logger.get();

	public static void test(){
		Properties props = App.subProperties("res.dev.test1","h2net");
		
		logger.info("{}={}","${dbms}.driver",props.getProperty("${dbms}.driver"));
		for(Object s: Iterators.of(props.keys())){
			logger.info("{}={}",s,props.getProperty((String)s));
		}
		logger.info("hashCode:{}",props.hashCode());
		logger.info("hashCode1:{}",new Object().hashCode());
		logger.info("hashCode1:{}",new Object().hashCode());
		logger.info("hashCode2:{}",new SeqNum(5).hashCode());
		logger.info("hashCode2:{}",new SeqNum(5).hashCode());


	}

	public static void main(String[] args) throws Exception {
		logger.info(App.getLocalMachineName());
		logger.info(InetAddress.getLocalHost().getHostName());
		logger.info(InetAddress.getLocalHost().getHostAddress());
		logger.info(InetAddress.getLocalHost().getCanonicalHostName());
		test();
	}

}
