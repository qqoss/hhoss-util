package com.hhoss.jour;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import com.hhoss.lang.Classes;
import com.hhoss.lang.Judge;

/**
 * logger handle util
 * @author kejun
 *
 */
public final class LoggerFactory {	
	/**
	 * @return Logger with caller's className as loggerName name
	 * should invoke method in endpoint class
	 */
	public static LocationAwareLogger getLogger(){
		return getLogger(Classes.callerName());
	}
	
	/**
	 * @return Logger with caller's className as loggerName name
	 */
	public static LocationAwareLogger refLogger(){
		return getLogger(Classes.invokerName());
	}
	
	/**
	 * @return Logger with given name
	 */
	public static LocationAwareLogger getLogger(final Class<?> cls){
		return getLogger(cls.getName());
	}
	
	/**
	 * @return Logger with given name
	 */
	public static LocationAwareLogger getLogger(final String name){
		org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(name);
		if(log instanceof ch.qos.logback.classic.Logger)try{
			ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)log;
			String level = logger.getLoggerContext().getProperty("spi.logger.level|"+name);
			if( level!=null && level.trim().length()>2 ){
				logger.setLevel(ch.qos.logback.classic.Level.toLevel(level.trim()));
			}
		}catch(Exception e){}		
		return (LocationAwareLogger)log;
	}
	
	public static LocationAwareLogger getParent(LocationAwareLogger logger){
		String name = logger.getName();
		int idx = name.lastIndexOf('.');
		return idx>0?getLogger(name.substring(0, idx)):null;		
	}

	
	public static ILoggerFactory getILoggerFactory() {
		return org.slf4j.LoggerFactory.getILoggerFactory();
	}	
	
	public static void setRootLevel(String level) {
		setLevel(Logger.ROOT_LOGGER_NAME,level);
	}	
		
	/**
	 * @param name
	 * @param level
	 * @return 
	 */
	public static void setLevel(String name, String level) {
		LocationAwareLogger log = getLogger(name);
		if(log instanceof ch.qos.logback.classic.Logger){
			ch.qos.logback.classic.Logger log2=(ch.qos.logback.classic.Logger)log;			
			String oldLevel = log2.getLevel().toString();			
		    log2.setLevel(ch.qos.logback.classic.Level.toLevel(level));
		    log.info("reset logger[{}] level from {} to {}",name, oldLevel,log2.getLevel());
		}else{
			log.warn("wrong when set logger[{}] level[{}].",name, level);
		}
	}	

    /**
     * @param str 
     * @return int from org.slf4j.spi.LocationAwareLogger._INT or org.slf4j.event.Level.toInt();
     */
    public static int level(String str) {
    	int level = 0;
        if( str == null || str.equalsIgnoreCase("ALL") ) {
        	level = LocationAwareLogger.TRACE_INT;//=org.slf4j.event.Level.TRACE.toInt();//0
        }else if( str.equalsIgnoreCase("TRACE") ){
        	level = LocationAwareLogger.TRACE_INT;//=org.slf4j.event.Level.TRACE.toInt();//0
        }else if( str.equalsIgnoreCase("DEBUG") ){
        	level = LocationAwareLogger.DEBUG_INT;//=org.slf4j.event.Level.DEBUG.toInt();//10
        }else if( str.equalsIgnoreCase("INFO") ) {
        	level = LocationAwareLogger.INFO_INT;//=org.slf4j.event.Level.INFO.toInt();//20
        }else if( str.equalsIgnoreCase("WARN") ) {
        	level = LocationAwareLogger.WARN_INT;//=org.slf4j.event.Level.WARN.toInt();//30
        }else if( str.equalsIgnoreCase("ERROR") ){
        	level = LocationAwareLogger.ERROR_INT;//=org.slf4j.event.Level.ERROR.toInt();//40
        }else if( str.equalsIgnoreCase("FATAL") ){
        	level = LocationAwareLogger.ERROR_INT;//=org.slf4j.event.Level.ERROR.toInt();//40
        }else if( Judge.isFail(str) ){
        	level=-1;
        }else{
        	level=0;
        }
        return level;
    }


}
