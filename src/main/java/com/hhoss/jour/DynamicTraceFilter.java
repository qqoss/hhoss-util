package com.hhoss.jour;

import org.slf4j.MDC;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

import com.hhoss.lang.Judge;
import com.hhoss.util.thread.ThreadKeys;

/**
 * This filter allows for efficient course grained filtering based on logger name level
 * 
 * @author Kejun
 * @See ch.qos.logback.classic.turbo.DynamicThresholdFilter;
 */
public class DynamicTraceFilter extends TurboFilter {
    private String key=ThreadKeys.LOG_TERM;
    /**
     * Get the MDC key whose value will be used as a level threshold
     * @return the name of the MDC key.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @see setKey
     */
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void start() {
        if (this.key == null) {
            addError("No key name was specified");
        }
        super.start();
    }
    
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
        String param = MDC.get(key);
        if( param==null || !isStarted() ){ return reply; }  
        
        String[] arrs = param.split("[_,;]"); 
        if(arrs.length<1){ return reply; }  
        
    	Level lev = Level.toLevel(arrs[0].trim(),null);
    	if(lev==null){ return reply; } 
    	
        if (level.toInt()<lev.toInt()) {
        	reply = arrs.length>1 ? getReply(arrs[1].trim(),FilterReply.DENY) : FilterReply.DENY ; 
        }else if (level.toInt()==lev.toInt()){
        	reply = arrs.length>2 ? getReply(arrs[2].trim(),FilterReply.NEUTRAL) : FilterReply.NEUTRAL ;
        } else {
        	reply = arrs.length>3 ? getReply(arrs[3].trim(),FilterReply.ACCEPT) : FilterReply.ACCEPT ;
        }
        return reply;
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
