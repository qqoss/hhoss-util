package com.hhoss.util;

import java.util.ArrayList;
import java.util.List;

import com.hhoss.jour.Logger;
public class RandomTest{
	private static final Logger logger = Logger.get();    
	
	private static List<String> getRandoms(int nums){
		List<String> strs = new ArrayList<>();
		for(int i=0;i<10;i++){
			switch(nums){
				case 8: strs.add(Random.int8());	break;
				case 9: strs.add(Random.int9());	break;
				case 11: strs.add(Random.int11());	break;
				case 14: strs.add(Random.int14());	break;
				case 17: strs.add(Random.int17());	break;
			}			
		}
		return strs;
	}
	
	private static void test(){
		for(int i=8;i<15;i++){
			logger.info("random[{}] test:{}",i,getRandoms(i));
		}
	}
	
	public static void main(String[] args){
		test();
	}

}
