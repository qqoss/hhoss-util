package com.hhoss.conf;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import com.hhoss.util.token.TokenProvider;
import com.hhoss.util.token.Tokens;

public class ResLocale extends TokenProvider{
	private static final long serialVersionUID = 3687331906630666392L;
	//holder assign settle locale locate
	private static final String LOCALE_NAME = "res.locale.name";
	private static final String LOCALE_PATH = "res.locale.path";//the holder full path
	
	private static final ResHolder EMPTY = new ResHolder("EMPTY");
	private Map<?,?> holder ;
	private Map<?,?> locale ;
	
	public ResLocale(){	}
	public ResLocale( Map<?,?> holder){
		this.holder=holder;
	}
	
	@Override public String getName(){return "locale";}	
	
	@Override public String get(String key){
		checkLocale();
		return isEmpty()?null:(String)locale.get(key);
	}
	
	@Override public boolean isEmpty(){
		checkLocale();
		return locale==null||locale==EMPTY;
	}
	@Override public Set<String> keySet(){
		if(isEmpty()){return null;}		
		if(locale instanceof Properties){
			return ((Properties)locale).stringPropertyNames();
		}
		return (Set<String>)locale.keySet();
	}	

	/* subHolders separator */
	private static final char SS='|';
	private synchronized void checkLocale() {
		if( locale!=null ){return;}
		
		String locName = (String)holder.remove(LOCALE_NAME);		
		if( locName==null ){
			locName =(holder instanceof ResHolder)?((ResHolder)holder).getName()
					:(String)holder.get(ResHolder.LOCATE_NAME);
			if(locName!=null&&locName.indexOf(SS)<0){// point to a default locale
				locale=getLocale(locName.trim());//if not point locale using bundle locale
			}
		}else{// point locale by config
			locale=EMPTY;//avoid recurse loop;
			locName=Tokens.from(holder).get(locName);//Tokens.from(holder,false)?
			locale=EnvHolder.getProperties(locName);
		}
		if(locale==null){locale=EMPTY;}
		
	}
	
	/**
	 * @param baseName
	 * @return default machine locale bundle
	 */
	private Map<?,?> getLocale(String baseName){
		EnvKeeper kp = EnvHolder.getKeeper();
		ClassLoader cl = (kp==null)?getClass().getClassLoader():kp.getLoader();
		try{
			/*
			ResourceBundle rb = ResourceBundle.getBundle(baseName, Locale.getDefault(), cl);
			if(!Locale.ROOT.equals(rb.getLocale())){//same with base, not a real locale
				return new ResHolder(baseName+"/"+rb.getLocale()).load(rb);
			}
			//*/
			ResHolder res = new ResHolder(baseName);
			ResourceBundle rb = res.loadBundle(cl,Locale.getDefault());
			if(res.size()>1 && !Locale.ROOT.equals(rb.getLocale())){//same with base means not a real locale
				res.setName(baseName+SS+rb.getLocale());
				return res;
			}
			//*/
		}catch(Exception e){}
		return null;
	}

}
