package com.hhoss.jour;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.joran.action.ActionConst;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.util.StatusPrinter;

import com.hhoss.boot.App;


/**
 * 应用的日志初始化以及日志相关操作
 * logger init for slf4j
 * @author kejun
 *
 */
public abstract class LoggerConfig {
	private static final Logger logger = LoggerFactory.getLogger();
    private static boolean initialized = false;
    private static Configurator configurator;

	public static void initial() {
		if(initialized||App.getKeeper()==null){
			logger.info("Logger has initialized or waiting Env initial!");
			return;
		}
		reset();
		initialized=true;
	}    

	 /**
	 * @param reset and Config logger
	 */
    protected static void reset(){
 		LoggerContext context = getLoggerContext();
		if(context==null) {
			logger.warn("LoggerContext is null!");
		}else try {
			//Map<String, String> old = context.getCopyOfPropertyMap();
			/*context.reset();//configurator.getContext();
			(configurator = new Configurator(context,props)).doConfigure();
			 */
			configurator = new Configurator(context).doConfigure();
			StatusPrinter.printInCaseOfErrorsOrWarnings(context);
			logger.info("logback reset by config {}",getConfigURL());
		}catch (Exception je) { 
			logger.error("logback config error, ignored reset!", je);
			System.out.println("^^^^^^ LoggerConfig: initial logback error. ^^^^^^");
			StatusPrinter.printInCaseOfErrorsOrWarnings(context);
		}
	}
	
	public static URL getConfigURL(){
		LoggerContext context = getLoggerContext();
		return ConfigurationWatchListUtil.getMainWatchURL(context);
	}
	
	public static String getProperty(String key){
		return configurator==null?null:configurator.getProperty(key);
	}
	public static void setProperty(String key, String val){
		configurator.setProperty(key,val);
	}

	@SuppressWarnings("unchecked")
	protected static Map<String, Appender<ILoggingEvent>> getAppenders(){
		Map<String, Object> objectMap = configurator.getInterpretationContext().getObjectMap();		
		return (Map<String, Appender<ILoggingEvent>>)objectMap.get(ActionConst.APPENDER_BAG);
 	}	
	
	//TODO 获取历史滚动文件
	/**
	 * 当前使用的日志记录文件
	 * @return
	 */
	public static Map<String, String> getFileAppenders(){
		Map<String,String> map = new HashMap<>();
		for(Entry<String, Appender<ILoggingEvent>> ent:getAppenders().entrySet()){
			Appender<ILoggingEvent> appd = ent.getValue();
			if(ent.getValue() instanceof FileAppender){
				map.put(ent.getKey(), ((FileAppender<ILoggingEvent>)appd).getFile());
			}
		}
    	return map;
	}
	
	/**
	 * 当前全局的日志上下文，可以知道配置信息
	 * @return
	 */
	public static LoggerContext getLoggerContext(){
	   	//todo: needn't default ch.qos.logback.classic.util.ContextInitializer#autoConfig();
		Object obj = LoggerFactory.getILoggerFactory();
		if(obj instanceof LoggerContext){
			return (LoggerContext)obj;
		}
		logger.warn("Wrong ILoggerFactory [{}].", obj==null?"null":obj.getClass());
		return null;
	}	
	
	/**
	 * 移除某个日志记录里面的输出通道
	 * @param logName
	 * @param appender
	 * @return
	 */
	public static boolean removeAppender(String logName, String appender){
		boolean removed =false;
		Logger log =LoggerFactory.getLogger(logName);
		if(log instanceof ch.qos.logback.classic.Logger){
			removed = ((ch.qos.logback.classic.Logger)log).detachAppender(appender);
			logger.info("logger[{}] removed appender[{}]",logName,appender);
		}
		return removed;
	}
	
	/**
	 * 当前已经有过记录的日志名
	 * @return
	 */
	public static Set<String> getCachedLoggers(){
		try{
			Field field = LoggerContext.class.getDeclaredField("loggerCache");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String,?> map=(Map<String, ?>)field.get(getLoggerContext());
			return map.keySet();
		}catch(Exception e){
			return null;
		}
		/*		
		List<String> logs = new ArrayList<>();
		List<ch.qos.logback.classic.Logger> list = getLoggerContext().getLoggerList();
		for(ch.qos.logback.classic.Logger log: list){
			logs.add(log.getName());
		}
		return logs;
		*/
	}
	
	/**
	 * @return Statu list which hold the logger init info
	 */
	public static List<Status> getStatus(){
		return getLoggerContext().getStatusManager().getCopyOfStatusList();
	}


 }