package com.hhoss.boot;

import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.hhoss.conf.EnvHolder;
import com.hhoss.util.thread.ThreadPool;



/**
 * Sys for static info and sys init info
 * @author Kejun
 * @deprecated merged to App
 */
abstract class Sys extends EnvHolder {
	
	public static void printEnv2(){
		ThreadPool.run(new Runnable(){
			@Override
			public void run() {
				while(true){
					try{
						System.out.println("===============begin to print env.rootPath,getprop,sysGetprop==========================");
						System.out.println(getRootEnv(App.APP_CONTEXT_ROOT));
						System.out.println(guessProperty(App.APP_CONTEXT_ROOT));
						System.out.println(System.getProperty(App.APP_CONTEXT_ROOT));
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}				
			}
			
		});
	}
	
	public static void printEnv1(){
		TreeMap<String,String> props = new TreeMap<> ();

		System.out.println("\r\n============= best separator line ============" );
		System.out.println("-- Process Environment --" );
		props = new TreeMap<String,String>(System.getenv());

		for(Entry<String,String> ent:props.entrySet() ){
			System.out.println(ent.getKey()+" = "+ent.getValue());
		}
		System.out.println("\r\n============= best separator line ============" );
		System.out.println("-- System  Properties --" );
		
		Properties sysProps = System.getProperties();
		//props.list(System.out);
		//Set<String> set = sysProps.stringPropertyNames();
		Enumeration<?> names = sysProps.propertyNames();
		props.clear();
		while (names.hasMoreElements()) {
			String key = (String) names.nextElement();
			props.put(key, sysProps.getProperty(key));
		}
		for(Entry<String,String> ent:props.entrySet() ){
			System.out.println(ent.getKey()+" = "+ent.getValue());
		}
	}

	public static void main(String[] args) {
		printEnv1();
		printEnv2();
	}

}
