package com.hhoss.ksid;

import java.util.concurrent.atomic.AtomicInteger;

public class SeqInt {	
	private final int maxium;
	private final AtomicInteger atomic;	
	
	/**
	 * @param bits size of sequence, should less than 31
	 */
	public SeqInt(int size){
		maxium = (1<<size)-1;
		atomic = new AtomicInteger();
	}
	
	
    /**
     * cyclic sequence between 0 and max.
     * @return next seq int
     */
    public final int next() {    	
        while(true){
            int current = atomic.get();
            if(atomic.compareAndSet(current,(current+1)&maxium)){
                return current;
            }
        }
    }

    public String text(){
    	return Integer.toString(next());
    }
   
}
