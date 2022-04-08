package com.hhoss.ksid;

import com.hhoss.jour.Logger;
import com.hhoss.ksid.SeqInt;
import com.hhoss.ksid.SeqNum;

public class SeqNumTest {
	private static final Logger logger = Logger.get();    
  
    public static void test(){
    	for(int i=0;i<12;i++){
    		int s = 0;    		
    		try{
    			s = new SeqNum(i).next();
    		}catch(Exception e){
    			logger.debug("exception {}:{}",i,e.getMessage());
    		}
    		logger.info("seq length {} first: {}",i, s);
    	}
    }

    public static void main(String[] args) throws Exception{
    	test();
    }

}
