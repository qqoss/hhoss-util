package com.hhoss.lang;

import java.util.Arrays;

public class BizException extends RuntimeException {
	private static final long serialVersionUID = 2549897061162751483L;
	/** comma split key, eg: ex.jixin.page.none=http404 */
	private Long sid;
	private String code;
	private String name;
	private String text;
	private Object[] params;
	
	/** comma split key */
	public BizException(String name){
		super(name);
		this.name=name;
	}
	
	/** comma split key */
	public BizException(String code, Object... params){
		super(code,(params!=null)&&(params.length>0)&&(params[params.length-1] instanceof Throwable)?(Throwable)params[params.length-1]:null);
		this.params=params;
	}
	
	public @Override String getMessage(){
		if(text!=null){return text;}
		if(code==null){
			if(sid>0){
				//todo code from sid
			}else if(name!=null){
				//todo code from name
			}
		}
		return text=String.format(code, params);
		
	}
	public void setParam(int idx, Object param){
		if( params==null ){
			params=new Object[idx+1];
		}
		if( params.length<=idx ){
			params=Arrays.copyOf(params, idx+1);
		}
		params[idx]=param;
	}
}
