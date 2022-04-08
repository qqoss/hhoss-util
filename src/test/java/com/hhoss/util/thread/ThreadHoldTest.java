
package com.hhoss.util.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.hhoss.jour.Logger;
import com.hhoss.ksid.Key;

/**
 * @author kejun
 * @see java.util.concurrent.Executors.DefaultThreadFactory
 */
public final class ThreadHoldTest{
	private static final Logger LOG = Logger.get();
	
	public void test() throws InterruptedException{
		ExecutorService workers = ThreadPool.get(3);
		ThreadHold.set("name1","main1");
		ThreadHold.set("name2","main2");
		for(int i=0;i<30;i++){
			//TimeUnit.MILLISECONDS.sleep(200l);
			workers.execute(newRunnable(i));
		}
		TimeUnit.MILLISECONDS.sleep(1000l);
		for(int i=0;i<50;i++){
			workers.execute(newRunnable(i+50));
		}
		workers.shutdown();
	}
	public static void test2(){
		ThreadPool workers = (ThreadPool)ThreadPool.get();
		LOG.info(((ThreadFactory)workers.getThreadFactory()).getPrefix());;
	}
	
	private Runnable newRunnable(final int i){
		return new Runnable(){@Override public void run() {
			if(i<10){
				ThreadHold.set("name2","child"+i);
			}
			ThreadHold.set("name3","child"+Key.next());
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LOG.info("work={},name1={},name2={},name3={}",i,ThreadHold.get("name1"),ThreadHold.get("name2"),ThreadHold.get("name3"));
		}};
	}
	
	public static void main(String[] args) {
		try {
			test2();
			new ThreadHoldTest().test();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

