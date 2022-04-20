package com.hhoss.conf;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import com.hhoss.boot.App;
import com.hhoss.jour.Logger;

/**
 * for treeable config, keep all the loaded bundle/holder/bucket
 * @author kejun
 *
 */
public final class ResKeeper {
	private static final Logger logger = Logger.get();
	private static final String FOLDER_DELIMS = "[;\t\n\r"+File.pathSeparator+"]";
	private static final String RES_LOOKUP_PATH = "res.lookup.path";
	private static final String IMPORT = "<import>";//preload properties
	
	private static final Properties NOT_FOUND = new ResHolder("NOT_FOUND");//new Properties();	
	private final ResLoader loader = new ResLoader();//ResKeeper.class.getClassLoader() ;
	private String initBundle;

	protected ResKeeper(){};
	protected ClassLoader getLoader() {
		return loader;
	}

	// every tree has a bundleMap, needn't set to static;
	private final Map<String, Properties> bundleMap = new Hashtable<String, Properties>();

	public void init(String bundle, Properties props) {
		//define the default lookup paths.
		loader.setLookups(new String[]{props.getProperty(App.APP_RUNTIME_CONF),props.getProperty(App.APP_DEFAULT_CONF)});		
		logger.debug("Properties[{}] initializing...",bundle);
		bundleMap.put(bundle, props);//using props when loading bundle;		
		Properties loaded = reloadProperties(bundle, props);
		if(loaded==null||loaded==NOT_FOUND){
		    //not exists config named bundle, then use given props;
		    bundleMap.put(bundle, props);
			logger.debug("Properties[{}] set by param.",bundle);
		}else{
		    loadSubProperties(loaded);
			logger.debug("Properties[{}] initialized.",bundle);
		}
		this.initBundle=bundle;
		//if the new properties defined the lookup paths, then reset it.
		String resPath = getProperty(bundle, RES_LOOKUP_PATH);
		
		if( resPath!=null && resPath.length()>1){			
			loader.setLookups(resPath.split(FOLDER_DELIMS));
		}
	}

	/*
	public void init(String bundle) {
		initProperties(bundle, true);
	}
	///**
	 * @param bundle
	 *            should be baseName, not include _zh_cn_...;
	 * @param recurse
	 *            decide whether we need to process the new props.
	 * @return new props, if not cached, otherwise, return cached props, needn't
	 *         reload;
	private void initProperties(String bundle, boolean recurse) {
		Properties props = loadProperties(bundle, null);
		if (recurse && props != null && props != NOT_FOUND) {
			loadSubProperties(props);
		}
	}
	*/

	/**
	 * Get a property value from the properties files.
	 * 
	 * @param String
	 *            propertiesFile the identifier of the properties file as
	 *            defined in PropertiesMap.properties
	 * @param String
	 *            propertiesAttribute the name of the property to be retrieved.
	 * @return String the property value.
	 * @exception NullPointerException
	 *                thrown if the properties file is not valid.
	 */
	public String getProperty(String bundleKey, String propertyKey) {
		if(warn(bundleKey)||warn(propertyKey)){return null;}	
		Properties props=getProperties(bundleKey);
		return (props==null)?null:props.getProperty(propertyKey.trim());
	}

	/**
	 * Get a resource bundle.
	 * 
	 * @param String
	 *            like: "<import>learn.message.zh_cn" or "learn.message.zh_cn" or
	 *            "conf.learn.message_zh_cn" propertiesFile the identifier of
	 *            the properties file as defined in PropertiesMap.properties
	 * @return ResourceBundle the property resource bundle. Null if not found.
	 * 
	 * if you want to get like "<import>p1.p2...", can ignore "<import>", and use
	 * "p1.p2..." but if you have "p1.p2..." and "<import>p1.p2...", they refer
	 * diff bundle, use qualified name will be better.
	 */
	public Properties getProperties(String bundleKey) {
		if(warn(bundleKey)){return null;}			
		Properties props = bundleMap.get(bundleKey);
		if (props == null&& !bundleKey.startsWith(IMPORT)) {
			props = bundleMap.get(IMPORT + bundleKey);
		}		
		if (props == null) {
			props = loadProperties(bundleKey);
		}		
		if (props == null){
			bundleMap.put(bundleKey,props=NOT_FOUND);
			// bundleMap.put(IMPORT + bundleKey, props);
		}
		if(props == NOT_FOUND) {
			logger.info("NOT FOUND properties[{}]." , bundleKey);
			props = null;// don't throw exception;
			// throw new NullPointerException( "Can't get peroperties for bundleKey:"+bundleKey );
		}
		return props;
	}
	
	private Properties getProperties0(String bundleKey) {
		if(warn(bundleKey)){return null;}			
		Properties props = bundleMap.get(bundleKey);
		if (props == null) {
			if (bundleKey.startsWith(IMPORT)) {
				props = NOT_FOUND;
			} else {
				props = bundleMap.get(IMPORT + bundleKey);
				if (props == null) {
					props = loadProperties(bundleKey);
					if (props == null)
						props = NOT_FOUND;
					bundleMap.put(bundleKey, props);
					// bundleMap.put(IMPORT + bundleKey, props);
				}
			}
		}
		if (props == NOT_FOUND) {
			logger.info("NOT FOUND properties[{}]." , bundleKey);
			props = null;// don't throw exception;
			// throw new NullPointerException( "Can't get peroperties for bundleKey:"+bundleKey );
		}
		return props;
	}
	
	/**
	 * Get a resource bundle, if not exists, use giving properties.
	 * 
	 * @param String
	 *            like: "<import>learn.message.zh_cn" or "learn.message.zh_cn" or
	 *            "conf.learn.message_zh_cn" propertiesFile the identifier of
	 *            the properties file as defined in PropertiesMap.properties
	 * @param defProps use defProps instead and put to cache if not found for bundleKey, then      
	 * @return ResourceBundle the property resource bundle. Null if not found.
	 * 
	 * if you want to get like "<import>p1.p2...", can ignore "<import>", and use
	 * "p1.p2..." but if you have "p1.p2..." and "<import>p1.p2...", they refer
	 * diff bundle, use qualified name will be better.
	 */
	public Properties getProperties(String bundleKey, Properties defProps) {
		if(warn(bundleKey)){return null;}		
		Properties props = bundleMap.get(bundleKey);
		if( props == null && !bundleKey.startsWith(IMPORT) ) {
			props = bundleMap.get(IMPORT + bundleKey);
		}
		if( props == null ) {
			props = loadProperties(bundleKey);
		}		
		if( props==null||props==NOT_FOUND){
			if( defProps==null ){
				bundleMap.put(bundleKey, NOT_FOUND);
				logger.info("NOT FOUND properties[{}]" , bundleKey);
				// throw new NullPointerException( "Can't get peroperties for bundleKey:"+bundleKey );
			}else{
				if((defProps instanceof ResHolder)&&(bundleKey.equals(((ResHolder)defProps).getName()))){
					props = defProps; //reuse same ResHolder;
				}else{
					props = new ResHolder(bundleKey,defProps); 
				}
				bundleMap.put(bundleKey, props);
				logger.info("Defined Properties[{}]." , bundleKey);
			}
		}	
		return props==NOT_FOUND?null:props;		
	}

	/**
	 * @param bundleKey
	 * @param asChild -
	 *            if true then regard the bundle as default, can't be modify,
	 *            all the modify in child properties.
	 * @return
	 */
	public Properties getProperties(String bundleKey, boolean asChild) {
		Properties props = getProperties(bundleKey);
		if (asChild) {
			props = new ResHolder(bundleKey+"/child",props);
		}
		return props;
	}

	private Properties reloadProperties(String bundle, Properties parentProps) {
		try {
			ResHolder newProps = new ResHolder(bundle);
			newProps.loadBundle(loader,Locale.ROOT);
			logger.debug("loaded [{}]",bundle);
			bundleMap.put(bundle, newProps);
			// recurse load parent properties as defaults;
			if (parentProps == null) {
				String parentName = (String) newProps.get(ResHolder.PARENT_NAME);//should allow from cacher or dbms, consider priority
				Properties def = loadProperties(parentName, null);
				newProps.setFather((def == NOT_FOUND) ? null : def);
			} else {
				newProps.setFather(parentProps);
			}
		} catch (MissingResourceException mre) {
			bundleMap.put(bundle, NOT_FOUND);
			logger.debug("Loading {} : NOT FOUND. {}",bundle,mre.getMessage());
		}
		return bundleMap.get(bundle);
	}

	/**
	 * @param bundle
	 *            should be baseName, not include _zh_cn_...;
	 * @param parentProps
	 *            as new props's defaults properties.
	 * @return new props, if not cached, otherwise, return cached props, needn't
	 *         reload;
	 */
	private Properties loadProperties(String bundle, Properties parentProps) {
		// from AppLoader, bundle should be baseName, not include
		// _zh_cn_...;
		if (bundle == null)
			return null;
		Properties props = bundleMap.get(bundle);
		if (props == null) {
			props = reloadProperties(bundle, parentProps);
		}
		return props;
		// (props==NOT_FOUND) ? null : props;
		// props can be NOT_FOUND, handle by invoker.
	}

	private Properties loadProperties(String bundle) {
		return loadProperties(bundle, null);
	}
	
	private void loadSubProperties(Properties props) {
		if (props == null || props == NOT_FOUND) {
			logger.info("parentProps hasn't been loadeded or null.");
			return;
		}
		Enumeration<?> keyList = props.keys();
		String preKey = null;
		String bundle = null;
		while (keyList.hasMoreElements()) {
			preKey = (String) keyList.nextElement();
			if (preKey.startsWith(IMPORT)) {
				// ResourceBundle bundle = ResourceBundle.getBundle(
				// properties.getString( keyName ) );
				bundle = (String) props.get(preKey);
				Properties newProps = loadProperties(bundle, props);
				// means sub props can't re define the two prop
				// same properties can refer by several properties
				// props.put(CONF_PARENT_NAME, parentProps.get(CONF_SELF_NAME));
				// props.put(CONF_PARENT_PATH, parentProps.get(CONF_SELF_PATH));
				bundleMap.put(preKey, newProps);
				logger.info("Linking {} TO bundle {} ", preKey,bundle);// binding,mapping
				loadSubProperties(newProps);
			}
		}
	}
	
	/**
	 * NOT SAFE, if same key in different bundle properties , which can't decide which will return, because the load order is unknown;
	 * @param key : property key
	 * @return value for the key.
	 * TODO sorted the bundleMap;
	 */
	protected String guessProperty(String key){
		String val = null;
		if( initBundle!=null ){
			val=bundleMap.get(initBundle).getProperty(key);
		}
		if(val==null)
		for(Properties p:bundleMap.values()){
			if((val=p.getProperty(key))!=null){
				break;
			}
		}
		return val;
	}
	
	private boolean warn(String key){
		if(key==null||key.trim().length()==0){
			logger.warn("Properties bundleKey is blank!");
			return true;
		}
		return false;
	}

}
