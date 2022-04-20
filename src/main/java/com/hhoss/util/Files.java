package com.hhoss.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

import com.hhoss.lang.Classes;
import com.hhoss.util.token.TokenProvider;

/**
 * file delete util
 * 
 * @author kejun
 *
 */
public class Files {

  public static void delete(File file) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (File f : files) {
        delete(f);
      }
    }
    file.delete();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * @see {@link org.springframework.boot.system.ApplicationHome#findSource(Class<?> sourceClass) }
   */
  public static File find(Class<?> sourceClass) {
    try {
      ProtectionDomain domain = (sourceClass == null) ? null : sourceClass.getProtectionDomain();
      CodeSource codeSource = (domain == null) ? null : domain.getCodeSource();
      URL location = (codeSource == null) ? null : codeSource.getLocation();
      File source = (location == null) ? null : find(location);
      if (source != null && source.exists()) {
        return source.getAbsoluteFile();
      }
    } catch (Exception ex) {
    }
    return null;
  }

  private static File find(URL location) throws IOException, URISyntaxException {
    URLConnection connection = location.openConnection();
    if (connection instanceof JarURLConnection) {
      return find(((JarURLConnection) connection).getJarFile());
    }
    return new File(location.toURI());
  }

  private static File find(JarFile jarFile) {
    String name = jarFile.getName();
    int separator = name.indexOf("!/");
    if (separator > 0) {
      name = name.substring(0, separator);
    }
    return new File(name);
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////

  public static File findFolder(File source) {
    File folder = source;
    if (folder.isFile()) {
      folder = folder.getParentFile();
    }
    if (folder.exists()) {
      return folder.getAbsoluteFile();
    }
    // TODO:create or not?
    folder = new File(".");
    return folder.getAbsoluteFile();
  }

  public static File findUserHome() {
    String userDir = TokenProvider.from("system").get("user.dir");
    if (userDir == null || userDir.isBlank()) {
      return new File(".").getAbsoluteFile();
    }
    return new File(userDir).getAbsoluteFile();
  }

  public static String findTemp() {
    String tmp = TokenProvider.from("system").get("java.io.tmpdir");
    if(tmp==null) {return null;}
    try {
      Path p = Paths.get(tmp);
      if (!java.nio.file.Files.exists(p)) {
        p = java.nio.file.Files.createDirectory(p);
      }
      return p.toFile().getAbsolutePath();
    } catch (IOException ex) {
      throw new IllegalStateException("Unable to create temp directory " + tmp, ex);
    }
  }



  ////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Kryo@20070914 get a class or jar root path
   * 
   * @param Class
   * @return String - class root path; if loaded by webapp classloader, then return webinf path; not endWith "/", like "/d:/p0/p1/p2";
   */
  @Deprecated
  static String findBootFolder() {
    Class<?> clazz = Classes.caller();
    String path = clazz.getResource("/").getPath();
    int pos = path.indexOf(clazz.getPackage().getName().replace('.', '/'));// "/biz/zheng/log"
    if (pos > 0) {
      path = path.substring(0, pos - 1);
    }
    return new File(path).getAbsolutePath();
  }

  /**
   * @return path the folder of the class/jar file belong to
   */
  public static String findBoot(Class<?> clazz) {
    if(clazz==null){clazz=Classes.caller();}
    // clazz = LocationAwareLogger.class;
    File file = findFolder(find(clazz));
    return trimSubfix(file.getAbsolutePath());
  }

  // TODO: if from jar, should have "!/"
  // @see org.springframework.util.ResourceUtils.JAR_URL_SEPARATOR="!/"
  public static String findRoot(String rp) {
    String path=(rp==null)?findBoot(Classes.caller()):rp;
    path = trimSubfix(path);
    if (path.endsWith("classes")) { // classes or test-classes
      int idx = path.lastIndexOf('/');
      if (idx < 0) {
        idx = path.lastIndexOf('\\');
      }
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

  /**
   * @param strPath
   * @param defPath
   * @return path NOT end with "\" or "/"
   */
  public static String trimSubfix(String strPath) {
    String path = strPath;
    if (path != null) {
      path = path.trim();
      if (path.endsWith("/") || path.endsWith("\\")) {
        path = path.substring(0, path.length() - 1);
      }
    }
    return path;
  }

  public static void main(String[] args) {
    com.hhoss.jour.Logger.get().info(findRoot(null));
  }

}
