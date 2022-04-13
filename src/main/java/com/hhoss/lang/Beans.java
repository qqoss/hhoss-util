package com.hhoss.lang;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.xml.sax.InputSource;

import com.hhoss.jour.Logger;
import com.hhoss.util.HMap;
/**
 * Provides auto-generation framework for {@code toString()} output.
 * <p>
 * Default exclusion policy (can be overridden with {@link GridToStringInclude}
 * annotation):
 * <ul>
 * <li>fields with {@link GridToStringExclude} annotations
 * <li>classes that have {@link GridToStringExclude} annotation (current list):
 *      <ul>
 *      <li>GridManager
 *      <li>GridManagerRegistry
 *      <li>GridProcessor
 *      <li>GridProcessorRegistry
 *      <li>IgniteLogger
 *      <li>GridDiscoveryMetricsProvider
 *      <li>GridByteArrayList
 *      </ul>
 * <li>static fields
 * <li>non-private fields
 * <li>arrays
 * <li>fields of type {@link Object}
 * <li>fields of type {@link Thread}
 * <li>fields of type {@link Runnable}
 * <li>fields of type {@link Serializable}
 * <li>fields of type {@link Externalizable}
 * <li>{@link InputStream} implementations
 * <li>{@link OutputStream} implementations
 * <li>{@link EventListener} implementations
 * <li>{@link Lock} implementations
 * <li>{@link ReadWriteLock} implementations
 * <li>{@link Condition} implementations
 * <li>{@link Map} implementations
 * <li>{@link Collection} implementations
 * </ul>
 */
public class Beans {
	public static final Logger logger=Logger.get();
    private static final HMap<HashSet<String>> classFields = new HMap<>();

    /** */
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();


	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object obj, String fld) throws Exception{
		Field field = Classes.getField((obj instanceof Class)?(Class<?>)obj:obj.getClass(),fld);
		field.setAccessible(true);
		return (T)field.get(obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T setFieldValue(Object obj, String fld, Object val){
		if( obj==null||fld==null ){
			throw new IllegalArgumentException("object or filed name should not be null!");
		}
		Class<?> clazz = (obj instanceof Class)?(Class<?>)obj:obj.getClass();
		Field field = Classes.getField(clazz,fld);
		if( field==null ){
			throw new IllegalArgumentException("can't find filed["+fld+"] in class["+val.getClass()+"].");
		}
		try {
			if(val==null || Classes.normalize(field.getType()).isInstance(val) ){	
				field.setAccessible(true);
				Object old=field.get(obj);
				field.set(obj, val);
				return (T)old;
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
		throw new IllegalArgumentException("typeof field["+field.getType().getSimpleName()+"]["+field.getType().getSimpleName()+"] is not compatible with typeof value["+val.getClass().getSimpleName()+"].");
	}	

	
    /**
     * Creates an uniformed string presentation for the given object.
     *@see org.apache.ignite.internal.util.tostring.GridToStringBuilder
     */
    protected static String str(Object obj) {
    	Class<?> cls = obj.getClass();
    	StringBuilder buf = new StringBuilder(cls.getSimpleName());
        try {
            buf.append("{ ");
            for(String name : getFieldNames(cls,null)) {
                Field field = cls.getDeclaredField(name);
                field.setAccessible(true);
                buf.append(name).append(':'); 
                buf.append(field.get(obj)).append(',');
                
            }
            buf.setCharAt(buf.length()-1,' ');
            buf.append('}');
            return buf.toString();
        } catch (Exception e) {
            rwLock.writeLock().lock();
            try {
                classFields.remove(cls.getName() + System.identityHashCode(cls.getClassLoader()));
            } finally {
                rwLock.writeLock().unlock();
            }
            throw new RuntimeException(e);
        }
    }

   
    

    /**
     * @param cls Class.
     * @param <T> Type of the object.
     * @return Descriptor for the class.
     */
    public static <T> Collection<String> getFieldNames(Class<T> cls, Iterable<Class<?>> fldTypes) {
        assert cls != null;
        String key = cls.getName() + System.identityHashCode(cls.getClassLoader());
        HashSet<String> fldNames;
        rwLock.readLock().lock();
        try {
            fldNames = classFields.get(key);
        }finally{
            rwLock.readLock().unlock();
        }

        if (fldNames == null) {
            fldNames = new HashSet<String>();
            List<Field> flds = Classes.getFields(cls,fldTypes);
            //final GridToStringInclude incFld = f.getAnnotation(GridToStringInclude.class);
            //final GridToStringInclude incType = type.getAnnotation(GridToStringInclude.class);
            
            for (Field f : flds) { fldNames.add(f.getName()); }
            /*
             * Allow multiple puts for the same class - they will simply override.
             */
            rwLock.writeLock().lock();
            try {
                classFields.put(key, fldNames);
            } finally {
                rwLock.writeLock().unlock();
            }
        }

        return fldNames;
    }
    
    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            return false;
        }
        return obj.getClass().isArray();
    }
  
	@SuppressWarnings({ "resource", "unchecked" })
	public static <T> T from(String xml){
		InputSource is = new InputSource(new StringReader(xml));
	    return (T) new XMLDecoder(is).readObject();  
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T from(URI uri) throws IOException{
		FileInputStream fis = new FileInputStream(new File(uri));
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(fis)); 
		T o = (T) decoder.readObject();
        decoder.close();
		return o;
	}
    
    public static void main(String[] args) {
    	System.out.print( Number.class.isAssignableFrom(Integer.class));
	}
  
	    
}
