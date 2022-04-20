package com.hhoss.lang;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * class handle util
 * 
 * @author Kejun
 * 
 * @see org.slf4j.helpers.Util.getCallingClass()
 * @See https://github.com/nallar/WhoCalled
 *
 */
public final class Classes {

	private static final Map<Class<?>, Class<?>> basicType = new IdentityHashMap<>(20);
	// private static final Map<Class<?>, Class<?>> plainType = new IdentityHashMap<>(16);
	static {
		basicType.put(Void.TYPE, Void.class);// =void.class
		basicType.put(Byte.TYPE, Byte.class);// =byte.class
		basicType.put(Long.TYPE, Long.class);// =long.class
		basicType.put(Short.TYPE, Short.class);// =short.class
		basicType.put(Float.TYPE, Float.class);// =float.class
		basicType.put(Double.TYPE, Double.class);// =double.class
		basicType.put(Integer.TYPE, Integer.class);// =int.class
		basicType.put(Boolean.TYPE, Boolean.class);// =boolean.class
		basicType.put(Character.TYPE, Character.class);// =char.class
		basicType.put(Void.class, Void.class);
		basicType.put(Byte.class, Byte.class);
		basicType.put(Long.class, Long.class);
		basicType.put(Short.class, Short.class);
		basicType.put(Float.class, Float.class);
		basicType.put(Double.class, Double.class);
		basicType.put(Integer.class, Integer.class);
		basicType.put(Boolean.class, Boolean.class);
		basicType.put(Character.class, Character.class);
		basicType.put(String.class, Character.class);
		// plainType.put(Date.class, Character.class);
		// plainType.put(VO.class, Character.class);
	}

	public static Class<?> normalize(Class<?> cls) {
		return (cls.isPrimitive()) ? basicType.get(cls) : cls;
	}

	/**
	 * @param klass
	 * @return true if class is primitive type or primitive wrapped class
	 */
	public static boolean isBasic(Class<?> klass) {
		return basicType.get(klass) != null;
	}
	/**
	 * @param klass
	 * @return true if String, Date, Enum, VO or Primitive
	 */
	public static boolean isPlain(Class<?> klass){
		if(klass==null){return false;}
    	return Classes.isBasic(klass)||
    		   //plainType.get(klass)!=null||
      		   Number.class.isAssignableFrom(klass)||
       		   Date.class.isAssignableFrom(klass)||
       		   Enum.class.isAssignableFrom(klass)||
       		  // Item.class.isAssignableFrom(klass)||
		   	   klass.isEnum();
 	}
	
	public static boolean isGroup(Class<?> klass) {
		if (klass == null) {return false;}
		return klass.isArray() || Map.class.isAssignableFrom(klass) || Collection.class.isAssignableFrom(klass);
	}	

	public static boolean isDefault(Object obj){
		if(obj==null){ return true; }		
		Class<?> cls = obj.getClass();
		if(cls==Boolean.class){
			return Boolean.FALSE.equals(obj);
		}if(cls==Character.class){
			return Character.valueOf((char)0).equals(obj);
		}else if(Number.class.isAssignableFrom(cls)){
			return ((Number)obj).doubleValue()==0d;
		}else if(CharSequence.class.isAssignableFrom(cls)){
			return ((CharSequence)obj).length()==0;
		}else if(cls.isArray()) {
			 return ((Object[])obj).length==0;
		}else if(Map.class.isAssignableFrom(cls)) {
			 return ((Map<?,?>)obj).size()==0;
		}else if(Collection.class.isAssignableFrom(cls)) {
			 return ((Collection<?>)obj).isEmpty();
		}
		return false;
	}
	
	public static Field getField(Class<?> klass, String name){
		Class<?> cls = klass;
		while(cls!=null){
			for(Field fld:cls.getDeclaredFields()){
				if(fld.getName().equalsIgnoreCase(name)){
					return fld;
				}
			}
			cls = cls.getSuperclass(); //得到父类,然后赋给自己
		}
		return null;		
	}
	
	public static List<Field> getFields(){
		return getFields(caller());
	}
    
	public static List<Field> getFields(Class<?> klass, Class<Annotation> anno){
		List<Field> flds = new ArrayList<>();
		for (Field fld : getFields(klass)){
			if(anno==null||fld.getName().startsWith("this$")) {//this$0 内部类引用的主类
				continue;
			}
			if(fld.isAnnotationPresent(anno)){ flds.add(fld); }
		}
		return flds;		
	}	

	public static List<Field> getFields(Class<?> klass){
		return getFields(klass, Collections.emptyList());
	}
	public static List<Field> getFields(Class<?> klass, Iterable<Class<?>> fldTypes){
		Class<?> cls = klass;
		List<Field> fields = new ArrayList<>();
		while(cls!=null){//cls.getSimpleName().equals("Object")||cls.getSimpleName().equals("Object")
			for(Field fld:cls.getDeclaredFields()){
				if(Modifier.isStatic(fld.getModifiers())||fld.getName().startsWith("this$")){
					continue;
				}
				if(Classes.isPlain(fld.getType())||Classes.isGroup(fld.getType()) ){
					fields.add(fld);
					continue;
				}
				if(fldTypes!=null)for(Class<?> type:fldTypes){
					if(type.isAssignableFrom(fld.getType())) {
						fields.add(fld);
						break;
					}
				}
			}
			cls = cls.getSuperclass(); //loop parent class
		}
		return fields;		
	}

	/**
	 * Java 9 introduced StackWalker API as an alternative to Thread.getStackTrace()
	 * or Throwable.getStackTrace() and SecurityManager.getClassContext(). This API
	 * targets a mechanism to traverse and materialize required stack frames
	 * allowing efficient lazy access to additional stack frames when required.
	 * 
	 * @return
	 */
	static Class<?>[] callers(int skip) {
		// Thread.currentThread().getStackTrace()
		StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
		Class<?>[] callers = walker.walk(s -> s.map(StackFrame::getDeclaringClass).skip(skip).toArray(Class[]::new));
		// Class<?>[] callers = walker.walk(s
		// ->s.map(StackFrame::getDeclaringClass).skip(1).toArray(Class[]::new));
		// Optional<Class<?>> callerClass =
		// walker.walk(s->s.map(StackFrame::getDeclaringClass).findFirst());
		// List<StackFrame> stack = walker.walk(s ->
		// s.limit(10).collect(Collectors.toList()));
		return callers;
	}

	/**
	 * @return the caller's stack, not include Classes self;
	 */
	public static Class<?>[] callers() {
		/*
		 * performance is better if stack is small Class<?> caller = null;
		 * SecurityManager sm = System.getSecurityManager(); if (sm != null) { //
		 * Reflective call to get caller class is only needed if a security manager //
		 * is present. Avoid the overhead of making this call otherwise.
		 * //Reflection.getCallerClass(3); @see Class.forName(...,...,..) caller =
		 * Reflection.getCallerClass(); //removed from jdk8 if (loader == null) {
		 * ClassLoader ccl = ClassLoader.getClassLoader(caller); if (ccl != null) {
		 * sm.checkPermission( SecurityConstants.GET_CLASSLOADER_PERMISSION); } } }
		 */
		/*
		 * performance is lower than classContext, StackTraceElement[] stElements =
		 * Thread.currentThread().getStackTrace(); for (int i=1; i<stElements.length;
		 * i++) { StackTraceElement ste = stElements[i]; if
		 * (ste.getClassName().indexOf("java.lang.Thread")!=0) { ste.getClass(); } }
		 */
		// cls 0=instance[.getClassContext()].class=ClassMeta;
		// 1=Caller[ClassMeta.caller(n)].class,
		// 2=Caller{Caller[ClassMeta.caller()]].class
		// Class<?>[] cls = getCallers(); return Arrays.copyOfRange(cls, 1, cls.length);
		return callers(2);
	}

	/**
	 * @param i the caller stack index
	 * @return the caller's class; caller is the invoked codes line on Caller is 1;
	 *         Caller's Caller is 2...; Classes is 0;
	 */
	public static Class<?> caller(int i) {
		return callers(2)[i];
	}

    /**
     * @return the caller class of the current method called by
     */
    public static Class<?> caller() {
      return caller(2);
  }


	/**
	 * @return the refer class of the current codes write on
	 * can use to instead class.getClass();
	 */
    public static Class<?> get() {
      return caller(1);
    }

    /**
	 * @return the refer class' name of current codes write on
	 */
	public static String getName() {
		return caller(1).getName();
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object obj, String method, Object... params) throws Exception {
		Method mth = getMethod(obj, method, params);
		if (mth == null) {
			StringBuilder sb = new StringBuilder("can't find method[");
			sb.append(method).append("] with special params in ");
			sb.append((obj instanceof Class) ? obj : obj.getClass());
			throw new IllegalArgumentException(sb.toString());
		}
		mth.setAccessible(true);
		return (T) mth.invoke(obj, params);
	}

	public static Method getMethod(Object obj, String name, Object... params) throws Exception {
		Class<?> cls = (obj instanceof Class) ? (Class<?>) obj : obj.getClass();
		while (cls != null) {
			for (Method mth : cls.getDeclaredMethods()) {
				if (mth.getName().equals(name) && validTypes(params, mth.getParameterTypes())) {
					return mth;
				}
			}
			cls = cls.getSuperclass(); // 得到父类,然后赋给自己
		}
		return null;
	}

	/**
	 * @param params
	 * @param types
	 * @return true if every params[i] is null or is instace of types[i]
	 */
	public static boolean validTypes(Object[] params, Class<?>[] types) {
		if ((params == null || params.length == 0) && types.length == 0) {
			return true;
		}
		if (types.length != params.length) {
			return false;
		}
		for (int i = 0; i < types.length; i++) {
			if (params[i] != null && !types[i].isInstance(params[i])) {
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
		// return (Class)cls.getTypeParameters()[idx].getBounds()[0];
	}

	/**
	 * @return Class of first generic type
	 */
	public static Class<?> getGenericType(Object obj) {
		return (Class<?>) getGenericType(obj.getClass(), 0);
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
