package com.hhoss.util.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hhoss.boot.App;
import com.hhoss.jour.Logger;
import com.hhoss.lang.Classes;

/**
 * @author kejun
 * @see java.util.concurrent.Executors.DefaultThreadFactory
 */
public final class ThreadPool extends ThreadPoolExecutor implements RejectedExecutionHandler{
	private static final Logger logger = Logger.get();
	private static final Properties options = App.getProperties("res.app.module.options");
	private static final Map<String,ThreadPool> workerMap=new HashMap<>();
	private String name;
	//private static final int DEF_SIZE = 8;
	
	/**
	 * @param name the pool key name
	 * @param basic the corePool thread basic count ,allow concurrent executing 
	 * @param queue the size of BlockingQueue, max count in queue
	 * @return ExecutorService
	 */
	protected ThreadPool(String name, int basic, int queue) {
		//super(size,1000,alive, TimeUnit.SECONDS, size<=1<<4?new LinkedBlockingQueue<Runnable>():new SynchronousQueue<Runnable>(), getFactory(name));
		super(basic, (int)Math.sqrt(basic*queue), 90, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queue), getFactory(name));
		//setMaximumPoolSize((int)Math.sqrt(count*queue));
		setRejectedExecutionHandler(this);
		this.name=name;
		logger.debug("Pool[{}] created: basic={},maxim={};queue={}",name,getCorePoolSize(),getMaximumPoolSize(),getQueue().getClass().getSimpleName());
		workerMap.put(name, this);
	}
/*	
	ThreadPool(){
		this(ClassMeta.caller().getSimpleName(),8, 8196);
		// Executors.newCachedThreadPool();
		// Executors.newFixedThreadPool(DEF_SIZE);
	}
*/	   

	@Override
	protected void beforeExecute(Thread t, Runnable r) { 
		ThreadHold.clear();//clear thread old variables;
		logger.trace("before[{}] in Thread[{}], Pool basic:{},count:{},active:{}; Task queue:{},total:{}.",r,t.getName(),getCorePoolSize(),getPoolSize(),getActiveCount(),getQueue().size(),getTaskCount());
		super.beforeExecute(t,r);
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r,t);
		logger.trace("finish[{}] from Pool[{}], Pool basic:{},count:{},active:{}; Task queue:{},total:{}. {}",r,name, getCorePoolSize(),getPoolSize(),getActiveCount(), getQueue().size(),getTaskCount());
		if(t!=null){logger.trace("async throwed [{}:{}] when run task[{}]",t.getCause(),t.getMessage(),r);}
		ThreadHold.clear();//clear current thread variables;
	}

	@Override
	protected void terminated() {
		logger.info("ThreadPool[{}] terminated, basic:{},count:{},active:{}; Task queue:{},total:{}.", name, getCorePoolSize(),getPoolSize(),getActiveCount(), getQueue().size(),getTaskCount());
		super.terminated();
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor ex) {
       String msg = "ThreadPool[{}] rejected task[{}]! basic:{},count:{},active:{}, maxim:{}, largest:{}; task total:{}, completed:{}; Executor shutdown:{}, terminated:{}, terminating:{}!"; 
       logger.warn(msg, ex==this?name:"", r, ex.getCorePoolSize(),ex.getPoolSize(), ex.getActiveCount(),  ex.getMaximumPoolSize(), ex.getLargestPoolSize(),
                ex.getTaskCount(), ex.getCompletedTaskCount(), ex.isShutdown(), ex.isTerminated(), ex.isTerminating());
	}
	
	/**
	 * retrieve already exists ThreadPool
	 * @param name
	 * @return ThreadPool which named
	 */
	public ThreadPool peek(String name){
		for(Entry<String,ThreadPool> ent : workerMap.entrySet()){
			if(ent.getKey().equalsIgnoreCase(name)){
				return ent.getValue();
			}
		}
		return null;
	}
	
	@Override
    public void execute(Runnable runnable) {
		Class<?>[] clazz=Classes.callers();
		for(int i=1;i<clazz.length;i++){
			if(!clazz[i].isInstance(this)){
				logger.trace("add async task from {}",clazz[i].getName() );
				break;
			}
		}
    	super.execute(TaskCall.get(runnable));
    }
	
	/**
	 * run in a generic pool for multi thread
	 * @param object to execute instance 
	 * @param method name of executed method 
	 * @param args parameters of the method 
	 */	
	public Future<?> execute(Object object, String method, Object... args) {
		return (Future<?>)submit(TaskCall.get(object, method,args));
	}
	
	/**
	 * allow remove idle core threads after seconds
	 * @param secs
	 * @return this
	 */
	public ThreadPool letAlive(int secs){
		setKeepAliveTime(secs,TimeUnit.SECONDS);
		allowCoreThreadTimeOut(true);
		return this;
	}
	
	/**
	 * set core threads for concurrent running
	 * @param size of basic concurrent threads
	 * @return this
	 */
	public ThreadPool letBasic(int size){
		setCorePoolSize(size);
		return this;
	}
	
	/**
	 * set the final threads max concurrent counts, not include the queue
	 * @param size of max allow concurrent threads, count(first+final)
	 * @return this
	 */
	public ThreadPool letAllow(int size){
		setMaximumPoolSize(size);
		return this;
	}
	
	/**
	 * run in a generic pool for multi thread
	 * @param runnable
	 */
	public static void run(Runnable runnable) {
		getWorker().execute(TaskCall.get(runnable));
	}
	
	/**
	 * run in a generic pool for multi thread
	 * @param runnable
	 */
	public static <V> Future<V> run(Callable<V> callable) {
		return getWorker().submit(TaskCall.get(callable));
	}
	
	/**
	 * run in a generic pool for multi thread
	 * @param object to execute instance 
	 * @param method name of executed method 
	 * @param args parameters of the method 
	 * @return Future
	 */
	public static Future<?> run(Object object, String method, Object... args) {
		return getWorker().submit(TaskCall.get(object, method,args));
	}	
	
	/**
	 * @return ExecutorService with name of caller's className and core size {@link #DEF_SIZE}; 
	 */
	public static ExecutorService get(){
		//return getWorker(ClassMeta.caller().getSimpleName());
		return getWorker();
	}
	
	/**
	 * @param name the pool suffix name
	 * @return ExecutorService with given name ;
	 */
	public static ExecutorService get(String name){
		return getWorker(name);
	}

	/**
	 * @param size  core pool size
	 * @return ExecutorService  with name of caller's className
	 */
	public static ExecutorService get(int count){
		String name = Classes.caller().getSimpleName();
		return getWorker(name,count,count*1000);
	}
	
	public static ExecutorService get(int count, int quene){
		String name = Classes.caller().getSimpleName();
		return getWorker(name,count,quene);
	}
		
	private static ThreadPool getWorker(){
		return getWorker(Classes.caller(2).getSimpleName());
	}
	
	private static ThreadPool getWorker(String name){
		int count = 8;
		String optCount = options.getProperty("run.thread."+name+".pool.basic");
		if( optCount!=null )try{
			count = Integer.decode(optCount.trim());
		}catch(Exception e){}
		
		int quene = 0;
		String optQuene = options.getProperty("run.thread."+name+".pool.quene");
		if( optQuene!=null )try{
			quene = Integer.decode(optQuene.trim());
		}catch(Exception e){}
		
		return getWorker(name,count,(quene<count)?count<<10:quene);
	}
	
	/**
	 * @param name the pool suffix name
	 * @param size the core pool size
	 * @return ExecutorService with name
	 */
	private static ThreadPool getWorker(String name, int count,int quene){
		ThreadPool es = workerMap.get(name);
		return(es==null)?new ThreadPool(name,count,quene):es;
	}
	
	private static ThreadFactory getFactory(String name){
		String key = (name==null||name.trim().length()==0)?"work":name.trim();
		return new ThreadFactory("pool%d-"+key+"-");
	}
	
	public @Override String toString(){
		return String.format("ThreadPool[%s] terminated, basic:%d,count:%d,active:%d; Task queue:%d,total:%d.", name, getCorePoolSize(),getPoolSize(),getActiveCount(), getQueue().size(),getTaskCount());

	}

}

