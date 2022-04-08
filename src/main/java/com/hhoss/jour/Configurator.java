package com.hhoss.jour;

import java.net.URL;
import java.util.Properties;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.RuleStore;

import com.hhoss.boot.App;
import com.hhoss.conf.ResHolder;


public class Configurator extends JoranConfigurator{
	private static final String SLF4J = "res/app/logger/logback.xml";
	private static final Logger logger = Logger.get();
	private Properties props;

	Configurator(Context ctx){
		setContext(ctx);
	}
	
	Configurator(Context ctx,Properties props){
		setContext(ctx);
		this.props=props;
	}
	
	Configurator doConfigure() throws JoranException{
		String res = getProps().getProperty("res.log.slf4j",SLF4J);
		URL url = App.getResource(res);
		if( url==null ){
			String msg = res+" is not exist, please check."; 
			logger.warn(msg);addError(msg);
			return null;
		}
		mergeContext();
		doConfigure(url);
		return this;
	}
	
	/**
	 * the properties actived order: passed properties -> res.app.logger.log -> res.app.logger.logback -> res.app.root
	 * @return
	 */
	private Properties getProps(){
		if(props!=null) return props;		
		Properties	conf= App.getProperties("res.app.logger.log");
		if( conf==null ){
			conf= App.getProperties("res.app.logger.logback");
		}
		if( conf==null ){
			String msg ="missing log properties, will use system properties."; 
			logger.warn(msg);addWarn(msg);
			conf=new ResHolder("res.app.logger.missing");
		}
		
		props = new Properties();
		for(String key : conf.stringPropertyNames()){
			String val = conf.getProperty(key);
			if(val!=null)props.put(key, val);
		}
		return props;
	}
	
	void setProperty(String key, String val){	
		getProps().setProperty(key, val);
	}
	String getProperty(String key){	
		String  v = getContext().getProperty(key);
		return (v==null)?getProps().getProperty(key):v;
	}
	
	private void mergeContext(){
		LoggerContext ctx = (LoggerContext)getContext();
		ctx.reset(); DynamicLevelFilter.clear();
		for(String key:props.stringPropertyNames()){
			String val = props.getProperty(key);
			ctx.putProperty(key,val); //scope is local
			addInfo(key+" = "+val);
		}
	}
	
    @Override 
    public void addInstanceRules(RuleStore rs) {
	    super.addInstanceRules(rs);
	    /*
		//mergePropeties(App.getProperties(App.getConfigRoot()));
	    rs.addRule( new ElementSelector("configuration"), new ConfigurationAction(){
	    	 @Override public void begin(InterpretationContext ic, String name, Attributes attrs) {
	    			mergePropeties(App.getProperties(App.getConfigRoot()));
			 }
	    	 @Override public void  end(InterpretationContext ec, String name) {}
	    });
	    */
	}
    
    @Override 
    public String toString() {
        return getClass().getSimpleName();
    }

}
