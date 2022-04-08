package com.hhoss.util.thread;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.slf4j.MDC;

import com.hhoss.boot.App;
import com.hhoss.jour.Logger;
import com.hhoss.jour.StopWatch;
import com.hhoss.ksid.Key;
import com.hhoss.lang.Classes;

/**
 * wrapped a command, the thread variables stored in MDC can be inherited
 * @author kejun
 * @param <V>
 */
public class TaskCall<V> implements Callable<V>, Runnable{
	private static final Logger logger = Logger.get();
	private static final int monitorBeforeCall = getOption("run.thread.monitor.before.call",200);
	private static final int monitorFinishTask = getOption("run.thread.monitor.finish.task",2000);
	private Map<String,String> contextMap;	
	private final String name;
	private Object task;	
	/**original is Runnable; */
	private boolean orun; 
	private StopWatch timer = new StopWatch("created");
	
	private TaskCall(Runnable wrapped){
		this();
		this.orun=true;
		this.task=wrapped;
	}
	
	private TaskCall(Callable<V> wrapped){
		this();
		this.task=wrapped;
	}
	
	private TaskCall() {
		String callerName = Key.text();
		for(Class<?> cls: Classes.callers()){
			if((!TaskCall.class.isAssignableFrom(cls))&&
				!ExecutorService.class.isAssignableFrom(cls) ){		
				//&& cls.getSimpleName().toLowerCase().indexOf("pool")=-1 ){	//should exclude org.quartz.spi.ThreadPool
				callerName=cls.getSimpleName()+"-"+callerName;
				break;
			}
		}			
		this.name=callerName;
		logger.debug("task[{}] created at {}.",name,new Date());
	}

	@Override
	public final V call(){
		return invoke();
	}
	
	@Override
	public final void run() {
		invoke();
	}

	public static Runnable get(Runnable task){
		TaskCall<?> tt = (task instanceof TaskCall)?(TaskCall<?>)task:new TaskCall<Object>(task);
		return tt.saveThreadMap();
	}

	protected static <T> Callable<T> get(Callable<T> task){
		TaskCall<T> tt = (task instanceof TaskCall)?(TaskCall<T>)task:new TaskCall<T>(task);
		return tt.saveThreadMap();
	}	
		
	//@see org.springframework.util.ReflectionUtils#findMethod()
	public static Callable<?> get(final Object object, final String method, final Object... params){
		if(method==null){
			logger.error("method is NULL, dynamic invoke method IGNORED.");
			return null;
		}else if(object==null){
			logger.error("object or method is NULL, dynamic invoke method[{}] IGNORED.",method);
			return null;
		}
		final Class<?>[] classes = new Class[params==null?0:params.length];
		for(int i=0;i<classes.length;i++){
			if(params[i]==null){
				logger.error("args[{}] is null, dynamic invoke method[{}] IGNORED.",i,method);
				return null;
			}else{
				classes[i]=params[i].getClass();
			}
		}
		return TaskCall.get(new Callable<Object>(){
			public @Override Object call() throws Exception {
				Method mth=object.getClass().getMethod(method,classes);
				//if(method.getReturnType().equals(Void.TYPE)){}
				return mth.invoke(object, params);
			}
		});
	}
	
	/**
	 * @param isCall if we have return value, we should call, otherwise run is enough
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private V invoke(){
		try {
			syncThreadMap();
			if(task==null){
				logger.error("task[{}] havn't be set or is null, ignore the task.",name);
				return null;
			}
			long tc = timer.getTime();
			if( tc>monitorBeforeCall ){
				logger.info("run task[{}] after created [{}] ms.",name,tc);
			}
			timer.reset("started");
			if(orun&& task instanceof Runnable){
				((Runnable)task).run();
			}else if( task instanceof Callable ){
				return ((Callable<V>)task).call();
			}else if( task instanceof Runnable ){
				((Runnable)task).run();
			}else{
				logger.error("task[{}][{}] can't be called or run.", name, task.getClass());
			}
		} catch (Exception e) {
			logger.error("Async task[{}] throw exception: [{}]",name, e.getMessage(), e);
		}finally{
			long ts = timer.getTime();
			if( ts>monitorFinishTask ){
				logger.info("task[{}] finished after running [{}] ms.",name,ts);
			}
		}
		return null;
	}
	
	
	private TaskCall<V> saveThreadMap(){		
		contextMap=MDC.getCopyOfContextMap();
		return this;
	}
	
	private void syncThreadMap(){
		if(contextMap!=null){
			MDC.clear();
			MDC.setContextMap(contextMap);
		}
		if( MDC.get(ThreadKeys.REQ_REFER)==null ){
			MDC.put(ThreadKeys.REQ_REFER, name);
		}
	}
	@Override 
	public String toString(){
		return name;
	}
	
	private static int getOption(String key, int def){
		//Integer c = ((ResHolder)App.getProperties("res.app.module.options")).getInteger(key);
		String c = App.getProperty("res.app.module.options",key);
		if(c!=null)try{
			return Integer.decode(c);
		}catch(Exception e){}
		return def;
	}

	
}

