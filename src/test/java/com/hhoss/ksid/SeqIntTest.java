package com.hhoss.ksid;

import com.hhoss.jour.Logger;
import com.hhoss.ksid.SeqInt;

public class SeqIntTest {
	private static final Logger logger = Logger.get();    
  
    public static void testInt(){
    	for(int i=0;i<12;i++){
    		int s = 0;    		
    		try{
    			s = new SeqInt(i).next();
    		}catch(Exception e){
    			logger.debug("exception {}:{}",i,e.getMessage());
    		}
    		logger.info("seq length {} first: {}",i, s);
    	}
    }

    public static void main(String[] args) throws Exception{
    	testInt();
    }

}
