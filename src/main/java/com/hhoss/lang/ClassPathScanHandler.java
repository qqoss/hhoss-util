package com.hhoss.lang;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import com.hhoss.jour.Logger;


public class ClassPathScanHandler {

  
    private static final Logger logger = Logger.get();

  
    private boolean excludeInner = true;
   
    private boolean checkInOrEx = true;

  
    private List<String> classFilters = null;

  
    public ClassPathScanHandler() {
    }

  
    public ClassPathScanHandler(Boolean excludeInner, Boolean checkInOrEx,
            List<String> classFilters) {
        this.excludeInner = excludeInner;
        this.checkInOrEx = checkInOrEx;
        this.classFilters = classFilters;

   }

  
    public Set getPackageAllClasses(String basePackage,  boolean recursive) {
        Set classes = new LinkedHashSet();
        String packageName = basePackage;
        if (packageName.endsWith(".")) {
            packageName = packageName
                    .substring(0, packageName.lastIndexOf('.'));
        }
        String package2Path = packageName.replace('.', '/');

         try {
        	Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(package2Path);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    logger.info("扫描file类型的class文件....");
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    doScanPackageClassesByFile(classes, packageName, filePath, recursive);
                } else if ("jar".equals(protocol)) {
                    logger.info("扫描jar文件中的类....");
                    doScanPackageClassesByJar(packageName, url, recursive, classes);
                }
            }
        } catch (IOException e) {
            logger.error("IOException error:", e);
        }

       return classes;
    }

  
    private void doScanPackageClassesByJar(String basePackage, URL url,
            final boolean recursive, Set classes) {
        String packageName = basePackage;
        String package2Path = packageName.replace('.', '/');
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(package2Path) || entry.isDirectory()) {
                    continue;
                }

               // 判断是否递归搜索子包
                if (!recursive
                        && name.lastIndexOf('/') != package2Path.length()) {
                    continue;
                }
                // 判断是否过滤 inner class
                if (this.excludeInner && name.indexOf('$') != -1) {
                    logger.info("exclude inner class with name:" + name);
                    continue;
                }
                String classSimpleName = name
                        .substring(name.lastIndexOf('/') + 1);
                // 判定是否符合过滤条件
                if (this.filterClassName(classSimpleName)) {
                    String className = name.replace('/', '.');
                    className = className.substring(0, className.length() - 6);
                    try {
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                    } catch (ClassNotFoundException e) {
                        logger.error("Class.forName error:", e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("IOException error:", e);
        }
    }

  
    private void doScanPackageClassesByFile(Set classes, String packageName, String packagePath, boolean recursive) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        final boolean fileRecursive = recursive;
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义文件过滤规则
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return fileRecursive;
                }
                String filename = file.getName();
                if (excludeInner && filename.indexOf('$') != -1) {
                    logger.info("exclude inner class with name:" + filename);
                    return false;
                }
                return filterClassName(filename);
            }
        });
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                doScanPackageClassesByFile(classes, packageName + "."
                        + file.getName(), file.getAbsolutePath(), recursive);
            } else {
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));

               } catch (ClassNotFoundException e) {
                    logger.error("IOException error:", e);
                }
            }
        }
    }

  
    private boolean filterClassName(String className) {
        if (!className.endsWith(".class")) {
            return false;
        }
        if (null == this.classFilters || this.classFilters.isEmpty()) {
            return true;
        }
        String tmpName = className.substring(0, className.length() - 6);
        boolean flag = false;
        for (String str : classFilters) {
            String tmpreg = "^" + str.replace("*", ".*") + "$";
            Pattern p = Pattern.compile(tmpreg);
            if (p.matcher(tmpName).find()) {
                flag = true;
                break;
            }
        }
        return (checkInOrEx && flag) || (!checkInOrEx && !flag);
    }

  
    public boolean isExcludeInner() {
        return excludeInner;
    }

  
    public boolean isCheckInOrEx() {
        return checkInOrEx;
    }

  
    public List getClassFilters() {
        return classFilters;
    }

  
    public void setExcludeInner(boolean pExcludeInner) {
        excludeInner = pExcludeInner;
    }

  
    public void setCheckInOrEx(boolean pCheckInOrEx) {
        checkInOrEx = pCheckInOrEx;
    }

  
    public void setClassFilters(List<String> pClassFilters) {
        classFilters = pClassFilters;
    }

  
    public static void main(String[] args) {
     ClassPathScanHandler handler = new ClassPathScanHandler();
     Set classSet = handler.getPackageAllClasses( 
      "com.nicolas.service.impl", true); 
     for (Iterator iterator = classSet.iterator(); iterator.hasNext();) {
   Class clazz = (Class) iterator.next();
   System.out.println(clazz.getName());
  }
 }
}


