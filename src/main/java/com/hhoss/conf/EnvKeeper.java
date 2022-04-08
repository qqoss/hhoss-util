package com.hhoss.conf;

import java.util.Properties;

import com.hhoss.jour.Logger;


/**
 * app env instance for static info and app init info
 * @author Kejun
 *
 */
public final class EnvKeeper {
	private static final Logger logger = Logger.get();
	private final ResKeeper resKeeper = new ResKeeper();
	private final ResHolder resRooter = new ResHolder("res.root");
	private String configRoot = "res.app.root";//default;
	private boolean initialized = false;

	EnvKeeper(){}
	
	/**
	 * @param resRootName the root config properties , default is res.app.root
	 */
	void initial(String resRootName){
		if(initialized&&configRoot.equals(resRootName)){
			logger.info("already initialized by configRoot[{}], ignore reInitialize.",resRootName);
			return;
		}
		
		if(resRootName!=null&&resRootName.trim().length()>0){
			this.configRoot=resRootName.trim();
		}//else configRoot using default value.

		resKeeper.init(configRoot, resRooter);
		initialized = true;
		logger.info("EnvKeeper initialized.");
		defineEnv();
	}
	
	private void defineEnv(){
		//if the properties defined system params, then using them.
		Properties props=getProperties(configRoot);
		if(props==null){return;}
		if(props instanceof ResHolder){
			Properties sys = ((ResHolder)props).subHolder("sys.define.",true);
			for(String name : sys.stringPropertyNames()){
				String val = sys.getProperty(name);
				System.setProperty(name,val);
				logger.info("system defined: {} = {}",name,val);
			}
		}
	}

	void destroy() {
		if (initialized) {
			logger.info("EnvKeeper has been destroyed succeed!");
			initialized = false;
		} else {
			logger.info("EnvKeeper has been destroyed, ignore destroy again.");
		}
	}
	
	
	ClassLoader getLoader(){
		return resKeeper.getLoader();		
	}

	/**
	 * @return String; the bundle name of the root config file used to init;
	 */
	String getConfigRoot() {
		return configRoot;
	}
	
	String getRootEnv(String key){
		if(key==null){return null;}
		return (String)resRooter.get(key);
	}
	
	void setRootEnv(String key, String val){
		if(key==null){return;}
		resRooter.put(key, val);	
		logger.info("set root props: {} = {}",key,val);
	}
	
	/**
	 * NOT SAFE, if same key in diff bundle properties tree, which can't decide which will return, because the load order is unknown;
	 * @param key : property key
	 * @param rootFirst : try from root properties 
	 * @return value
	 */
	String guessProperty(String key) {
		return resKeeper.guessProperty(key);
	}

	/**
	 * @param bundleKey :
	 *            properties bundle Name
	 * @param propertyKey :
	 *            the property key in the bundle;
	 * @return String: the property value, null if the key can't find in the
	 *         bundle;
	 */
	String getProperty(String bundleKey, String propertyKey) {
		return resKeeper.getProperty(bundleKey, propertyKey);
	}
	
	/**
	 * @param bundleKey :
	 *            properties bundle Name
	 * @return Properties: the cached properties, null if not been cached.;
	 */
	Properties getProperties(String bundleKey) {
		return resKeeper.getProperties(bundleKey);
	}

	/**
	 * @param bundleKey :  properties bundle Name
	 * @param defProps :  properties  as default if not found
	 * @return Properties: the cached properties, null if not been cached.;
	 */
	Properties getProperties(String bundleKey,Properties defProps) {
		return resKeeper.getProperties(bundleKey,defProps);
	}

}
