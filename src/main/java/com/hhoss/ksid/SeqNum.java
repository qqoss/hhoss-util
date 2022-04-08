package com.hhoss.ksid;

import static com.hhoss.lang.Num.S3;
import static com.hhoss.lang.Num.S4;
import static com.hhoss.lang.Num.S5;
import static com.hhoss.lang.Num.S6;
import static com.hhoss.lang.Num.S7;
import static com.hhoss.lang.Num.S8;
import static com.hhoss.lang.Num.S9;

import java.util.concurrent.atomic.AtomicInteger;

public class SeqNum {	
	private static final int MIN[] = new int[]{1,4,32,S3,S4,S5,S6,S7,S8,S9};
	private final AtomicInteger atomic;	
	private final int min;
	private final int max;
	
	/**
	 * @param size of sequence length, should between 3 and 9
	 */
	public SeqNum(int size) throws IndexOutOfBoundsException{
		min = MIN[size];
		max = min|(min-1);
		atomic = new AtomicInteger(min);
	}
	
	
    /**
     * cyclic sequence between min and max.
     * @return
     */
    public final int next() {    	
        while(true){
            int current = atomic.get();
            if(atomic.compareAndSet(current,(current+1)&max|min))
                return current;
        }
    }

    public String text(){
    	return Integer.toString(next());
    }
	
	/**  @return SeqInt for 4 numbers  */
	public static SeqNum len4(){return new SeqNum(4);}

	/**  @return SeqInt for 5 numbers  */
	public static SeqNum len5(){return new SeqNum(5);}

	/**  @return SeqInt for 6 numbers  */
	public static SeqNum len6(){return new SeqNum(6);}

	/**  @return SeqInt for 7 numbers  */
	public static SeqNum len7(){return new SeqNum(7);}

	/**  @return SeqInt for 8 numbers  */
	public static SeqNum len8(){return new SeqNum(8);}

	/**  @return SeqInt for 9 numbers  */
	public static SeqNum len9(){return new SeqNum(9);}

   
}
