package com.hhoss.util.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.hhoss.jour.Logger;

public class ThreadFactory implements java.util.concurrent.ThreadFactory {
	private static final Logger logger = Logger.get();
	private static final ThreadGroup THREAD_GROUP = new ThreadGroup("thread.group");
    private static final AtomicInteger poolRank = new AtomicInteger(10);
    private final AtomicInteger threadRank = new AtomicInteger(10);
    private final ThreadGroup groups;
    private final String prefix;
    
    /**
     *  using default thread name: pool%d-work, name is pool%d-work%d. 
     *  jdk default Name : "pool-%d-thread-%d"
     */
    public ThreadFactory() {
    	this("pool%d-work");
    }

    /**
     * 
     * @param pattern the thread name prefix pattern, the thread name will be {pattern}%d = {pattern}342
     */
    protected ThreadFactory(String pattern) {
       // SecurityManager s = System.getSecurityManager();
       // groups = (s != null) ? s.getThreadGroup() :  Thread.currentThread().getThreadGroup();
        prefix = String.format(pattern,poolRank.incrementAndGet());
        groups = new ThreadGroup(THREAD_GROUP,prefix);
        // "pool-" +poolNumber.getAndIncrement() + "-thread-";
    }
    
    /**
     * @return the pool prefix name
     */
    public String getPrefix(){
    	return prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(groups, r, prefix + threadRank.incrementAndGet(), 0);
        if (t.isDaemon()){
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY){
            t.setPriority(Thread.NORM_PRIORITY);
        }
    	logger.trace("created a new thread[{}].",t.getPriority());
        return t;
    }
	
	/**
	 * @param  threadName part of thread name
	 * @param stackSize filter by stack size, eg. idle thread stack size < 16  
	 * @return the stack of thread which name matched
	 */
	public static String getStacks(String threadName, int stackSize ){
		 Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		 StringBuilder sb = new StringBuilder("Total threads ");
		 sb.append(map.size()).append("\r\n");
		 int i=0;
		 for(Thread t : map.keySet()){
			 if(t.getName().indexOf(threadName)>-1 && map.get(t).length>stackSize ){
				i++;
				sb.append("======statcks for ").append(t.getName()).append(">>>>>\r\n");
				for(StackTraceElement se : map.get(t)){
					sb.append(se).append("\r\n");
				}
			 }
		 }
		 sb.append("<<<<<Matched threads ").append(i);
		 return sb.toString();
	}
   
	/**
	 * @param threadName part of thread name
	 * @return the app active thread list which created by ThreadFactory.class
	 */
	public static List<Thread> getThreads(String threadName){
		List<Thread> list = new ArrayList<>();
		Thread[] threads= new Thread[THREAD_GROUP.activeCount()+8];
		THREAD_GROUP.enumerate(threads);
		for(Thread t:threads){
			if(t!=null&&t.getName().indexOf(threadName)>-1){
				list.add(t);
			}
		}
		return list;
	}
	
	/**
	 * @param  threadName part of thread name
	 * @return result info of execute
	 */
	public static String interrupt(String threadName){
 		StringBuilder sb = new StringBuilder("interrupt threads ... ");
		int i=0;
		for(Thread t: getThreads(threadName)){
			if(t.isAlive())try{
				sb.append("\r\n").append(t.getName());
				t.interrupt();
			}catch(Throwable e){
				sb.append(" -> ").append(e.getMessage());
			}
		}
		sb.append("\r\n handled ").append(i);
	   	logger.info("interrupted {} threads for [{}].",i,threadName);
		return sb.toString();
	}

}
