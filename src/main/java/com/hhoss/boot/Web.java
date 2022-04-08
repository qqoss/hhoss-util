package com.hhoss.boot;

import java.util.Properties;

import com.hhoss.util.thread.ThreadHold;
import com.hhoss.util.thread.ThreadKeys;




/**
 * AppContext for static info and app init info
 * @author Kejun
 *
 */
public class Web extends App{
	protected static final String WEB_REALITY_PATH = "web.reality.path";//web reality full path
	protected static final String WEB_CONTEXT_PATH = "web.context.path";//web context path
	protected static final String WEB_CONTEXT_NAME = "web.context.name";//the runtime name
	private static final String WEB_SERVICE_HOST = "web.service.host";//web http host/domain
	private static final String WEB_SERVICE_PORT = "web.service.port";//web http port
	private static final String WEB_SERVICE_CODE = "web.service.code";//the runtime unique code
	private static final String WEB_RESROOT_CONF = "resAppRoot";	
	private static final Properties props = new Properties();

	public void initial(Properties params) {
		if(params!=null){props.putAll(params);}
		initial(getInitParam(WEB_REALITY_PATH), getInitParam(WEB_RESROOT_CONF));
	}	
	
	@Override
	protected void prepare(){	
		setRootEnv(WEB_CONTEXT_PATH, getInitParam(WEB_CONTEXT_PATH));
		setRootEnv(WEB_CONTEXT_NAME, genContextName());
		if( getProjectName()==null ){
			setRootEnv(APP_PROJECT_NAME, getContextName());
		}
		super.prepare();
	}	
	
	private static String genContextName(){
		String name=getInitParam(WEB_CONTEXT_NAME);
		if( name==null||name.trim().length()<1){
			name=getContextPath();//if not set display-name in web.xml
		}
		if( name!=null&&name.trim().length()>0){
			name=name.replace('/', '_');
		}
		return name;
	}
	
	/**
	 * @return name of web if defined, otherwise return display name in web.xml
	 */
	public static String getContextName(){
		String name = getRootEnv(WEB_CONTEXT_NAME);
		return name==null?getProjectName():name;
	}

	/**
	 * @return path of web context, eg: /manager
	 */
	public static String getContextPath(){
		return getRootEnv(WEB_CONTEXT_PATH);
	}
	
	/**
	 * @return haven't set, 
	 */
	public static String getServiceHost(){
		return getRootEnv(WEB_SERVICE_HOST);
	}	
	
	/**
	 * @return haven't set, 
	 */
	public static String getServicePort(){
		return getRootEnv(WEB_SERVICE_PORT);
	}	
	
	/**
	 * @return haven't set, 
	 */
	public static String getServiceCode(){
		String code = getRootEnv(WEB_SERVICE_CODE);
		return code==null?getStartupCode():code;
	}
	
	private static String getInitParam(String name){
		return props.getProperty(name);
	}
	
	public static String getProperty(String key) {
		String s = props.getProperty(key);
		return s==null?getRootEnv(key):s;
	}
	//TODO to be invoked
	protected void setServiceHost(String host){
		setRootEnv(WEB_SERVICE_HOST, host);
	}
	//TODO to be invoked
	protected static void setServicePort(String port){
		setRootEnv(WEB_SERVICE_PORT, port);
	}
	//TODO to be invoked
	protected static void setServiceCode(String code){
		setRootEnv(WEB_SERVICE_CODE, code);
	}

	
	/**
	 * @param lnk
	 * @param def
	 * @return val lnk+"req.rkey="+${req.rkey}
	 */
	public static String referKey4URL(char lnk){
		return lnk+ThreadKeys.REQ_REFER+'='+ThreadHold.peek(ThreadKeys.REQ_REFER,"");
	}	

}
