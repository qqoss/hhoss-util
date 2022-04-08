package com.hhoss.ksid;
/**
 * the class for generate capital letter and numeric fron long;
 * the string is readable for human and qrCode.
 * @author kejun
 *
 */
public class CapNum {	
    private static final char[] CODE32 = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ34567".toCharArray();	 
    /**
     * 
     * @param str ,should not null and LE 13 chars
     * @return the num as long, 0 if wrong 
     */
    public static long decode(String str) {
    	return decode(str,false);
    }
    
	/**
	 * @param str the encode string
	 * @param suffix 最后一位是否为校验位
	 * @return the num as long, 0 if wrong 
	 */
	public static long decode(String str, boolean suffix) {
    	long num = 0;
    	if(str==null){return num;}
    	int len = str.length();
    	if(suffix){len--;}
    	if(len>13) {//should NOT to here. invoker should check it
    		return num;
    	}
    	char[] codes = str.toCharArray();
    	for(int i=0;i<len;i++) {
    		num<<=5;
    		num|=(long)of(codes[i]);
    	}
    	return num;
    }
    
    public static String encode(long num) {
    	if(num<1) {
    		//should NOT to here. invoker should check it
    		return "";
    	}
        long code=num;
    	int len=13,i=13;//valid chars
    	char[] codes = new char[len];
     	while(code>0) {
    		codes[--i]= CODE32[(int)code&0x1F];
    		code >>>= 5;
    	}
        return new String(codes,i,len-i);
    }
    
    /**
     * @param num
     * @param prefix 是否保留零|9前缀
     * @param check 是否增加校验位
     * @return string of encodes
     */
    public static String encode(long num, boolean prefix, boolean check) {
    	if(num<1) {
    		//should NOT to here. invoker should check it
    		return "";
    	}
        long code=num;
        int rcpc=0; //Redundancy Column Parity Check
    	int len=13;//valid chars
    	char[] codes = new char[len+1];
        
        for(int i=len-1; i>=0; i--) {
        	int b = (int)code&0x1F;
        	codes[i]= CODE32[b];
        	code >>>= 5;
    		rcpc ^= b;
        }
        codes[len]= CODE32[rcpc];//check char
        int h = 0;
        if(!prefix){while(codes[h]==CODE32[0])h++;} //head is 0
        return new String(codes,h,len-h+(check?1:0));
    }
    
    /**
     * @param codes
     * @return boolean, true if passed the RCPC checks
     */
    public static boolean check(String strCode) {
    	if(strCode==null) { return false; }
    	char[] codes = strCode.toCharArray();
        int rcpc=0; //Redundancy Column Parity Check
        for(int i=codes.length-1; i>=0; i--) {
    		rcpc ^= of(codes[i]);
        }
        return rcpc==0;
    }
    
    
    /**
     * @param c
     * @return val : the index of the char in 
     */
    public static int of(char c) {//indexOf CODE32
    	if(c>0x40) {//'A'-'Z','a'-'z',不区分大小写
    		return c&0x1F; //c&0x1F; 1-31
    	}
    	if(c>0x32 && c<0x38) {//'3'-'7'
    		return (c&0x7)|0x18;//c-24; c&0x7|0x18; 27-31;
    	}
    	return 0;//zero is CODE32[0]='9'
    }
    
    /**
     * 
     * @param idx
     * @return the char in Of "9ABCDEFGHIJKLMNOPQRSTUVWXYZ34567"
     */
    public static char at(int idx) {
		if(idx<0||idx>0x1F){return 0;}
    	return CODE32[idx];
    }
    
    /**
     * @param val
     * @return the mask strings, show every char if the bit is 1;
     */
    public static String mask(int val) {
		String mark = "";
		for(int i=0;i<32;i++) {
			if((1<<i|val)==val){mark+=at(i);}
		}
		return mark;
    }
    
    /**
     * @param str of masks
     * @return the bitset int for the masks
     */
    public static int mask(String str) {
		if(str==null)return 0;
		int flag =0;
		for(char c : str.toCharArray()) {
			flag |= 1<<of(c);
		}
		return flag;
    }
    
}
