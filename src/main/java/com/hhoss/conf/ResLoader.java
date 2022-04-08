package com.hhoss.conf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import com.hhoss.jour.Logger;

/**
 * for custom class loader
 * @author kejun
 *
 */
public class ResLoader extends ClassLoader {
	private static final Logger logger = Logger.get();
	private static final int BUFFER_SIZE = 8192;
	private static final File SP_FOLDER = new File("<systemPaths>");
	private Collection<File> lookups = new Vector<>();

	protected ResLoader() {
		super(ResLoader.class.getClassLoader());
	}

	@Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                c = findClass(name);
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

	/*
	 * @see org.apache.tools.ant.AntClassLoader#findClassInComponents
	 * 
	 */
	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		Class<?> c = null;
		InputStream stream = null;
		String classFile = className.replace('.', '/') + ".class";
		stream = getResourceAsStream(classFile);
		try {
			if (stream != null) {
				c = getClassFromStream(className, stream);
			}
		} catch (IOException e) {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException ioe) {
			}
		}
		if (c == null) {
			throw new ClassNotFoundException(className);
		}
		return c;
	}

	/**
	 * load a class 
	 * @param className
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private Class<?> getClassFromStream(String className, InputStream stream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int bytesRead = -1;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((bytesRead = stream.read(buffer, 0, BUFFER_SIZE)) != -1) {
			baos.write(buffer, 0, bytesRead);
		}
		byte[] classData = baos.toByteArray();
		return defineClass(className, classData, 0, classData.length);
	}

    @Override
	public URL getResource(String name) {
		URL url = null;
		for(File folder:lookups){
			if (SP_FOLDER.equals(folder)) {
				url = super.getResource(name);
			} else {
				File f = new File(folder, name);
				if(f.exists())try {
					url = f.toURI().toURL();
				} catch (Exception e) {
					logger.info("getResource[{}] Exception {} ",name,e.getMessage());
				}
			}
			if (url != null) {
				logger.debug("getResource[{}] from {} ",name,url);
				break;
			}
		}
		
		if( url==null && !lookups.contains(SP_FOLDER) ){
			url=super.getResource(name);
		}
		return url;
	}
    
   @Override
   protected URL findResource(String name) {
	   URL url = null;
		for(File folder:lookups){
			if (SP_FOLDER.equals(folder)) {
				url=super.findResource(name);
			}else{
				File f = new File(folder, name);
				if(f.exists())try {
					url = f.toURI().toURL();
				} catch (Exception e) {
					logger.info("findResource[{}] Exception {} ",name,e.getMessage());
				}
			}
			if (url != null) {
				logger.debug("findResource[{}] from {} ",name,url);
				break;
			}
		}
		if( url==null && !lookups.contains(SP_FOLDER) ){
			url=super.findResource(name);
		}
		return url;
   }


	@Override
	public InputStream getResourceAsStream(String name) {
		// we need to search the components of the path to see if we can
		// find the class we want.
		InputStream stream = null;
		for(File folder:lookups){
			if (SP_FOLDER.equals(folder)) {
				stream = getFromParent(name);
			} else{
				stream = getStream(folder, name);
			}
			if (stream != null) {
				logger.info("getResourceStream[{}] from {} ",name,folder.getPath());
				break;
			}
		}
		if(stream==null && !lookups.contains(SP_FOLDER) ){
			stream = getFromParent(name);
		}
		return stream;
	}
   
	private InputStream getFromParent(String name){
	   ClassLoader parent = getParent();
	   if(parent==null){
		   return getSystemResourceAsStream(name);
	   }else{
		   return parent.getResourceAsStream(name);
	   }
	}

	private InputStream getStream(File folder, String name) {
		if(folder==null){return null;}
		try {
			if (folder.isDirectory()) {
				File resource = new File(folder, name);
				if (resource.canRead()) {
					return new FileInputStream(resource);
				}
			} else if (folder.isFile()) {
				//TODO: if from zip/jar, should have  "!/"
				//@see org.springframework.util.ResourceUtils#JAR_URL_SEPARATOR="!/"
				//@see ServiceLoader
			}
		} catch (Exception e) {
			logger.info("Ignoring Exception {}:{} ; reading resource {} from {} . ",e.getClass().getName(),e.getMessage() ,name,folder);
		}
		return null;
	}

	protected void setLookups(String[] pathList) throws NullPointerException {
		//Collection<File> newPaths = getPaths(pathList);
		Collection<File> newPaths = getLookupFolders(Arrays.asList(pathList));
		if(newPaths.isEmpty()){
			logger.warn("try to set empty path, ignored.");
		}else{
			lookups=newPaths;
		}
	}
	
	private Collection<File> getLookupFolders(Collection<String> cs){
		Set<File> paths = new LinkedHashSet<>();
		for(String f:cs){
			if( f.trim().length() > 0 ){
				paths.add(new File(f.trim()));
				//File folder = new File(f.trim()); 
				//if(folder.canRead()){ paths.add(folder);}
			}			
		}
		return paths;
	}
	
	public static void main(String[] args) throws Exception{
		File f1 = new File("<SysTemPaths>");
		File f2 = new File(SP_FOLDER,"test");
		System.out.println(SP_FOLDER.equals(f1));//true;
		System.out.println(f2.exists());//false;
		System.out.println(f2.canRead());//false;
		
	}

}
