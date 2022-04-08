package com.hhoss.ksid;

import java.util.Calendar;

public class Tims {	
    
    /**
     * bit length for every datetime field.<br />
     * bl[0]: 5bit,年范围[0,32),最近的32年<br />
     * bl[1]:13bit,年范围[0,8192) 公元年，最大4位数字<br />
     * bl[2]: 4bit,月范围[1,12]<br />
     * bl[3]: 5bit,日范围[1,31]<br />
     * bl[4]: 5bit,时范围[0,24)<br />
     * bl[5]: 6bit,分范围[0,60)<br />
     * bl[6]: 6bit,秒范围[0,60)<br />
     * bl[7]:10bit,毫秒范围[0,999]<br />
     */
    private static final int[] bits=new int[]{5,13,4,5,5,6,6,10};//y,yyyy,M,d,h,m,s,ms
    private static final int[] mask=new int[]{0x1F,0x1FFF,0x0F,0x1F,0x1F,0x3F,0x3F,0x3FF};//y,yyyy,M,d,h,m,s,ms
	private static final ThreadLocal<Calendar> localendar = new ThreadLocal<Calendar>(){
		protected Calendar initialValue() { return Calendar.getInstance(); }
	};
	
    /**
     * instant seconds of time
     * return a int for current seconds(31bits{y5,m4,d5,h5,m6,s6}),as year%32
     * 不使用 System.currentTimeMillis()/1000作为秒数，因为(2003年超过30位)(2036年超过31位)(2103年超过32位)；
     * @return int contain the time info without milliseconds
     */
    public static int get() { 
		return from(meta());
    }

    /**
     * return a int for current seconds(31bits{y5,m4,d5,h5,m6,s6}),
     * @param meta[7|8]{y,yyyy,M,d,h,m,s,ms}
     * @return int contain the time info without milliseconds
     */
    static int from(int[] mt) {    	
		int ts = mt[0];
		for(int i=2;i<7;i++){
			ts=ts<<bits[i]|mt[i];
		}
		return ts;
    }

    /**
     * make a time array a int[8] for current milliseconds,
     * @return int[8]{y,yyyy,M,d,h,m,s,ms} contain the time info with milliseconds
     */
    static int[] meta() {    	
     	Calendar cal=localendar.get();
     	cal.setTimeInMillis(System.currentTimeMillis());
    	return meta(cal);
    }

    /**
     * restore the date meta fields from ts which generated from {@link Tims#get()} 
     * @param int seconds, generated from {@link Tims#get()}
     * @return int[8]{y,yyyy,M,d,h,m,s,0}
     */
    static int[] meta(int ts){
    	int ls = ts;
    	int[] flds = new int[8];
    	for(int i=6;i>1;i--){
    		flds[i]=ls&mask[i]; ls>>=bits[i];
    	}
    	flds[0] = ls&mask[0]; //year 
    	flds[1] = localendar.get().get(Calendar.YEAR)&(~mask[0])|flds[0]; //full year 
    	return flds;
    } 
    
    /**
     * make a time array a int[8] for current Calendar,
     * @return int[8]{y,yyyy,M,d,h,m,s,ms} contain the time info with milliseconds
     */
    private static int[] meta(Calendar cal){
    	int[] arr =new int[8];
    	arr[1] = cal.get(Calendar.YEAR);
    	arr[2] = cal.get(Calendar.MONTH)+1;
    	arr[3] = cal.get(Calendar.DAY_OF_MONTH);
    	arr[4] = cal.get(Calendar.HOUR_OF_DAY);
    	arr[5] = cal.get(Calendar.MINUTE);
    	arr[6] = cal.get(Calendar.SECOND);
    	arr[7] = cal.get(Calendar.MILLISECOND);
    	arr[0] = arr[1]&mask[0];
    	return arr;
    }  

    
}
