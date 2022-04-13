package com.hhoss.util;

import java.util.Arrays;

/**
 * @author kejun
 * @version 0.1.1
 * @date 2015-09-03
 *
 */
public class PrimeUtil {
	private static long[] primes = new long[]{2,3,5};
	private static int k = primes.length; //like list.modeCount
	static{
		//PrimeUtil.nextPrime(34139969);
		//System.out.println(PrimeUtil.nextPrime(34139969));
		// assign first 768 primes, the 768 is 5849, then cached prime can judge prime<=34140649
	}

	public static long get(int index) {
		long p = primes[k-1];
		while( index >= k ){
			p+=2;
			long s = (long)Math.ceil(Math.sqrt(p));
			for(int i=1;i<k;i++){
				//if( primes.get(i) * primes.get(i) > n ){
				if( primes[i]>s ){
					add(p);
					break;
				}
				
				if(p % primes[i] == 0){
					break;
				}
			}
		}
		return primes[index];
	}

	/**
	 * @param n the int prime, don't too big
	 * @return the index by prime order, 0,1,2,3,4.... prime[0]=2, prime[1]=3, prime[2]=5
	 * TODO seems very slow
	 */
	public static int indexOf(int n) {
		int i=0;
		while( get(i)<n ){
			i++;
		}
		return i;
	}
	
	public static long[] getPrimes(long start, int count){
		long[] arr = new long[count];
		long m=start-1;
		for(int i=0;i<count;i++){
			System.out.println(m=arr[i]=PrimeUtil.nextPrime(++m));
		}
		return arr;
	}

	/**
	 * @param n
	 * @return 比n小的最大质数
	 * @deprecated using pre(n) is faster!
	 */
	public static long prevPrime(long n) {
		if(n<3) return 2;
		long s = (long)Math.ceil( Math.sqrt(n));
		System.out.println();
		while( primes[k-1] <= s ){
			//should be <=s if no = ,the test PrimeUtil.prevPrime(66601889) will return 2! 
			//在(m-1)^2   到m^2  之间至少有一个质数。故而比目标小的所有因子都在primes里面！
			get(k);//lastPrime increase;
		}
		
		long p = n|1 ; //从当前奇数或的下一个奇数开始递减；
		while( p > 2 ){
			p-=2;
			for(int i=1;i<k;i++){
				if( primes[i]>s ){
					return p;//finish return;
				}else if(p % primes[i] == 0){
					break;
				}
			}
		}
		return 2;
	}

	
	/**
	 * @param n
	 * @return 比n大的最小质数
	 * @deprecated using next(n) is faster!
	 */
	public static long nextPrime(long n) {
		if(n<2) return 2;
		long s = (long)Math.ceil( Math.sqrt(n));
		while( primes[k-2] < s ){
			//在(m-1)^2   到m^2  之间至少有一个质数。故而比目标小的所有因子都在primes里面！
			get(k);//lastPrime increase;
		}

		long p = n|1 ; //从当前奇数或的下一个奇数开始递增
		while(true){
			for(int i=1;i<k;i++){
				if( primes[i]>s ){
					return p;//finish return;
				}else if(p % primes[i] == 0){
					break;
				}
			}
			p+=2;
		}
	}

	private static long nextPrime2(long n) {
		long p = primes[k-1];
		if(p>n){// in the cache！
			int i=0;
			//while(getPrime(++i)<=n){};
			while(i++>=0){
				if(get(i)>n){
					break;
				};
			}
			return get(i);
		}
		
		int i = 0; p += 2;
		int s = (int)Math.ceil( Math.sqrt(n));
		while( get(++i)<=s || p<=n ){
			if(p % get(i) == 0){
				i=0;p+=2;
			}
		}
		return p;
	}
	
	/**
	 * @param n
	 * @return true if n is prime 
	 * p=123456789123487 运行时间: 3640 ms;
	 */
	public static boolean isPrimeByCache(long n) {
	    return nextPrime(n)==n;
	}	
	
	private static void add(long p) {
	    if( primes.length<=k ){
	    	primes = Arrays.copyOf(primes, k<<1);
	    }
	    primes[k]=p;
	    k++;
	 }
	
	/**
	 * @param n
	 * @return true if n is prime 
	 * more efficiency
	 */
	public static boolean isPrime(long n) {
		int s = (int)Math.sqrt(n);
	    if (n < 4) {
	        return n > 1;
	    }
	    if (n % 2 == 0 || n % 3 == 0) {
        	//System.out.println("the module is " + 3);
	        return false;
	    }
	 
	    for (long i = 5; i < s; i += 6) {  	
	        if (n % i == 0 || n % (i + 2) == 0) {
	        	//System.out.println("the module is " + ((n % i == 0)?i:(i+2)));
	            return false;
	        }
	    }
	    return true;
	}	

	public static long prev(long n) {
		for(long p=n;;){
			if(isPrime(--p)){
				return p;
			}
		}
	}

	public static long next(long n) {
		for(long p=n;;){
			if(isPrime(++p)){
				return p;
			}
		}
	}

	//TODO:need test
	public static int sqrt(int a) {
		int rem = 0;
		int root = 0;
		int divisor = 0;
		for (int i = 0; i < 16; i++) {
			root <<= 1;
			rem = ((rem << 2) + (a >> 30));
			a <<= 2;
			divisor = (root << 1) + 1;
			if (divisor <= rem) {
				rem -= divisor;
				root++;
			}
		}
		return (int) (root);
	}
    
	public static void main(String[] args){
		long t = 0;
		
		t = System.currentTimeMillis();			
		//System.out.println("nextPrime: "+PrimeUtil.nextPrime(66601889));
		System.out.print("prevPrime: "+PrimeUtil.prevPrime(66601889));
		//System.out.print("nextPrime: "+PrimeUtil.nextPrime(123456789012301L));
		System.out.println("; 运行时间: " + (System.currentTimeMillis() - t)+" ms;");
		/*		
		for(int i=100;i<300;i++){
			System.out.println(i+" sqrt: "+PrimeUtil.sqrt(i));
		}
		for(int i=0;i<1000;i++){
			System.out.println("prime at "+i+": "+PrimeUtil.get(i));
		}
		t = System.currentTimeMillis();		
		System.out.print("nextPrime: "+PrimeUtil.nextPrime(123456789123458L));
		System.out.println("; 程序运行时间: " + (System.currentTimeMillis() - t)+" ms;");
		t = System.currentTimeMillis();		
		System.out.print("next  : "+PrimeUtil.next(123456789123458L));
		System.out.println("; 运行时间: " + (System.currentTimeMillis() - t)+" ms;");
	
		
		for(int i=0;i<10;i++){
			System.out.println(i+"th: "+PrimeUtil.get(i)+" ,");
		}
		
		for(int i=0;i<10;i++){
			System.out.println("indexOf "+i+" is "+PrimeUtil.indexOf(i)+" ,");
		}
	
		for(long p=123456789012300L;p<123456789012345L;p++){
			t = System.currentTimeMillis();		
			System.out.print("\risPrime  : "+p+": "+PrimeUtil.isPrime(p));
			System.out.println("; 运行时间: " + (System.currentTimeMillis() - t)+" ms;");
			
			t = System.currentTimeMillis();		
			System.out.print("nextPrime: "+PrimeUtil.nextPrime(p));
			System.out.println("; 程序运行时间: " + (System.currentTimeMillis() - t)+" ms;");
			t = System.currentTimeMillis();		
			System.out.print("next: "+PrimeUtil.next(p));
			System.out.println("; 程序运行时间: " + (System.currentTimeMillis() - t)+" ms;");
	
			t = System.currentTimeMillis();		
			System.out.print("prevPrime: "+PrimeUtil.prevPrime(p));
			System.out.println("; 程序运行时间: " + (System.currentTimeMillis() - t)+" ms;");
			t = System.currentTimeMillis();		
			System.out.print("prev: "+PrimeUtil.prev(p));
			System.out.println("; 程序运行时间: " + (System.currentTimeMillis() - t)+" ms;");
		}
			 */		
		
		
	}

}
