package com.hhoss.ksid;

import java.util.Arrays;

import com.hhoss.jour.Logger;

/**
 * return global unique 26W sequence in every second
 * will wait to next second if need more
 * @author kejun
 *
 */
public class Seq {	
	private static final Logger logger = Logger.get();    
	private static final int BITS = 18;
	private static final int MASK = (1<<BITS)-1;//-1L^(-1L<<BITS)
	
	private static final int bits = 13;
	private static final int mask = (1<<bits)-1;
	private static final int work = Idsn.appCode4()&mask;
 	private static long tims = 0;//Tims.get();
	private static int loop = 0;	
 
	
    /**
     * <br/>返回18位长度的序列号，在每个应用实例的保证唯一性，
     * <br/>日期(14bit)+时间(17bit)+序列号6位(19bit)+应用实例4位(13bit)
     * <br/>单应用（运行实例数最大8000）支持每秒26万的序列号全局30年不重复；
     * <br/>序列生成时间大约0.2us；
     * <br/>bits usage rate about 48%: 12/16*31/32*24/32*60/64*60/64
     * @return next sequence for global 
     */
    public synchronized final static long next(){      	
     	long inst = Tims.get();//31位到秒；
    	if( tims > inst ){
    		logger.error("system clock error, old[{}] is after now[{}] .",tims,inst);
    		throw new IllegalArgumentException("system clock error !");
    	}
      	loop = (loop+1) & MASK;
    	if( tims < inst ){
    		//logger.debug("tims:{}->{},loop:{}->0.",tims,inst,loop);
     		loop = 0;
    	}else if( loop==0 ){//wait to next time
     		logger.debug("cyclic too fast, wait to next second; now:[{}].",tims);
    		while(inst<=tims)try{Thread.sleep(5);inst=Tims.get();}catch(Exception e){}
    		
      	}
    	tims=inst;
    	return tims<<32 | loop<<bits | work ;
    }

    /**
     * @see #next()
     * @return Long.toString
     */
    public static String text(){
    	return Long.toString(next());
    }
    /**
     * generate new seq using {@link #next()}
     * @return  int[9]{y,yyyy,M,d,h,m,s,seq,loc} 
     */
    public static int[] meta(){    	
    	return meta(next());
    }

    /**
     * @param lsn long generated by {@link Seq#next()} or {@link Seq#text()};
     * @return int array for seq fields
     */
    public static int[] meta(long lsn){
    	int[] flds = Tims.meta((int)(lsn>>>32));    	
			  flds = Arrays.copyOf(flds, 9);
 			  flds[7]= ((int)lsn)>>>bits; // cyclic Seq;
    		  flds[8]= mask&(int)(lsn); //worker;
    	return flds;
    }  
    
    /**
     * @param lsn long generated by {@link Seq#next()} or {@link Seq#text()};
     * @return yyyyMMdd
     */
    public static String ymd(long lsn){
    	int[] flds = Tims.meta((int)(lsn>>>32));    	
    	return String.format("%1$d%2$02d%3$02d", flds[1],flds[2],flds[3]);
    }  
    /**
     * @param lsn long generated by {@link Seq#next()} or {@link Seq#text()};
     * @return hhmmss
     */
    public static String hms(long lsn){
    	int[] flds = Tims.meta((int)(lsn>>>32));    	
    	return String.format("%1$02d%2$02d%3$02d", flds[4],flds[5],flds[6]);
    }  
    
	private static final String SF18="%1$02d%2$02d%3$02d%4$02d%5$02d%6$02d%7$06d";
	private static final String SF24="%1$04d%2$02d%3$02d%4$02d%5$02d%6$02d%7$06d%8$04d";
	
    /**
     * seq with 18 readable numbers, unique in same jvm,not for cluster.
     * @return  yyMMddHHmmss+seq6 (170102123456123456)
     */
    public static String num18() {
       	int[] f= meta();
    	return String.format(SF18,f[0],f[2],f[3],f[4],f[5],f[6],f[7]);
    }
    
    /**
     * seq with 24 readable numbers, unique for cluster.
     * @return  yyyyMMddHHmmss+seq6+instance4 (170102123456123456)
     */
    public static String num24() {
    	int[] f= Seq.meta();
    	return String.format(SF24,f[1],f[2],f[3],f[4],f[5],f[6],f[7],f[8]);    	
    }    

}
