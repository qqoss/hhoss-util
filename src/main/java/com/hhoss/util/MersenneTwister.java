package com.hhoss.util;


/**
 * @author kejun
 * @see http://blog.csdn.net/xiadasong007/article/details/18819463
 * https://baike.baidu.com/item/MersenneTwister%E7%AE%97%E6%B3%95
 * https://en.wikipedia.org/wiki/Mersenne_Twister
 * 
 */
public class MersenneTwister {
	
	private static final int LENGTH = 624;
	/* Create a length 624 array to store the state of the generator */
	private int [] MT = new int[LENGTH];
	private int idx;
	private boolean isInitialized = false;

	/* Initialize the generator from a seed */
	private void msInit(int seed) {
		int i;
		int p;
	    idx = 0;
	    MT[0] = seed;
	    for (i=1; i < LENGTH; ++i) { /* loop over each other element */
	        p = 1812433253 * (MT[i-1] ^ (MT[i-1] >> 30)) + i;
	        MT[i] = p & 0xffffffff; /* get last 32 bits of p */
	    }
	    isInitialized = true;
	}
	
	private int msRand() {
	    if (!isInitialized){
	    	return 0;
	    }
	    	

	    if (idx == 0)
	        msRenerate();

	    int y = MT[idx];
	    y = y ^ (y >> 11);
	    y = y ^ ((y << 7) & (int)2636928640l);
	    y = y ^ ((y << 15) & (int)4022730752l);
	    y = y ^ (y >> 18);

	    idx = (idx + 1) % LENGTH; /* increment idx mod 624*/
	    return y;
	}
	 
	private void msRenerate() {
	    int i;
	    int y;
	    for (i = 0; i < LENGTH; ++i) {
	        y = (MT[i] & 0x80000000) + 
	                (MT[(i+1) % LENGTH] & 0x7fffffff);
	        MT[i] = MT[(i + 397) % LENGTH] ^ (y >> 1);
	        if (y % 2 != 0) { /* y is odd */
	            MT[i] = MT[i] ^ (int)(2567483615l);
	        }
	    }
	}
	
	//随机种子
	public void rseed(int seed){
		if(isInitialized){
			return ;
		}
		msInit(seed);
	}
	
	//随机值
	public int rand(){
		if(isInitialized == false){
			return 0;
		}
		return msRand();
	}
	
	public MersenneTwister(int seed){
		rseed(seed);
	}

}
