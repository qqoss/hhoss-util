package com.hhoss.boot;
import java.util.Arrays;

import com.hhoss.jour.Logger;


public class Run {  
   private static final Logger logger = Logger.get();
   private static final long num = 123_456_7859_0235L;

    public static void main(String[] args) throws InterruptedException {
    	logger.info("input args: {}",Arrays.toString(args));
    	String[] arr= "123,456,789.6".split(",");
    }
}