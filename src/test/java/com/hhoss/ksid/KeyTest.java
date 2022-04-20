package com.hhoss.ksid;

import com.hhoss.jour.Logger;
import com.hhoss.util.Num;

public class KeyTest {
	private static final Logger logger = Logger.get();    
	private static final String SF="%9$d %1$d-%2$02d-%3$02d %4$02d:%5$02d:%6$02d %7$d@%8$d";
    /**
     * @param lsn long seq generated by Key.next();
     * @return str of appIns-ymd-hms-sn
     */
    public static String format(int[] f){
    	return String.format(SF,f[1],f[2],f[3],f[4],f[5],f[6],f[7],f[8],f[0]);
    }
    
    public static void test(){
    	for(int i=0;i<3;i++){
    		long seq = Key.next();
	    	logger.debug("seq:{} => {}",seq, format(Key.meta(seq)));
    	}
    	for(int i=0;i<Num.S6*2-6;i++){Key.next();}
    	for(int i=0;i<5;i++){
    		long seq = Key.next();
	    	logger.debug("seq:{} => {}",seq, format(Key.meta(seq)));
    	}
    }
  
    public static void main(String[] args){
       	test();
       	int tims = 0;
       	logger.debug("---");
       	for(int i=0;i<10_000_000;i++){tims=(int)(System.currentTimeMillis()/1000);}//31位到秒(2036年超过31位)(2103年31位32位)；
       	logger.debug("---");
       	for(int i=0;i<10_000_000;i++){tims=Tims.get();}
       	logger.debug("---"); 
     } 
    
}
