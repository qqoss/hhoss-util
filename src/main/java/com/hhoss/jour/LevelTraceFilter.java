package com.hhoss.jour;

import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import com.hhoss.lang.Judge;
import com.hhoss.util.thread.ThreadKeys;

/**
 * This filter allows for efficient course grained filtering based on appenders
 * 
 * @author Kejun
 * @See ch.qos.logback.classic.filter.ThresholdFilter;
 */
public class LevelTraceFilter extends Filter<ILoggingEvent> {
    private String key=ThreadKeys.LOG_TERM;
    private String def="info_0"; // default setting; eg. "info,deny,neutral,accept";
    
    private Level level;
    private FilterReply lower=FilterReply.DENY;
    private FilterReply equal=FilterReply.NEUTRAL;
    private FilterReply upper=FilterReply.ACCEPT;
 
    /**
     * set the MDC key whose value will be used as a level threshold
     */
    public void setKey(String key) {
        this.key = key;
    }
    public void setDef(String def) {
        this.def = def;
    }

    @Override
    public void start() {
        if (this.key == null || this.def == null) {
            addError("key or def should be specified");
            return;
        }	
        String[] arrs = def.split("[_,;]"); 
        if(arrs.length>0){ level = Level.toLevel(arrs[0].trim(),null); }  
     	if(level==null){
     		addError("level should be specified");
     		return; 
     	} 
    	if(arrs.length>1){ lower = getReply(arrs[1].trim(),lower); }   
    	if(arrs.length>2){ equal = getReply(arrs[2].trim(),equal); } 
    	if(arrs.length>3){ upper = getReply(arrs[3].trim(),upper); } 
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
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()){ return FilterReply.DENY; }  
    	Level evtl = event.getLevel();
    	FilterReply reply = evtl.toInt()==level.toInt()?equal:evtl.toInt()<level.toInt()?lower:upper;
        
        String param = MDC.get(key);
        if( param==null ){ return reply; }  
        String[] arrs = param.split("[_,;]"); 
        if(arrs.length<1){ return reply; }   
        Level mdcl = Level.toLevel(arrs[0].trim(),null);
    	if(mdcl==null){ return reply; } 
        if (evtl.toInt()<mdcl.toInt()) {
        	reply = arrs.length>1 ? getReply(arrs[1].trim(),lower) : lower ; 
        }else if (evtl.toInt()==mdcl.toInt()){
        	reply = arrs.length>2 ? getReply(arrs[2].trim(),equal) : equal ;
        } else {
        	reply = arrs.length>3 ? getReply(arrs[3].trim(),upper) : upper ;
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
