package com.hhoss.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Wrapper {
	
	/**
	 * type T should assignable from S;
	 * @param source
	 * @return
	 */
	public static <K, S, T> Map<K, T> wrap(final Map<K, S> source) {
		if(source==null){return null;}
		try{return (Map<K, T>)source;}catch(ClassCastException e){}
		return new AbstractMap<K, T>() {
			public T put(K key, T value) {
				return (T) source.put(key, (S) value);
			}

			@Override
			public Set<Map.Entry<K, T>> entrySet() {
				return wrap(source.entrySet());
			}
		};

	}

	/**
	 * type T should assignable from S;
	 * @param source
	 * @return
	 */
	public static <K, S, T> Set<Map.Entry<K, T>> wrap(final Set<Map.Entry<K, S>> source) {
		if(source==null){return null;}
		
		return new AbstractSet<Map.Entry<K, T>>() {
			public Iterator<Map.Entry<K, T>> iterator() {
				return new Iterator<Map.Entry<K, T>>() {
					private Iterator<Map.Entry<K, S>> i = source.iterator();

					public boolean hasNext() {
						return i.hasNext();
					}

					public Map.Entry<K, T> next() {
						return (Map.Entry<K, T>) i.next();
					}

					public void remove() {
						i.remove();
					}
				};
			}

			public int size() {
				return source.size();
			}
		};

	}

	/**
	 * type T should assignable from S;
	 * @param source
	 * @return
	 */
	// TODO: simple it ...oherwise every invoke create new wrap object
	public static <S, T> Collection<T> wrap(final Collection<S> source) {
		if(source==null){return null;}
		try{return (Collection<T>)source;}catch(ClassCastException e){}
		return new AbstractCollection<T>() {
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					private Iterator<S> i = source.iterator();

					public boolean hasNext() {
						return i.hasNext();
					}

					public T next() {
						return (T) i.next();
					}

					public void remove() {
						i.remove();
					}
				};
			}

			public int size() {
				return source.size();
			}
		};

	}
	
	/**
	 * type T should assignable from S;
	 * @param source
	 * @return target construct a new hashMap if null;
	 */
	//always return not null;
	public static <K, S, T> Map<K, T> safeWrap(final Map<K, S> source) {
		if(source==null){return new HashMap<K,T>();}
		try{return (Map<K, T>)source;}catch(ClassCastException e){}
		return wrap(source);
	}

	/**
	 * type T should assignable from S;
	 * @param source
	 * @return target construct a new hashMap.entrySet() if null;
	 */
	public static <K, S, T> Set<Map.Entry<K, T>> safeWrap(final Set<Map.Entry<K, S>> source) {
		Set<Map.Entry<K, S>> set = (source==null)?new HashMap<K,S>().entrySet():source;
		return wrap(set);
	}

	/**
	 * type T should assignable from S;
	 * @param source
	 * @return target construct a new hashMap.values() if null;
	 */
	public static <S, T> Collection<T> safeWrap(final Collection<S> source) {
		Collection<S> col = (source==null)?new HashMap<String,S>().values():source;
		return wrap(col);
	}

	/**
	 * @param a
	 * @return Iterable for array 
	 */
	public static <V> Iterable<V> iterate(final V[] a){
       return new Iterable<V>() {
			@Override
    	    public Iterator<V> iterator() {
			    //return Iterators.forArray(a);   //com.google.common.collect.Iterators from guava utility
				return new Iterator<V>(){
		            private int pos=0;
		            public boolean hasNext() {
		               return a.length>pos;
		            }
	
		            public V next() {
		               return a[pos++];
		            }
	
		            public void remove() {
		                throw new UnsupportedOperationException("Cannot remove an element of an array.");
		            }
				};

	        };
	        
			@Override
	        public String toString(){
	        	if(a==null||a.length==0){return a==null?"null":"[]"; }
	        	StringBuilder sb = new StringBuilder("[");
	        	for(V o: a){
	        		sb.append(String.valueOf(o)).append(",");	        		
	        	}
	        	sb.setLength(sb.length()-1);
	        	return sb.append(']').toString();
	        }
	    };
	}
	
	public static void main(String args[]){
		System.out.println("[{test2:543}]".equals( Wrapper.iterate(new String[]{"{test2:543}"}).toString()));
	}

}