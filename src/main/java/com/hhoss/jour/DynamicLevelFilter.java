package com.hhoss.jour;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

import com.hhoss.lang.Judge;

/**
 * This filter allows for efficient course grained filtering based on logger name level
 * 
 * @author Kejun
 * @See ch.qos.logback.classic.turbo.DynamicThresholdFilter;
 */
public class DynamicLevelFilter extends TurboFilter {
    private static final Map<String,Object[]> filters = new HashMap<>();//todo timer clean cache 
    
    /**
     * 
     * @param marker
     * @param logger
     * @param level
     * @param s
     * @param objects
     * @param throwable
     * 
     * @return FilterReply - this filter's decision
     */
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String s, Object[] objects, Throwable throwable) {
        FilterReply reply = FilterReply.NEUTRAL;
        String pname = logger.getName();
        while(true){
 	        Object[] opts = getOption(pname);
	        if ( opts!=null && opts[0]!=null ) {
		        int filterLevel= ((Level)opts[0]).toInt();
		        if (level.toInt()<filterLevel) {
		        	reply = (FilterReply)opts[1];
		        }else if (level.toInt()==filterLevel){
		        	reply = (FilterReply)opts[2];
		        } else {
		        	reply = (FilterReply)opts[3];
		        }
		        break;
	        }
	       	int idx = pname.lastIndexOf('.');
        	if( idx<1 ){ break; }//ignore ROOT
	        pname=pname.substring(0,idx);
        }
        return reply;
    }
    
    public static void clear(){
    	filters.clear();
    }
    
    private Object[] getOption(String name){
    	Object[] opts = filters.get(name);
    	if( opts==null ){
	    	String opt = LoggerConfig.getProperty("spi.logger.filter|"+name);
	    	if(opt==null||opt.length()<4){return opts;}
	    	String[] arrs = opt.split("[_,;]");
	    	
	    	opts = new Object[4];
	    	opts[0]= Level.toLevel(arrs[0].trim(),null);
	    	opts[1]= arrs.length>1 ? getReply(arrs[1].trim(),FilterReply.DENY) : FilterReply.DENY ;   //onLower
	    	opts[2]= arrs.length>2 ? getReply(arrs[2].trim(),FilterReply.NEUTRAL) : FilterReply.NEUTRAL ;//onEqual
	    	opts[3]= arrs.length>3 ? getReply(arrs[3].trim(),FilterReply.ACCEPT) : FilterReply.ACCEPT ; //onHigher
	    	filters.put(name,opts);
    	}
    	return opts;
    }  
    
    private FilterReply getReply(String s, FilterReply def ){
    	if(Judge.isTrue(s)){
    		return FilterReply.ACCEPT;
    	}else if("0".equals(s)||"NEUTRAL".equalsIgnoreCase(s)){
    		return FilterReply.NEUTRAL;
    	}else if(Judge.isFail(s)){
    		return FilterReply.DENY;
    	}
    	return def;    	
    }
    
 }
