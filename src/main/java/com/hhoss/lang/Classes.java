package com.hhoss.lang;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;

import com.hhoss.jour.Logger;

/**
 * class handle util
 * @author Kejun
 * 
 * @see org.slf4j.helpers.Util.getCallingClass()
 * @See https://github.com/nallar/WhoCalled
 *
 */
public final class Classes{	
	/**
	 * Java 9 introduced StackWalker API as an alternative to Thread.getStackTrace() or Throwable.getStackTrace() and SecurityManager.getClassContext(). 
	 * This API targets a mechanism to traverse and materialize required stack frames allowing efficient lazy access to additional stack frames when required.
	 * @return
	 */
	static Class<?>[] callers(int skip){
		//Thread.currentThread().getStackTrace()
		StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
		Class<?>[] callers = walker.walk(s ->s.map(StackFrame::getDeclaringClass).skip(skip).toArray(Class[]::new));
		//Class<?>[] callers = walker.walk(s ->s.map(StackFrame::getDeclaringClass).skip(1).toArray(Class[]::new));
		//Optional<Class<?>> callerClass = walker.walk(s->s.map(StackFrame::getDeclaringClass).findFirst());
		//List<StackFrame> stack = walker.walk(s -> s.limit(10).collect(Collectors.toList()));
		return callers;
	}
	
		
	/**
	 * @return the caller's stack, not include Classes self;
	 */
	public static Class<?>[] callers() {
		/* 
		performance is better if stack is small
		Class<?> caller = null;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // Reflective call to get caller class is only needed if a security manager
            // is present.  Avoid the overhead of making this call otherwise.
 		    //Reflection.getCallerClass(3);  @see Class.forName(...,...,..)
            caller = Reflection.getCallerClass();  //removed from jdk8
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
		//Class<?>[] cls = getCallers(); return Arrays.copyOfRange(cls, 1, cls.length);
		return callers(2); 
	}

	/**
	 * @param i the caller stack index
	 * @return the caller's class;
	 * caller is the invoked codes line on
	 * Caller is 1; Caller's Caller is 2...; Classes is 0;
	 */
	public static Class<?> caller(int i) {
		return callers(2)[i];
	}

	/**
	 * @return the class of caller
	 */
	public static Class<?> caller() {
		return caller(2);
	}

	/**
	 * @return the refer class's name (codes in refer class)
	 */
	public static String referName() {
		return caller(1).getName();
	}
	
	/**
	 * @return ReferClass's caller's classNme (eg:LoggerFactory's caller)
	 */
	public static String callerName() {
		return caller(2).getName();
	}

	/**
	 * @return the sub class name which extends caller's class (eg: clazz extends/invoke LoggerFactory's caller)
	 */
	public static String invokerName() {
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
	
	
	public static void main(String[] args) {
		test();
	}
	
	private static void test(){
		Logger LOG = Logger.get();
		LOG.info("caller :{}",ClassMeta.caller());
		LOG.info("caller :{}",Classes.caller());
	}

	
}
