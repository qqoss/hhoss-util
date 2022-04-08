package com.hhoss.ksid;

import com.hhoss.jour.Logger;
import com.hhoss.ksid.SeqLong;

public class SeqLongTest {
	private static final Logger logger = Logger.get();    
   
    public static void testLong(){
    	for(int i=9;i<21;i++){
    		long s = 0;    		
    		try{
    			s = new SeqLong(i).next();
    		}catch(Exception e){
    			logger.debug("exception {}:{}",i,e.getMessage());
    		}
    		logger.info("seq length {} first: {}",i, s);
    	}
    }

    public static void main(String[] args){
    	testLong();
    }

}
