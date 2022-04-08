package com.hhoss.util.token;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;

import com.hhoss.jour.Logger;

public final class SystemProvider extends TokenProvider{
	private static final long serialVersionUID = -7201727132767821279L;
	private static final Logger logger = Logger.get();
	protected static final SystemProvider instance = new SystemProvider();
	
	@Override public String getName(){return PREFIX+"system";}
	/**
	 * @param Enviroment key
	 * @return the value set in Enviroment as below
	 * 1.getProperty是获取系统的相关属性,包括文件编码,操作系统名称,区域,用户名等,此属性一般由jvm自动获取,不能设置
	 * 2.getenv是获取系统的进程环境变量，对于windows对在系统属性-->高级-->环境变量中设置的变量将显示在此(对于linux,通过export设置的变量将显示在此)   
	 */
    @Override public String get(final String key) {
		String value = null;
	    try {
			//返回Java进程变量值;通过命令行参数的"-D"选项
			//value = System.getProperty(key);
			value = AccessController.doPrivileged(new PrivilegedAction<String>() 
						{ public String run(){return System.getProperty(key);} }
					);
		} catch (Throwable e) { 
			// MS-Java throws com.ms.security.SecurityExceptionEx
			logger.warn("Was not allowed to read system property {}", key);
		}
		
		if (value == null) try {
			//返回系统环境变量值:用户主目录下的设置,在Shell中设置,在IDE中设置
			//value = System.getenv(key);
			value = AccessController.doPrivileged(new PrivilegedAction<String>() 
						{ public String run(){return System.getenv(key);} }
					);
		} catch (Throwable e) { 
			logger.warn("Was not allowed to read system environment {}", key);
		}
		return value;
   }
    
}
