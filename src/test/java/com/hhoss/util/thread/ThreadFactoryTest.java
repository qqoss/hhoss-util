package com.hhoss.util.thread;

import com.hhoss.boot.App;
import com.hhoss.jour.Logger;

public class ThreadFactoryTest {
	private static final Logger logger = Logger.get();

	public static void main(String[] args) {
		App.defaultInitial();
		logger.debug(ThreadFactory.getThreads("a").toString());
		logger.debug(ThreadFactory.getStacks("a", 1));
		logger.debug(ThreadFactory.interrupt("a"));
	}

}
