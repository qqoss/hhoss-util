package com.hhoss.util;

//TODO:前导零的问题，bug。。。。
public class Random extends java.util.Random{
	private static final long serialVersionUID = 1L;
	private static final Random random=new Random();
	
	/*
	 * System.currentTimeMillis() has 13 number length;  1/8000 per ms
	 * String return a 17 length string by TimeMillis.substring(3,13)
	 * recent 300 years and random4 in [1024,9216)(0x2400=8192+1024)
	 */
	public static String int17(){
		return ts(13)+(random.nextInt(1<<13)+(1<<10));
        //MDC.put("requestUUID", uuid.replaceAll("-", "").toUpperCase());  
	}
	
	/*
	 * String return a 17 length string by TimeMillis.substring(3,13)
	 * for recent 3 years and random4 in [1024,9216)(0x2400=8192+1024)
	 */
	public static String int15(){
		return ts(11)+(random.nextInt(1<<13)+(1<<10));
        //MDC.put("requestUUID", uuid.replaceAll("-", "").toUpperCase());  
	}
	
	/*
	 * String return a 14 length string by TimeMillis.substring(3,13) & random4
	 * for recent 4 months and random4 in [1024,9216)(8192+1024);  1/8000 per ms
	 */
	public static String int14(){
		return ts(10)+(random.nextInt(1<<13)+(1<<10));
		//String uuid = UUID.randomUUID().toString();  
        //MDC.put("requestUUID", uuid.replaceAll("-", "").toUpperCase());  
	}
	
	/*
	 * String return a 11 length string by TimeMillis.substring(5,13) & random3
	 * for daily and random3 in [256,768)(512+256),  1/500  per ms
	 */
	public static String int11(){
		return ts(8)+(random.nextInt(1<<9)+(1<<8));
		//String uuid = UUID.randomUUID().toString();  
        //MDC.put("requestUUID", uuid.replaceAll("-", "").toUpperCase());  
	}
	
	/*
	 * String return a 9 length string by TimeSeconds & random1
	 * for hours and random2 in [32,96)
	 */
	public static String int9(){
		return ts(7)+(random.nextInt(1<<6)+(1<<5));
		//String uuid = UUID.randomUUID().toString();  
        //MDC.put("requestUUID", uuid.replaceAll("-", "").toUpperCase());  1/64 per ms
	}
	
	/*
	 * String return a 7 length string by TimeSeconds & random1
	 * for hours and random1 in [0,8)
	 */
	public static String int8(){
		return ts(7)+random.nextInt(8);
		//String uuid = UUID.randomUUID().toString();  
        //MDC.put("requestUUID", uuid.replaceAll("-", "").toUpperCase());  
	}
	
	public static String ts(int n){
		if(n>13||n<1){throw new IllegalArgumentException("");}
		char[] chrs = Long.toString(System.currentTimeMillis()).toCharArray();
		int off = chrs.length-n;
		if(chrs[off]=='0'){chrs[off]='5';}
		return new String(chrs,off,n);
	}
	
	public static int get(int max){
		return random.nextInt(max);
	}
	public static long get(){
		return random.nextLong();
	}

}
