package com.hhoss.boot;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.hhoss.conf.EnvHolder;
import com.hhoss.jour.Logger;
import com.hhoss.lang.Classes;
import com.hhoss.util.Files;
import com.hhoss.util.token.TokenProvider;

/**
 * AppContext for static info and app init info
 * @author Kejun
 */
public class App extends EnvHolder {
	private static final Logger logger = Logger.get();
	
	/**
	 * default initialization, for single boot or test.
	 * folder will be ear|war|jars' folder, and using res.root as root 
	 */
	public final static void initial() {
	    Class<?> clazz = Classes.caller();
		logger.info("initializing from {}",clazz);
		new App().initial(Files.findBoot(clazz),null);
	}

	@Override
	protected void prepare(){
		setRootEnv(SYS_PROCESS_CODE, getPID());
		setRootEnv(SYS_MACHINE_NAME, getLocalHostName());
		//setRootEnv(SYS_TEMPARY_PATH, getEnvRoot());//TODO if set by config
		
		//if run in jar, how about the path? 
		String resFolder = getResFolder();	
		setRootEnv(APP_CONTEXT_ROOT, resFolder);
		
		File defFolder = new File(resFolder, "WEB-INF");
		String defPath = defFolder.isDirectory()&&defFolder.exists()?defFolder.getAbsolutePath():resFolder;		
		setRootEnv(APP_DEFAULT_CONF, defPath);
		
		File clsFolder = new File(defPath, "classes");
		String clsPath = clsFolder.isDirectory()&&clsFolder.exists()?clsFolder.getAbsolutePath():defPath;
		setRootEnv(APP_CLASSES_PATH, clsPath);
		
		TokenProvider sys = TokenProvider.from("system");		
		String runPath = sys.get(APP_RUNTIME_CONF, defPath);
		setRootEnv(APP_RUNTIME_CONF, runPath);
		
		String appCode = sys.get(APP_STARTUP_CODE,genStartupCode());
		setRootEnv(APP_STARTUP_CODE, appCode);
		
		String appName = sys.get(APP_PROJECT_NAME,getProjectName());
		setRootEnv(APP_PROJECT_NAME, appName==null?"module":appName);//TODO from maven name
		
		String appSite = sys.get(APP_CLUSTER_SITE);
		if(appSite!=null ){ setRootEnv(APP_CLUSTER_SITE, appSite); }
		
		String temPath = sys.get(APP_TEMPARY_PATH,Files.findTemp());
		setRootEnv(APP_TEMPARY_PATH, temPath==null?runPath:temPath);//
		
		super.prepare();
	}

	/**
	 * "app.tempary.path" if set env, 
	 * "java.io.tmpdir" if not set env
	 * @return temparary path
	 */
	public static String getTemparyPath(){
		return getRootEnv(APP_TEMPARY_PATH);
	}
	
	/**
	 * jvm processid,<br />
	 * which stored env by "sys.process.code"
	 * @return process code
	 */
	public static String getProcessCode(){
		return getRootEnv(SYS_PROCESS_CODE);
	}

	/**
	 * machina name-> nat name-> host -> ip, <br />
	 * which stored env by "sys.machine.name"
	 * @return
	 */
	public static String getMachineName(){
		String name = getRootEnv(SYS_MACHINE_NAME);
		if( name==null )try{
			name=getLocalMachineName();
		}catch(Exception e){}
		return name;
	}
	//
	/**
	 *  config from "app.startup.code", <br />
	 *  hashcode by pid in [1024,9216) if not set
	 * @return
	 */
	public static String getStartupCode(){
		return getRootEnv(APP_STARTUP_CODE);
	}
	
	/**
	 * the datacenter id or cluster id, <br />
	 * stored in env by "app.cluster.site"
	 * @return cluster id
	 */
	public static String getClusterSite(){
		String site = getRootEnv(APP_CLUSTER_SITE);
		return site==null?guessProperty(APP_CLUSTER_SITE):site;
	}
	
	public static String getProjectName(){
		return getRootEnv(APP_PROJECT_NAME);
	}
	
	/**
	 * @return code between 1024~9215(1024+8192-1=0x22FF)
	 */
	private static String genStartupCode(){
		int code = getRootEnv(SYS_PROCESS_CODE).hashCode();
			code+= getRootEnv(SYS_MACHINE_NAME).hashCode();
			//code&=0x1FF; //max 512-1;
			//code+=0x80; //min 128;
			code&=0x1FFF;//max 8192-1
			code+=0x400;//min 1024 
		return String.valueOf(code);	
	}
	
	/**
	 * get pid from RuntimeMXBean.name
	 * @return pid 
	 */
	private static String getPID() {
        try {
            String name = getMBeanName(); // format: "pid@hostname"  
            return name.substring(0, name.indexOf('@'));
        } catch (Throwable e) {}
        return "0";
    }
	
	/**
	 * get RuntimeMXBean.name
	 * @return pid 
	 */
	private static String getMBeanName() {
        try {
        	// format: "pid@hostname"  
            return java.lang.management.ManagementFactory.getRuntimeMXBean().getName(); 
         } catch (Exception e) {
        	 return "0@";
        }
    }
	
	/**
	 * @see ch.qos.logback.core.util.ContextUtil#getLocalHostName()
	 * @return the local host/machine name
	 */
	public static String getLocalHostName() {
		String name;
		try {
			name = getLocalMachineName();//ContextUtil.getLocalHostName()
		} catch (Exception e) {
			name = getMBeanName();
			name = name.substring(name.indexOf('@')+1);
		}		
		return name;
	}
	
	/**
	 * return the machine name by loop InetAddress,NetworkInterface, except localhost, 127.0.0.1,0.0.0.0
	 */
	static String getLocalMachineName() throws IOException {
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			//InetAddress.getCanonicalHostName();
			return localhost.getHostName();
		} catch (Exception e) {
			return getLocalAddressAsString();
		}
	}

	static String getLocalAddressAsString() throws IOException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces != null && interfaces.hasMoreElements()) {
			Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
			while (addresses != null && addresses.hasMoreElements()) {
				InetAddress address = addresses.nextElement();
				if (acceptableAddress(address)) {
					return address.getHostAddress();
				}
			}
		}
		throw new IOException();//UnknownHostException
	}

	private static boolean acceptableAddress(InetAddress address) {
		return !(address == null || address.isLoopbackAddress() || address.isAnyLocalAddress() || address.isLinkLocalAddress());
	}

}
