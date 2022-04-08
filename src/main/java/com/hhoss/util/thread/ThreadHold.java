package com.hhoss.util.thread;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;

import com.hhoss.jour.Logger;

/**
 * @author kejun
 *
 */
public class ThreadHold { 
	private static final Logger logger = Logger.get();
	private static final Map<String,ThreadLocal<Object>> variables = new HashMap<>();	  
   
    /**
     * hold  entry in threadLocal
     * @param key
     * @param val
     */
    public static void set(String key, final Object val) {
	    ThreadLocal<Object> tl=variables.get(key);
  		if( val==null ){
			if( tl!=null ){
				tl.remove();
				//variables.remove(tl); DON'T, it will affect other threads;
			}
			return;
		}  		
		if(tl==null){tl=new ThreadLocal<Object>();}
		tl.set(val);
		variables.put(key,tl);
		if(variables.size()>1<<6){
			logger.debug("gloabal threadLocal variables size:{}",variables.size());
		}
    } 
     
    @SuppressWarnings("unchecked")
 	public static <T> T get(String key) {  
     	ThreadLocal<?> tl=variables.get(key);
     	return (tl==null)?null:(T)tl.get();
     }  
     
 	public static <T> T get(String key, T add) {  
 		T v = get(key);
     	if(v==null ){set(key,v=add);}
     	return v;
     }  

	/**
	 * store the value for MDC in current thread, can be inherited for thread
	 * if val==null, will remove the entry
	 * @param key key to put MDC
	 * @param val value to put MDC
	 */
	public static void push(String key,String val){
		try{ if(val==null){MDC.remove(key);}else{MDC.put(key, val);}	
		}catch(Exception e){logger.debug(e.getMessage());}
	}
 	
	/**
	 * get the value from MDC,support inherit for thread
	 * @param key key in MDC
	 * @param def default value if null in MDC
	 * @return string
	 */
	public static String peek(String key,String def){
		String val= peek(key);
		return val==null?def:val;
	}
	/**
	 * get the value from MDC, support inherit for thread
	 * @param key key in MDC
	 * @return string
	 */	
	public static String peek(String key){
		return MDC.get(key);
	}
	
	/**
	 * clear entries of MDC and thread variables in current thread
	 */	
	public static void clear(){
		try{MDC.clear();}catch(Exception e){logger.debug(e.getMessage());}
		for(ThreadLocal<Object> tl:variables.values()){tl.remove();}
	}
    
}