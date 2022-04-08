
package com.hhoss.util.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.hhoss.boot.App;
import com.hhoss.jour.Logger;
import com.hhoss.ksid.Key;

/**
 * @author kejun
 * @see java.util.concurrent.Executors.DefaultThreadFactory
 */
public final class ThreadPoolTest{
	private static final Logger LOG = Logger.get();
	
	public void test() throws InterruptedException{
		ExecutorService workers = ThreadPool.get(10);
		ThreadHold.push("name1","main1");
		ThreadHold.push("name2","main2");
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
	public void test3() throws InterruptedException{
		ThreadPool workers = (ThreadPool)ThreadPool.get();
		Runnable r = new Runnable(){@Override public void run() {}};
		for(int i=0;i<660;i++){
			workers.execute(r);
			if(i%100==0){
				TimeUnit.MILLISECONDS.sleep(1000);
				System.out.println(workers.toString());
			}
		}
	}
	
	private Runnable newRunnable(final int i){
		return new Runnable(){@Override public void run() {
			ThreadHold.push("name2","child"+Key.next());
			ThreadHold.push("name3","child"+Key.next());
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOG.info("work={},name1={},name2={},name3={}",i,ThreadHold.peek("name1"),ThreadHold.peek("name2"),ThreadHold.peek("name3"));
		}};
	}
	
	public static void main(String[] args) {
		try {
			App.defaultInitial();
			test2();
			new ThreadPoolTest().test3();
			//new ThreadPoolTest().test();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

