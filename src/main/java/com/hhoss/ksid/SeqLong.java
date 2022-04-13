package com.hhoss.ksid;

import static com.hhoss.util.Num.S10;
import static com.hhoss.util.Num.S11;
import static com.hhoss.util.Num.S12;
import static com.hhoss.util.Num.S13;
import static com.hhoss.util.Num.S14;
import static com.hhoss.util.Num.S15;
import static com.hhoss.util.Num.S16;
import static com.hhoss.util.Num.S17;
import static com.hhoss.util.Num.S18;
import static com.hhoss.util.Num.S19;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kejun
 *
 */
public class SeqLong {	
	private static final long MIN[] = new long[]{S10,S11,S12,S13,S14,S15,S16,S17,S18,S19};
	private final AtomicLong atomic;	
	private final long min;
	private final long max;
	
	/**
	 * @param size of sequence length, should between 10 and 19
	 */
	public SeqLong(int size) throws IndexOutOfBoundsException{
		min = MIN[size-10];			
		max = min|(min-1);
		atomic = new AtomicLong(min);
	}
	
    /**
     * cyclic sequence between min and max.
     * @return
     */
    public final long next() {    	
        while(true){
            long current = atomic.get();
            if(atomic.compareAndSet(current,(current+1)&max|min))
                return current;
        }
    }

    public String text(){
    	return Long.toString(next());
    }    
   
}
