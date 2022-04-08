package com.hhoss.lang;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;

/**
 * class handle util
 * @author Kejun
 * 
 * @see org.slf4j.helpers.Util.getCallingClass()
 * @See https://github.com/nallar/WhoCalled
 * 
 * @deprecated The Security Manager is deprecated and subject to removal in a
 *       future release. There is no replacement for the Security Manager.
 *       See <a href="https://openjdk.java.net/jeps/411">JEP 411</a> for
 *       discussion and alternatives.
 */
@Deprecated(forRemoval=true,since="jdk17")
public final class ClassMeta extends SecurityManager {
	private static final ClassMeta instance = new ClassMeta();	
		
	/**
	 * @return the caller's stack, not include ClassMeta self;
	 */
	public static Class<?>[] callers() {
		/* 
		 * performance is better if stack is small
		Class<?> caller = null;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // Reflective call to get caller class is only needed if a security manager
            // is present.  Avoid the overhead of making this call otherwise.
 		    //Reflection.getCallerClass(3);  @see Class.forName(...,...,..)
            caller = Reflection.getCallerClass();//removed from jdk8
           if (loader == null) {
                ClassLoader ccl = ClassLoader.getClassLoader(caller);
                if (ccl != null) {
                    sm.checkPermission(
                        SecurityConstants.GET_CLASSLOADER_PERMISSION);
                }
            }
        }
		*/
		/*
		 * performance is lower than classContext, 
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
             if (ste.getClassName().indexOf("java.lang.Thread")!=0) {
                ste.getClass();
            }
        }
        */
		//cls 0=instance[.getClassContext()].class=ClassMeta; 1=Caller[ClassMeta.caller(n)].class, 2=Caller{Caller[ClassMeta.caller()]].class
		Class<?>[] cls = instance.getClassContext();
		return Arrays.copyOfRange(cls, 1, cls.length);
	}

	/**
	 * @param i the caller stack index
	 * @return the caller's class;
	 * caller is the invoked codes line on
	 * Caller is 1; Caller's Caller is 2...; ClassMeta is 0;
	 */
	public static Class<?> caller(int i) {
		return instance.getClassContext()[i+1];
	}

	/**
	 * @param i the caller stack index
	 * @return the caller's classNme;
	 * caller is the invoked codes line on
	 * Caller is 1; Caller's Caller is 2...; ClassMeta is 0;
	 */
	public static String callerName(int i) {
		//return instance.getClassContext()[i].getName();
		return caller(i).getName();
	}

	/**
	 * @return the self class where code in
	 */
	public static Class<?> refer() {
		//return caller(1);
		return instance.getClassContext()[1];
	}

	/**
	 * @return the class of caller
	 */
	public static Class<?> caller() {
		//return caller(2);
		return instance.getClassContext()[2];
	}

	/**
	 * @return the class of caller
	 */
	public static Class<?> invoker() {
		//return caller(2);
		return instance.getClassContext()[3];
	}

	/**
	 * @return the refer class's name (codes in refer class)
	 */
	public static String referName() {
		//return getClassName(1);
		return caller(1).getName();
	}
	
	/**
	 * @return ReferClass's caller's classNme (eg:LoggerFactory's caller)
	 */
	public static String callerName() {
		//return getClassName(2);
		return caller(2).getName();
	}

	/**
	 * @return the sub class name which extends caller's class (eg: clazz extends/invoke LoggerFactory's caller)
	 */
	public static String invokerName() {
		//return getClassName(3);
		return caller(3).getName();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object obj, String method, Object... params) throws Exception{
		Method mth = getMethod(obj,method,params);
		if( mth ==null ){
			StringBuilder sb = new StringBuilder("can't find method[");
			sb.append(method).append("] with special params in ");
			sb.append((obj instanceof Class)?obj:obj.getClass());
			throw new IllegalArgumentException(sb.toString());
		}		
		mth.setAccessible(true);
		return (T)mth.invoke(obj, params);
	}
	
	public static Method getMethod(Object obj, String name, Object... params) throws Exception{
		Class<?> cls = (obj instanceof Class)?(Class<?>)obj:obj.getClass();
		while(cls!=null){
			for(Method mth:cls.getDeclaredMethods()){
				if(mth.getName().equals(name) && validTypes(params,mth.getParameterTypes())){
					return mth;
				}
			}
			cls = cls.getSuperclass(); //得到父类,然后赋给自己
		}
		return null;		
	}
	
	/**
	 * @param params
	 * @param types
	 * @return true if every params[i] is null or is instace of types[i]
	 */
	public static boolean validTypes(Object[] params, Class<?>[] types){
		if((params==null||params.length==0)&& types.length==0 ){
			return true;
		}
		if(types.length!=params.length){
			return false;
		}
		for(int i =0 ; i<types.length; i++){
			if( params[i]!=null && !types[i].isInstance(params[i]) ){
				return false;
			}
		}		
		return true;			
	}

	/**
	 * @param cls the class should be given special generic subclass;[已泛型化好的子类]
	 * @param idx
	 * @return
	 */
	public static Class<?> getGenericType(Class<?> cls, int idx) {
		  Type genType = cls.getGenericSuperclass();
		  if (!(genType instanceof ParameterizedType)) {
		   return Object.class;
		  }
		  Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		  if (idx >= params.length || idx < 0) {
		   throw new RuntimeException("Index outof bounds");
		  }
		  if (!(params[idx] instanceof Class)) {
		   return Object.class;
		  }
		  return (Class<?>) params[idx];
		//return (Class)cls.getTypeParameters()[idx].getBounds()[0];
	}
	
	/**
	 * @return Class of first generic type
	 */
	public static Class<?> getGenericType(Object obj) {
		return (Class<?>)getGenericType(obj.getClass(),0);
	}
	
	/**
	 * 获取可比较的元素类型
	 */
	public Class<?> getComparableType(Comparator<?> comparator) {
	    Type[] types = comparator.getClass().getGenericInterfaces();
	    for (Type type : types) {
	        if (type instanceof ParameterizedType) {
	            ParameterizedType ptype = (ParameterizedType) type;
	            if (Comparator.class.equals(ptype.getRawType())) {
	                Type cmpType = ptype.getActualTypeArguments()[0];
	                if (cmpType instanceof ParameterizedType) {
	                    return (Class<?>) ((ParameterizedType) cmpType).getRawType();
	                } else {
	                    return (Class<?>) ptype.getActualTypeArguments()[0];
	                }
	            }
	        }
	    }
	    return Object.class;
	}
	
}
