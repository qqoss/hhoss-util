package com.hhoss.conf;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import com.hhoss.boot.App;
import com.hhoss.jour.Logger;
import com.hhoss.lang.Judge;


/**
 * AppContext for static info and app init info
 * @author Kejun
 *
 */
/**
 * @author kejun
 *
 */
public abstract class EnvHolder {
	
	protected static final String SYS_MACHINE_NAME = "sys.machine.name";//machine name
	protected static final String SYS_PROCESS_CODE = "sys.process.code";//process pid

	protected static final String APP_PROJECT_NAME = "app.project.name";//for web-app display name; project,segment,section,module name
	protected static final String APP_STARTUP_CODE = "app.startup.code";//for web-app, it is different even in cluster
	protected static final String APP_CLUSTER_SITE = "app.cluster.site";//different data center, city, area, cluster,group
	
	protected static final String APP_CONTEXT_ROOT = "app.context.root";// it is the root absolute path, for web it is war/
	protected static final String APP_CLASSES_PATH = "app.classes.path";// old "env.path.classes"; APP_DEFAULT_CONF/classes, for web-app, it is /WEB-INF/classes
	protected static final String APP_TEMPARY_PATH = "app.tempary.path";// temporay path, set from env, default is java.io.tmpdir
	protected static final String APP_DEFAULT_CONF = "app.default.conf";// old "env.path.appconf" for web-app, it is  war/WEB-INF
	protected static final String APP_RUNTIME_CONF = "app.runtime.conf";// set from env, default is APP_DEFAULT_CONF
	protected static final String APP_PREPARE_FLAG = "app.prepare.flag";// key for the status of container
	
	private static final EnvKeeper envKeeper = new EnvKeeper();
	private static int status = 0;//1:initializing,2:initialized.
/*
	private String rootPath;
	private String confPath;
	private String clasPath;
*/
	private String resFolder=null;

	/**
	 * only used when initial
	 * @return res folder, the given res folder path when initial
	 */
	protected String getResFolder(){
		checkInitial();
		return resFolder;
	}
	private void setResFolder(String folder){
		this.resFolder=folder;
	}
	
	public static void setRootEnv(String key,String val){
		if(key==null){return;}
		envKeeper.setRootEnv(key, val);
		if(key.startsWith("sys.")&&key.length()>4){
			System.setProperty(key, val);
		}
		if(key.startsWith("SYS_")&&key.length()>4){
			System.setProperty(key.substring(4), val);
		}
	}
	
	/**
	 * @param key
	 * @return the root env set in startup 
	 */
	public static String getRootEnv(String key){
		return getKeeper().getRootEnv(key);
	}
		
	/**
	 * check env has initialized, if no, init by default 
	 */
	private static void checkInitial(){
		if( status>0 ) return;
		App.defaultInitial();
	}
	
	/**
	 * @param folder the path for res config folder
	 * @param bundle the root res bundle name
	 */
	protected final void initial(final String folder, final String bundle) {
		status++;
		setRootEnv(APP_PREPARE_FLAG,"0");
		setResFolder(normalize(folder));
		prepare();
		envKeeper.initial(bundle);
		status++;
		
		String site = getRootEnv(APP_CLUSTER_SITE);
		if( site==null ){
			site=envKeeper.guessProperty(APP_CLUSTER_SITE);
			setRootEnv(APP_CLUSTER_SITE, String.valueOf(site));
		}
	}	

	public static void destroy() {
		if( envKeeper!=null ){
			setRootEnv(APP_PREPARE_FLAG, "-1");
			envKeeper.destroy();
		}
	}	
	
	/**
	 * @return envKeeper the env keeper for current app;
	*/
	public static EnvKeeper getKeeper() {
		//checkInitial();
		return envKeeper;
	}
	
	public static void setStatus(int stat){
		setRootEnv(APP_PREPARE_FLAG, String.valueOf(stat));
	}
	
	public static boolean isReady(){
		return Judge.isTrue(getRootEnv(APP_PREPARE_FLAG));
	}
	
	/**
	 * do some prepare before real initial.<br />
	 */
	protected void prepare(){
		/**
		 * if a jvm/tomcat have multi webapp, it will be affected cause of web shared one System.getProperties,
		 * so if one jvm only one app, we can invoke this mehod manually after initialized,
		 * for logback...only affected in current jvm thread, so using static EnvKeeper .
		*/
		//System.getProperties().putAll(envKeeper.getRootProps());
	}

	//TODO: if from jar, should have  "!/"
	//@see org.springframework.util.ResourceUtils.JAR_URL_SEPARATOR="!/"
	protected static String normalize(String rootPath) {
		String path = normalize(rootPath,getClassLocation(EnvHolder.class));
		if (path.endsWith("classes")) { //classes or test-classes
			int idx = path.lastIndexOf('/');
			if(idx<0){idx= path.indexOf('\\');}
			path = path.substring(0, idx);
		} else if (path.endsWith("lib")) {
			path = path.substring(0, path.length() - 4);
		}
		// for web app, return web root;
		if (path.endsWith("WEB-INF")) {
			path = path.substring(0, path.length() - 8);
		}
		// for app, return web root;
		if (path.endsWith("META-INF")) {
			path = path.substring(0, path.length() - 9);
		}
		return path;
	}
	
	protected static String normalize(String original,String defaults){
		String path = original==null?defaults:original;
		if(path!=null){
			path=path.trim();
			if (path.endsWith("/") || path.endsWith("\\")) {
				path = path.substring(0, path.length() - 1);
			}
		}
		return path;
	}

	/**
	 * Kryo@20070914 get a class or jar root path
	 * 
	 * @param Class
	 * @return String - class root path; if loaded by webapp classloader, then
	 *         return webinf path; not endWith "/", like "/d:/p0/p1/p2";
	 */
	private static String getClassLocation(Class<?> clazz) {
		String path = clazz.getResource("/").getPath();
		int pos = path.indexOf(clazz.getPackage().getName().replace('.', '/'));// "/biz/zheng/log"
		if (pos > 0){
			path = path.substring(0, pos - 1);
		}
		return path;
	}

	/**
	 * NOT SAFE, if same key in diff bundle properties tree, which can't decide which will return, because the load order is unknown;
	 * @param key : property key
	 * @return value
	 */
	protected static String guessProperty(String key) {
		return getKeeper().guessProperty(key);
	}

	/**
	 * @param bundleKey :
	 *            properties bundle Name
	 * @param propertyKey :
	 *            the property key in the bundle;
	 * @return String: the property value, null if the key can't find in the
	 *         bundle;
	 */
	public static String getProperty(String bundleKey, String propertyKey) {
		return getKeeper().getProperty(bundleKey, propertyKey);
	}

	/**
	 * @param bundleKey :
	 *            properties bundle Name
	 * @return Properties: the cached properties, null if not been cached.;
	 */
	public static Properties getProperties(String bundleKey) {
		return getKeeper().getProperties(bundleKey);
	}
	
	/**
	 * Cast type as ResHolder, from {@link #getProperties(String)}
	 * @param bundleKey
	 * @return ResHolder
	 */
	public static ResHolder getRes(String bundleKey) {
		return (ResHolder)getProperties(bundleKey);
	}
	
	/**
	 * @param prefix
	 * @return properties which include the sub keys
	 */
	public static Properties subProperties(String bundleKey,String propsKey){
		if(propsKey==null||propsKey.length()<1){return null;}
		Properties ori= getProperties(bundleKey);
		Properties sub;
		if(ori instanceof ResHolder){
			sub = ((ResHolder)ori).subHolder(propsKey,false);
		}else{
			sub = new ResHolder(bundleKey+"-"+propsKey, ori);
			String head=propsKey.endsWith(".")?propsKey:propsKey+".";
			for(String name : ori.stringPropertyNames()){
				if(name.startsWith(head)&&name.length()>head.length()){
					sub.setProperty(name.substring(head.length()), ori.getProperty(name));
				}
			}
		}
		return sub;
	}
	
	/**
	 * @param bundleKey :  properties bundle Name
	 * @param defProps :  properties  as default if not found
	 * @return Properties: the cached properties, null if not been cached.;
	 */
	public static Properties getProperties(String bundleKey,Properties defProps) {
		return getKeeper().getProperties(bundleKey,defProps);
	}
	/**
     * Accepts decimal, hexadecimal, and octal numbers given by the following grammar:
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i>
     * <p>
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
	 * @param key of the prop
	 * @return Long
	 */
	public static Long getLong(String bundleKey, String propertyKey) {
		return ((ResHolder)getProperties(bundleKey)).getLong(propertyKey);
	}
	/**
     * Accepts decimal, hexadecimal, and octal numbers given by the following grammar:
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i>
     * <p>
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
	 * @param key of the prop
	 * @return Integer
	 */
	public static Integer getInteger(String bundleKey, String propertyKey) {
		return ((ResHolder)getProperties(bundleKey)).getInteger(propertyKey);
	}
	/**
	 * support radix, 0x...
     * <dl>
     * <dt><i>HexSignificand:</i>
     * <dd>{@code 0x} <i>HexDigits<sub>opt</sub>
     *     </i>{@code .}<i> HexDigits</i>
     * <dd>{@code 0X}<i> HexDigits<sub>opt</sub>
     *     </i>{@code .} <i>HexDigits</i>
     * </dl>
	 * @param key of the prop
	 * @return float
	 * @see Double#valueOf(String s)
	 */
	public static float getFloat(String bundleKey, String propertyKey) {
		return ((ResHolder)getProperties(bundleKey)).getFloat(propertyKey);
	}
	/**
	 * support radix, 0x...
     * <dl>
     * <dt><i>HexSignificand:</i>
     * <dd>{@code 0x} <i>HexDigits<sub>opt</sub>
     *     </i>{@code .}<i> HexDigits</i>
     * <dd>{@code 0X}<i> HexDigits<sub>opt</sub>
     *     </i>{@code .} <i>HexDigits</i>
     * </dl>
	 * @param key of the prop
	 * @return double
	 * @see Double#valueOf(String s)
	 */
	public double getDouble(String bundleKey, String propertyKey) {
		return ((ResHolder)getProperties(bundleKey)).getDouble(propertyKey);
	}
	
	/**
	 * @param key the key of property or prefix of property Array
	 * @return List list is not null and each item is not null, IllegalArgumentException when propertyKey is null
	 * the value as list by delimiters "var.list.split.delimiters" default as ",;| \t\n"
	 * or values which key matched prefix{[.|_]?\d+}
	 * the max matches entry is 1000 for prefix;
	 */
	public static List<String> getList(String bundleKey, String propertyKey) {
		return ((ResHolder)getProperties(bundleKey)).getList(propertyKey);
	}
	
	/**
	 * the value as list by delimiters "var.list.split.delimiters" default as ",;| \t\n" <br />
	 * or values which key matched prefix{[.|_]?\d+} <br />
	 * the max matches entry is 1000 for prefix; <br />
	 * value item only support 10 radix, not support 16 radix
	 * @param key the key of property or prefix of property Array
	 * @param klass the class of element type
	 * @return List list is not null and each item is not null, IllegalArgumentException when propertyKey is null
	 */
	public static <T> List<T> getList(String bundleKey, String propertyKey, Class<T> klass) {
		return ((ResHolder)getProperties(bundleKey)).getList(propertyKey, klass);
	}
	
	/**
	 * @param key
	 * @return the array of config, allow null/empty item, IllegalArgumentException when propertyKey is null
	 */
	public static String[] getArray(String bundleKey, String propertyKey) {
		return ((ResHolder)getProperties(bundleKey)).getArray(propertyKey);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] getArray(String bundleKey, String propertyKey, Class<T> klass) {
		return (T[]) getList(bundleKey, propertyKey, klass).toArray();
	}

	
	/**
	 * @param name  like "res/app/common/logback.xml"
	 * @return url the resource
	 */
	public static URL getResource(String name) {
		return getKeeper().getLoader().getResource(name);
	}

	/**
	 * @param name  like "res/app/common/logback.xml"
	 * @return url the resource
	 */
	public static InputStream getResourceAsStream(String name) {
		return getKeeper().getLoader().getResourceAsStream(name);
	}

	/**
	 * Return the absolute path of the webapp root folder in the current FS.
	 * 
	 * @return String the absolute path of the web root.
	 */
	public static String getRootPath() {
		return getRootEnv(App.APP_CONTEXT_ROOT);
	}

	/**
	 * 
	 * @return String the resource config path.
	 */
	public static String getConfPath() {
		return getRootEnv(App.APP_RUNTIME_CONF);
	}

	/**
	 * Return the absolute path of the classes folder in the current FS.
	 * 
	 * @return String the absolute path of the classes.
	 */
	public static String getClasPath() {
		return getRootEnv(App.APP_CLASSES_PATH);
	}

	/**
	 * @return String; the bundle name of the root config file used to init;
	 */
	public static String getConfigRoot() {
		return getKeeper().getConfigRoot();
	}

	public static void main(String[] args) {
		Logger.get().info(getClassLocation(EnvKeeper.class));
	}	

}
