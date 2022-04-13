package com.hhoss.conf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.hhoss.boot.App;
import com.hhoss.jour.Logger;
import com.hhoss.jour.LoggerFactory;
import com.hhoss.lang.Judge;
import com.hhoss.util.Enumerations;
import com.hhoss.util.HMap;
import com.hhoss.util.Strings;
import com.hhoss.util.token.PresetProvider;
import com.hhoss.util.token.TokenProvider;
import com.hhoss.util.token.Tokens;


/**
 * res tree node, as a holder / bucket
 * @author kejun
 * old name PropsNode
 */
public final class ResHolder extends Properties {
	private static final long serialVersionUID = 1359257876422093067L;
	private static final Logger logger = Logger.get();
	
	protected static final String PARENT_NAME = "res.parent.name";
	protected static final String LOCATE_NAME = "res.locate.name";// the res unique name 
	protected static final String LOCATE_PATH = "res.locate.path";// the res real full path 
	protected static final String LOCATE_RSID = "res.locate.rsid";// the resource id, refer self id
	
	private static final String LOOKUP ="res.holder.lookup";
	private static final String IGNORE ="res.holder.ignore";
	private static final String HASHER ="hasher";
	private static final String LOCALE ="locale";
	private static final String PARENT ="parent";	

	//@See com.hhoss.lang.Judge
	private static final String DEF_TRUE_KEY="var.token.true.values";
	private static final String DEF_FAIL_KEY="var.token.fail.values";
	/** define the value split delimiters for getList, if not set use default DEFAULT_DELIMS=",;| \t\r\n" */
	private static final String DEF_DELIM_KEY="var.token.delimiters";
	private static final String DEF_DELIM_VALUES=",;| \t\r\n";

	private static final TokenProvider NULL = new PresetProvider();
	private final HMap<TokenProvider> providers = new HMap<>();
	/**the holder priority order: locale> cacher> holder> parent> system */
	private String[] lookups = {LOCALE,"cacher",HASHER,PARENT,"rooter","system"};
	private String[] ignores ;
	private String name;
	private long msid;
	/** if get value from current map directly, will give warn */
	//private String logLevel="NA";
	private int logLevel=-1<<1;// -2; Integer.MIN_VALUE;

	public ResHolder(String name){setName(name);}
	
	public ResHolder(String name, Properties parentProps) {
		setFather(parentProps);
		setName(name);
	}

	/** null if res.{name}.ignore=1 else then search provider
	 * @param holderName
	 * @return TokenProvider for given lookup name
	 */
	private TokenProvider provider(String name){
		TokenProvider tp = providers.get(name);
		if( tp==null){
			//options switch in properties to disable some lookup 
			if(!Judge.contains(ignores, name)){
				tp=TokenProvider.from(name, PARENT.equals(name)?defaults:this);
			}
			providers.put(name, tp=(tp==null)?NULL:tp);
		}
		return tp;
	}
	
	@SuppressWarnings("unused")
	private TokenProvider provider_old(String name){
		TokenProvider tp = providers.get(name);
		//options switch in properties to disable some lookup every time when get property
		if( tp==null && !"1".equals(get("res."+name+".ignore"))){
			tp=TokenProvider.from(name, PARENT.equals(name)?defaults:this);
			providers.put(name, tp);
		}
		return tp==null?NULL:tp;
	}

	/*
	 * the res bundle unique name 
	 */
	public String getName(){
		return name==null?(String)get(LOCATE_NAME):name;
	}

	/*
	 *  the bundle meta sid, myself sid
	 */
	public long getRsid(){		
		if(msid>0 || msid<-3){//has get msid ok, or failure 3 times;
			return msid;
		}		
		String s= (String)get(LOCATE_RSID);
		if(s==null||s.trim().length()==0){
			msid--;
		}else try{
			msid = Long.parseLong(s);
		}catch(NumberFormatException e){
			logger.debug("bundle sid setting[{}={}] error.",LOCATE_RSID,s);
			msid=-9; //has value,but wrong directly; 
		}
		return msid;
	}

	protected void setName(String name){
		this.name=name;
	    setProperty(LOCATE_NAME,name);
	}

	protected void setFather(Properties parentProps) {
		this.defaults = parentProps;
		if (parentProps != null) {
			String parentName = (parentProps instanceof ResHolder)?	((ResHolder)parentProps).getName()
							  :(String)parentProps.get(LOCATE_NAME);
			if (parentName != null){
				setProperty(PARENT_NAME, parentName);
			}
		}
	}
	
	/**
	 * case insensitive <br />
	 * true if explicit set as list of tokens: eg. 1,Y,true,allow...  <br />
	 * false if not set or not the explicit values. <br />
	 * can set the tokens by var.token.true.values=...
	 * @param key
	 * @return boolean
	 */
	public boolean isTrue(String key) {
		String val= getProperty(key);
		if(val==null||(val=val.trim()).isEmpty()){return false;}
		String[] tks = getArray(DEF_TRUE_KEY); //TODO:cache ?
		if(tks.length>0){
			for(String tk:tks){	if(val.equalsIgnoreCase(tk)){return true;} }
			return false;
		}else{
			return Judge.isTrue(val);
		}
	}

	/**
	 * case insensitive <br />
	 * true if explicit set as list of tokens: eg. 0,N,F,false,deny....  <br />
	 * false if not set or not the explicit values.
	 * can set the tokens by var.token.false.values=...
	 * @param key
	 * @return boolean
	 */
	public boolean isFail(String key) {
		String val= getProperty(key);
		if(val==null||(val=val.trim()).isEmpty()){return false;}
		String[] tks = getArray(DEF_FAIL_KEY); //TODO:cache ?
		
		if(tks.length>0){
			for(String tk:tks){	if(val.equalsIgnoreCase(tk)){return true;} }
			return false;
		}else{
			return Judge.isFail(val);
		}
	}
	
	/**
	 * using configured val if config a valid boolean value, otherwise using the default param
	 * @param key of the property
	 * @param def default value if not config or config wrong
	 * @return boolean
	 */
	public boolean getBool(String key,boolean def) {
		boolean nt = !isTrue(key);
		boolean nf = !isFail(key);
		if(nt&&nf){return def;}
		return !nt;
	}	
	
	/**
	 * true when the value is not set or set blank
	 * @param key of the prop
	 * @return bool
	 */
	public boolean isEmpty(String key) {
		String val= getProperty(key);
		return val==null||val.trim().isEmpty();
	}
	
	/**
	 * @param key of the prop property
	 * @param def default value if not config
	 * @return String 
	 */
	public String getString(String key,String def) {
		String v = getProperty(key);
		return v==null?def:v;
	}
	
	/**
     * Accepts decimal, hexadecimal, and octal numbers given by the following grammar:
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i>
     * <p>
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
	 * @param key of the prop
	 * @return Long
	 */
	public Long getLong(String key) {
		String nm= getProperty(key);
		return (nm==null||nm.trim().length()==0)?null:Long.decode(nm);
	}
	/**
	 * @param key of the property
	 * @param def default value if not config
	 * @return long 
	 */
	public long getLong(String key,long def) {
		Long v = getLong(key);
		return v==null?def:v;
	}

	/**
     * Accepts decimal, hexadecimal, and octal numbers given by the following grammar:
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i>
     * <p>
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
	 * @param key of the property
	 * @return Integer
	 */
	public Integer getInteger(String key) {
		String nm= getProperty(key);
		return (nm==null||nm.trim().length()==0)?null:Integer.decode(nm);
	}
	/**
	 * @param key of the property
	 * @param def default value if not config
	 * @return int 
	 */
	public int getInt(String key,int def) {
		Integer v = getInteger(key);
		return v==null?def:v;
	}
	
	/**
	 * support radix, 0x...
     * <dl>
     * <dt><i>HexSignificand:</i>
     * <dd>{@code 0x} <i>HexDigits<sub>opt</sub>
     *     </i>{@code .}<i> HexDigits</i>
     * <dd>{@code 0X}<i> HexDigits<sub>opt</sub>
     *     </i>{@code .} <i>HexDigits</i>
     * </dl>
	 * @param key of the property
	 * @return float
	 * @see Float#parseFloat(String s)
	 */
	public Float getFloat(String key) {
		String nm= getProperty(key);
		return (nm==null)?null:Float.parseFloat(nm);
	}
	/**
	 * @param key of the property
	 * @param def default value if not config
	 * @return float 
	 */
	public float getFloat(String key,float def) {
		Float v = getFloat(key);
		return v==null?def:v;
	}
	
	/**
	 * support radix, 0x...
     * <dl>
     * <dt><i>HexSignificand:</i>
     * <dd>{@code 0x} <i>HexDigits<sub>opt</sub>
     *     </i>{@code .}<i> HexDigits</i>
     * <dd>{@code 0X}<i> HexDigits<sub>opt</sub>
     *     </i>{@code .} <i>HexDigits</i>
     * </dl>
	 * @param key of the property
	 * @return double
	 * @see Double#valueOf(String s)
	 */
	public Double getDouble(String key) {
		String nm= getProperty(key);
		return (nm==null)?null:Double.parseDouble(nm);
	}
	/**
	 * @param key of the property
	 * @param def default value if not config
	 * @return double 
	 */
	public double getDouble(String key,double def) {
		Double v = getDouble(key);
		return v==null?def:v;
	}

	/**
	 * the value as list by delimiters "var.list.split.delimiters" default as ",;| \t\n" <br />
	 * or values which key matched prefix{[.|_]?\d+} <br />
	 * the max matches entry is 1000 for prefix; <br />
	 * value item only support 10 radix, not support 16 radix
	 * @param key the key of property or prefix of property Array
	 * @param klass the class of element type, should be privative
	 * @return List list is not null and each item is not null
	 */
	public <T> List<T> getList(String key, Class<T> klass) {
		List<T> list = new ArrayList<>();
		for(String s:getList(key)){
			list.add(Strings.convert(s, klass));
		}
		return list;
	}
	
	/**
	 * the value as list by delimiters "var.list.split.delimiters" default as ",;| \t\n" <br />
	 * or values which key matched prefix{[.|_]?\d+} <br />
	 * the max matches entry is 1000 for prefix; <br />
	 * value item only support 10 radix, not support 16 radix
	 * @param key the key of property or prefix of property Array
	 * @return List list is not null and each item is not null
	 * @see org.springframework.context.ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS=",; \t\n"
	 */
	public List<String> getList(String key) {
		if(key==null||key.trim().isEmpty()){
			throw new IllegalArgumentException("Property key can't be empty.");
		}		
		List<String> list = new ArrayList<>();
		String val = getProperty(key);
		int max = 0;
		if(val!=null&&(val=val.trim()).length()>0){
			if(val.matches("#\\d+")){
				max=Integer.parseInt(val.substring(1));
			}else{
				list=split(val,null,false);
			}
		}
		if(list.isEmpty()){
			if(max==0){max=999;}
			String prefix = normalize(key);
			for(int i=0;i<=max;i++){
				String v = getProperty(prefix+i);
				if(i>0&&v==null){break;}
				if(v!=null){list.add(v.trim());}
			}
		}
		return list;
	}

	/**
	 * @param key
	 * @return the array of config, allow null/empty item
	 */
	public String[] getArray(String key){
		if(key==null||key.trim().isEmpty()){
			throw new IllegalArgumentException("Property key can't be empty.");
		}		
		String val = getProperty(key);
		String[] values = null;
		if(val==null||(val=val.trim()).length()==0){
			List<String> list = new ArrayList<>();
			String prefix = normalize(key);
			String vi = getProperty(prefix+0);
			if(vi!=null){list.add(vi);}
			for(int i=1;i<999;i++){
				vi = getProperty(prefix+i);
				if(vi==null){break;}else{list.add(vi);}
			}
			values = list.toArray(new String[list.size()]);
		}else if(val.matches("#\\d+")){
			int max=Integer.parseInt(val.substring(1));
			String prefix = normalize(key);
			List<String> list = new ArrayList<>();
			for(int i=0;i<=max&&i<999;i++){
				list.add(getProperty(prefix+i));
			}
			values = list.toArray(new String[list.size()]);
		}else{
			values = split(val,null,true).toArray(new String[]{});
			//String delims=getProperty(KEY_FOR_DELIMS, DEFAULT_DELIMS);
			//values = val.split("["+delims+"]"); 
		}
		return values;
	}
	
	private static String normalize(String prefix){
		if(prefix.endsWith(".")||prefix.endsWith("_")){
			return prefix;
		}
		return prefix+=".";
	}


	/**
	 * @Override
	 * when use (Map)get(), we still return the value from HashTable, don't
	 * recurse and replace, so comment; public synchronized String get(Object
	 * key) { return getProperty( String.valueOf(key) ); }
	 */

	@Override
	public synchronized Object put(Object key, Object val) {
		if( key==null||val==null ){
			logger.warn("ignore put property: {}={}",key,val);
			return val;
		}else if(LOOKUP.equals(key)){
			lookup((String)val);
		}else if(IGNORE.equals(key)){
			ignore((String)val);
		}else if("::".equals(val)||">>".equals(val)){//allow quick group in properties.
			logger.info("start group[{}] properties.",key);
			prefix=(String)key; //end with '.' or not, by user
			return val;
		}else if("==".equals(val)||"<<".equals(val)){//finish quick group.
			logger.info("close group[{}] properties.",key);
			 prefix=null;
			return val;
		}
		return (String)super.put(prefix==null?key:(prefix+key),((String)val).trim());
	}
	private String prefix;

	@Override
	public String getProperty(String key) {
		return getProperty(key, true);
	}

	/**
	 * @param qkey
	 * @param subst need subst if not provider this parameter means true
	 *            to replace the holder of ${...} with substitution;
	 * @return val 
	 */
	public String getProperty(String qkey, boolean subst) {
		String key = getProvider().get(qkey);		
		String val = getOriginal(key);
		return (subst && val!=null)?convert(key,val):val;		
	}
	
	private String convert(String key, String val) {
		if( key.startsWith("crypto.")||
			key.endsWith(".secret")||
			key.endsWith(".cipher")||
			val.startsWith("$crypto$")||
			val.startsWith("$cipher$")||
			val.startsWith("$secret$")) {
			val = TokenProvider.from("cipher","").get(val);//TODO implements Algorithm as 2nd param
		}
		return getProvider().get(val);
	}
	
	private TokenProvider provider;
	private TokenProvider getProvider(){
		if( provider==null ){
			provider = Tokens.from(this);
		}
		return provider;
	}

	private static final String FORMAT = "res[{}/{}]->[{}={}]";	
	protected final String getOriginal(String key) {
		//checkLocale();
		String v;
		for(String lp:lookups){
			if((v=provider(lp).get(key))!=null){
				if( level()>-1 ){
					logger.log(null, getName(),level(),FORMAT, new String[]{getName(),lp,key,v}, null);
				}
				return v;
			}
		}
		return null;
	}

    private int level() {
    	if( logLevel<-1 ){
    		logLevel = LoggerFactory.level((String)get("spi.logger.level"));
    	}   
    	return logLevel;
     }


	/**
	 * Load specified properties file and construct it as Properties
	 * 
	 * @param ResourceBundle
	 *            bundle
	 * @return Properties
	 */
	/*
	protected synchronized ResHolder load(ResourceBundle bundle) {
		Enumeration<String> keyList = bundle.getKeys();
		String keyName = null;
		while (keyList.hasMoreElements()) {
			keyName = keyList.nextElement();
			put(keyName, bundle.getString(keyName));
		}
		return this;
	}
	public synchronized void load(InputStream is) throws IOException {
		super.load(is);
		//lookupIgnores();
	}
	public synchronized void load(Reader rd) throws IOException {
		super.load(rd);
		//lookupIgnores();
	}
	
	private void lookupIgnores(){
		String val = (String)get(LOOKUP);
		if(val!=null&&val.length()>0){
			lookup((String)val);
		}
		val = (String)get(IGNORE);
		if(val!=null&&val.length()>0){
			ignore((String)val);
		}
	}
	*/
	protected synchronized ResourceBundle loadBundle(ClassLoader loader, Locale locale) {
		return ResourceBundle.getBundle(getName(), locale , loader, new ResourceControl(this));
	}

	/**
	 * @override save properties with sorted order.
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void store(OutputStream out, String comments) throws IOException {
		TreeMap map = new TreeMap(this);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		bw.write("#### BOM holder line, generated by ResHolder. ");
		bw.write(new Date().toString()); bw.write("####"); bw.newLine();
		if (comments != null){bw.write("##");bw.write(comments);}		
		for(Entry<String,String> ent:(Set<Entry>)map.entrySet()){
			bw.newLine();  bw.write((String)ent.getKey()); 
			bw.write("="); bw.write((String)ent.getValue());
		}
		bw.flush();
	}
	
	/**
	 * @param comments
	 * @throws IOException
	 */
	public void store(String comments) throws IOException {
		String name = getName().replace('.',File.separatorChar)+".properties";
		String path = getProperty(App.APP_RUNTIME_CONF);
		File file = new File(path,name);
		file.getParentFile().mkdirs();//.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		store(out,comments);
		out.close();
	}
	
	@Override
    public Enumeration<String> propertyNames() {
    	return Enumerations.of(stringPropertyNames());
    }
    
	@Override
    public Set<String> stringPropertyNames() {
    	Set<String> set = super.stringPropertyNames();
    	Set<String> lks = provider(LOCALE).keySet();
    	if(lks!=null && !lks.isEmpty()){
    		set = new HashSet<String>(set);// super set can't modify;
    		set.addAll(lks);
    	}
        return set;
    }
	
	/**
	 * @param prefix, keys start with
	 * @param subst true will replace the placeHolder to real value.
	 * @return properties which include the sub keys
	 */
	public Properties subHolder(String prefix, boolean subst){
		if(prefix==null||prefix.length()<1){return null;}
		String head=prefix.endsWith(".")?prefix:prefix+".";
		Properties sub= subst?new Properties():new ResHolder(this.getName()+"-"+prefix,this);
		for(String name : stringPropertyNames()){
			if(name.startsWith(head)&&name.length()>head.length()){
				sub.setProperty(name.substring(head.length()), subst?getProperty(name):"${"+name+"}");
			}
		}
		return sub;
	}
	
	public ResHolder let(String key, String val){
		setProperty(key, val);			
		return this;
	}
	
	/**
	 * redefined the lookup for current resources holder, <br/>
	 * please NOTE, it will affect the value retrieve sources!<br/>
	 * if not set, default is {@link #lookups}
	 * @param lookups 
	 * @return this
	 */
	public ResHolder lookup(String lookups){
		List<String> names = split(lookups,DEF_DELIM_VALUES,false);
		if(names!=null && !names.isEmpty()){
			this.lookups = names.toArray(new String[]{});
			logger.info("lookups of [{}] changed to {}",getName(),names);
		}
		return this;
	}
	
	public ResHolder ignore(String ignores){
		List<String> names = split(ignores,DEF_DELIM_VALUES,false);
		if(names!=null && !names.isEmpty()){
			this.ignores = names.toArray(new String[]{});
			logger.info("ignores of [{}] changed to {}",getName(),names);
		}
		return this;
	}
		
	private List<String> split(String val, String delim, boolean nullable){
		if(val==null){return null;}
		String delims=(delim==null)?getProperty(DEF_DELIM_KEY, DEF_DELIM_VALUES):delim;
		StringTokenizer st = new StringTokenizer(val,delims);
		List<String> list = new ArrayList<>();
		while(st.hasMoreTokens()){
			String v = st.nextToken();
			if(nullable){
				list.add(v);
			}else if(v!=null && v.trim().length()>0){
				list.add(v.trim());
			}
		}
		return list;
	}
	
	/* @see org.springframework.context.support.ResourceBundleMessageSource.MessageSourceControl */
	static class ResourceControl extends ResourceBundle.Control {
			private ResHolder owner;
			ResourceControl(ResHolder owner){
				this.owner=owner;
			}
			public @Override ResourceBundle newBundle(String baseName, Locale locale, String format, final ClassLoader loader, final boolean reload)
					throws IllegalAccessException, InstantiationException, IOException {
				// Special handling of default encoding
				if(format.equals("java.properties")) {
					final String resName = toResourceName(toBundleName(baseName, locale), "properties");
					//final ResHolder self = ResHolder.this;
					InputStream stream = null;
					if (reload) {
						java.net.URL url = loader.getResource(resName);
						if (url != null) {
							java.net.URLConnection connection = url.openConnection();
							if (connection != null) {
								connection.setUseCaches(false);
								stream = connection.getInputStream();
							}
						}
					}else {
						stream = loader.getResourceAsStream(resName);
					}
					if (stream != null)try{
						//owner.load(new InputStreamReader(stream, "UTF-8"));
						owner.load(stream);
					}finally {
						stream.close();
					}
					
					return new ResourceBundle(){							
						protected @Override Object handleGetObject(String key) {
							return owner.get(key);
						}
						public @Override Enumeration<String> getKeys() {
							return (Enumeration<String>)owner.keySet();
					       // return owner.propertyNames();
						}
					};
				}else {
					// Delegate handling of "java.class" format to standard Control
					return super.newBundle(baseName, locale, format, loader, reload);
				}
			}

	}

}
