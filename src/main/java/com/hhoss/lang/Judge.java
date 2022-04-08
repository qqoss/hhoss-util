package com.hhoss.lang;

import java.util.Collection;

public class Judge {
	//sync with res.app.root; @see com.hhoss.conf.ResHolder
	private static final String[] DEF_TRUE_VALUES = {"1","Y","T","A","S","R","ok","on", "yes", "true","pass","allow","right","accept","enable","enabled","success"};
	private static final String[] DEF_FAIL_VALUES = {"0","N","F","D","C","W","no","off","deny","fail","false","null","wrong","error", "cancel","disable","disabled","failure","-1"};

	public static <T> boolean contains(Collection<T> list, T obj){
		if(list==null || obj==null){return false;}
		
		for(T s : list) if(s==obj || s.equals(obj)) {
			return true;
		}else if(s instanceof String && s.toString().equalsIgnoreCase(obj.toString().trim())){
			return true;
		}
		
		return false;
	}		

	public static <T> boolean contains(T[] arr, T val){
		if(arr==null||val==null){ return false; }
		
		for(T obj : arr) if( obj==val || obj.equals(val) ){
			return true;
		}else if(obj instanceof String && obj.toString().equalsIgnoreCase(val.toString().trim())){
			return true;
		}
		
		return false;
	}		

	/**
	 * @param obj
	 * @return true if obj.equalsIgnoreCase in {@link #DEF_TRUE_VALUES}
	 */
	public static boolean isTrue(Object obj){
		return contains(DEF_TRUE_VALUES,obj);
	}	
	/**
	 * @param obj
	 * @return true if obj.equalsIgnoreCase in {@link #DEF_TRUE_VALUES}
	 */
	public static boolean isPass(Object obj){
		return contains(DEF_TRUE_VALUES,obj);
	}	
	
	/**
	 * @param obj
	 * @return true if obj.equalsIgnoreCase in {@link #DEF_FAIL_VALUES}
	 */
	public static boolean isFail(Object obj){
		return contains(DEF_FAIL_VALUES,obj);
	}		
	
	public static boolean isDeny(Object obj){
		return contains(DEF_FAIL_VALUES,obj);
	}		
	
	public static boolean expect(Object obj, boolean val){
		return contains(val?DEF_TRUE_VALUES:DEF_FAIL_VALUES,obj);
	}		

	public static boolean expect(Object obj, Number val){
		if(obj==null||val==null){return val==obj;}
		double d1=((Number)val).doubleValue();
		double d2=(obj instanceof Number)?((Number)obj).doubleValue():Double.parseDouble(obj.toString());
		return Double.doubleToLongBits(d1-d2)==0;
		//return !(Math.abs(d1-d2)>0);
	}		

	/**
	 * @param obj
	 * @param expect
	 * @return if and only obj and val has same meaning/explain with expect.
	 */
	public static boolean expect(Object obj, Object val){
		if(obj==null||val==null){return val==obj;}
		
		boolean br=false;
		if( val instanceof Boolean ){
			br = expect(obj,(boolean)val);
		}else if( obj instanceof Boolean ){
			br = expect(val,(boolean)obj);
		}else if( val instanceof Number ){
			br = expect(obj,(Number)val);
		}else if( obj instanceof Number ){
			br = expect(val,(Number)obj);
		}else if(val instanceof String || obj instanceof String){
			br = val.toString().trim().equalsIgnoreCase(obj.toString().trim());
		}else if(obj.getClass().isAssignableFrom(val.getClass())){
			br = obj.equals(val);
		}else if(val.getClass().isAssignableFrom(obj.getClass())){
			br = val.equals(obj);
		}else{
			br = obj==val||obj.equals(val);
		}		
		return br;
	}		
	
	/*
	public static boolean isFalse(Object obj){
		if(obj==null){return false;}
		Boolean b = Boolean.TRUE;
		if(obj instanceof Boolean){
			b = BooleanUtils.toBooleanObject((Boolean)obj);
		}else if(obj instanceof Number){
			b = BooleanUtils.toBooleanObject(((Number)obj).intValue());
		}else if(obj instanceof String){
			b = BooleanUtils.toBooleanObject((String)obj);
		}
		
		return BooleanUtils.isFalse(b);
	}
	*/
	
	public static void main(String[] args) {
		System.out.println(isTrue("True"));
		System.out.println(isTrue(Boolean.valueOf("True")));
		System.out.println(isTrue(true));
		System.out.println(isTrue(1));
		System.out.println(isFail("True"));
		System.out.println(isFail(Boolean.valueOf("FAlse")));
		System.out.println(isFail(true));
	}


}
