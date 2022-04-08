package com.hhoss.jour;

import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

import com.hhoss.boot.App;
import com.hhoss.ksid.Tims;

/**
 * This filter allows for efficient course grained filtering based on logger name level
 * 
 * @author Kejun
 * @See ch.qos.logback.classic.turbo.DynamicThresholdFilter;
 */
public class DynamicStateFilter extends TurboFilter {
	private int last = Tims.get();
	private boolean initial = false;

    public boolean prepare() { 
    	if(!isStarted()){return false;}
 
    	int t = Tims.get();
    	if(!initial && t-last>8 || t-last>8<<8){
    		last = t; 
    		initial=App.isReady();
    	}
    	return initial ;
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
        if (!prepare()){ return reply; }
        return FilterReply.NEUTRAL;
    }
    
 }
