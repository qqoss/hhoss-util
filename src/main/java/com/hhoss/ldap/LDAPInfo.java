package com.hhoss.ldap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.hhoss.jour.Logger;
import com.hhoss.util.Base64;
import com.hhoss.util.Digester;
import com.hhoss.util.HexCoder;
import com.hhoss.util.thread.ThreadPool;


public class LDAPInfo {
	private static final Logger logger = Logger.get();
	private static final String ldapFactory="com.sun.jndi.ldap.LdapCtxFactory";
	private Hashtable<String,String> env;
    private DirContext context;
    
    public LDAPInfo(String url){
    	env= getLdapEnv(url);
       	//host ldap://ldap.credit2go.cn:389/ou=users,dc=credit2go,dc=cn
       	//url = ldap://ldap.credit2go.cn:389/ou=users,dc=credit2go,dc=cn
    }
    
    public LDAPInfo(String url,String uid,String pwd){
    	env= getLdapEnv(url);
        env.put(Context.SECURITY_PRINCIPAL, uid );
        env.put(Context.SECURITY_CREDENTIALS, pwd);
    }
    
    public LDAPInfo(Hashtable<String,String> env){
    	this.env = env;
    }

    private DirContext getCtx() {
        if (context == null ) {
    	    if(!env.containsKey(Context.INITIAL_CONTEXT_FACTORY)){
    	        env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
    	    }
    	    if(!env.containsKey(Context.PROVIDER_URL)){
    	        env.put(Context.PROVIDER_URL, "ldap://localhost:389");
    	    }
    	    if(!env.containsKey(Context.SECURITY_AUTHENTICATION)){
    	        env.put(Context.SECURITY_AUTHENTICATION, "simple");
    	    }
            context = getDirContext(env) ;
        }
         return context;
    }
    
    public static Hashtable<String,String> getLdapEnv(String providerUrl){
        Hashtable<String,String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
        env.put(Context.PROVIDER_URL, providerUrl);
        return env;
    }
    
    public void close(){
        try {
            if(context!=null)context.close();
        } catch (NamingException ex) {
        	 logger.warn("exceptionwhen close ldap connection.",ex);
        }
    }
    
    public static DirContext getDirContext(Hashtable<String,String> env){
    	DirContext ctx = null ;
        try {
           // Hashtable env = new Hashtable();
            //env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            //env.put(Context.PROVIDER_URL, "ldap://localhost:389/" + root);
           // env.put(Context.PROVIDER_URL, "ldap://ldap.credit2go.cn:389/" + root);
           // env.put(Context.SECURITY_AUTHENTICATION, "simple");
           // env.put(Context.SECURITY_PRINCIPAL, "cn=root,dc=credit2go,dc=cn" );
           // env.put(Context.SECURITY_PRINCIPAL, "uid=kjzheng@credit2go.cn" );
            // env.put(Context.SECURITY_CREDENTIALS, "credit2go");
            // env.put(Context.SECURITY_CREDENTIALS, "Abcd1234");
           // 链接ldap
        	ctx = new InitialDirContext(env);
        	logger.info("[{}]连接ladp[{}]成功.",env.get(Context.SECURITY_PRINCIPAL),env.get(Context.PROVIDER_URL));
        } catch (javax.naming.AuthenticationException e) {
        	logger.warn("[{}]认证ladp[{}]失败.",env.get(Context.SECURITY_PRINCIPAL),env.get(Context.PROVIDER_URL));
        } catch (Exception e) {
        	logger.info("[{}]连接ladp[{}]出错.",env.get(Context.SECURITY_PRINCIPAL),env.get(Context.PROVIDER_URL));
        	logger.warn("出错信息：{}", e);
        }
        return ctx;

    }
    
    public String getPWD(byte[] ldapValues){
    	String hash = "";
    	String ldappw = new String(ldapValues);
        if (ldappw.startsWith("{md5}")) {
        	hash = ldappw.substring(4);
        } else if (ldappw.startsWith("{SHA}")) {
        	hash = ldappw.substring(5);
        } else if (ldappw.startsWith("{SSHA}")) {
        	hash = ldappw.substring(6);
        }
        return HexCoder.toHex(Base64.decodeString(hash));
    }

    public static boolean verifyMD5(String ldappw, String inputpw) {
        // 取出加密字符
        if (ldappw.startsWith("{md5}")) {
            ldappw = ldappw.substring(4);
        }
        return ldappw.equals(Digester.md5Base64(inputpw));
    }
    
    public static boolean verifySHA(String ldappw, String inputpw) {
        // 取出加密字符
        if (ldappw.startsWith("{SSHA}")) {
            ldappw = ldappw.substring(6);
        } else if (ldappw.startsWith("{SHA}")) {
            ldappw = ldappw.substring(5);
        }
       // 解码BASE64
        byte[] ldappwbyte = Base64.decodeString(ldappw);
        byte[] code;
        byte[] salt;
        // 前20位是SHA-1加密段，20位后是最初加密时的随机明文
        if (ldappwbyte.length <= 20) {
        	code = ldappwbyte;
        	salt = new byte[0];
        } else {
        	code = new byte[20];
        	salt = new byte[ldappwbyte.length - 20];
            System.arraycopy(ldappwbyte,  0, code, 0, 20);
            System.arraycopy(ldappwbyte, 20, salt, 0, salt.length);
        }
        return ldappw.equals(Digester.sha1Base64(code, salt));
      }
    
    //("ldap://localhost:389","uid=credit2go@credit2go.cn,ou=users,dc=credit2go,dc=cn","abcd1234")
   public static boolean auth(String url,String usr, String pwd) {
	   try{
		   DirContext ctx=	new LDAPInfo(url,usr,pwd).getCtx() ;
		   if( ctx!=null ){
			   ctx.close();
			   return true;
		   }
	   }catch(Exception e){}
	   return false;
    }
    //("ou=users,dc=credit2go,dc=cn","uid=credit2go@credit2go.cn","abcd1234")
   public boolean verify(String usr, String pwd) {
	   return verify("",usr, pwd);
   }
   
    public boolean verify(String base, String usr, String pwd) {
        boolean ret = false;
        DirContext ctx = getCtx();
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            NamingEnumeration<SearchResult> nes = ctx.search(base, usr, constraints); // 查询所有用户
            if(nes!=null)while (nes.hasMoreElements()) {
                    SearchResult si = nes.nextElement();
                    Attributes attrs = si.getAttributes();
                    if (attrs == null) {
                    	logger.warn("{} hasn't any attribute.",si.getName());
                    } else {
                        Attribute attr = attrs.get("userPassword");
                        Object o = attr.get();
                        byte[] s = (byte[]) o;
                        String pwd2 = new String(s);
                        ret = LDAPInfo.verifyMD5(pwd2, pwd);
                        logger.info("auth {} result:{}",usr,ret);
                        return ret;
                    }
            }
        } catch (NamingException ex) {
            logger.warn("exception",ex);
        }
        logger.info("auth result:false");
        return false;
    }
    
    public List<Map<String,Object>> getAllUsers(String base) {
    	List<Map<String,Object>> list = new ArrayList<>();
        DirContext ctx = getCtx();
        try {
            NamingEnumeration<SearchResult> nes = ctx.search(base, "(&(objectclass=person)(uid=*@credit2go.cn))" , new SearchControls()); // 查询所有用户
            if(nes!=null)while(nes.hasMore()) {
            	logger.info("ldap object:{}",nes);
                SearchResult sr = nes.next();
                logger.info("name:" + sr.getName());
                Attributes attrs = sr.getAttributes();
                if (sr.getAttributes() == null) {
                	logger.warn("No attributes");
                } else {
               	NamingEnumeration<? extends Attribute> nea = attrs.getAll();
                	Map<String,Object> map = new HashMap<>();
                    if(nea!=null)while(nea.hasMore()) {
 	                     Attribute attr = nea.next();
 	                     map.put(attr.getID(), attr.get());
 	                   // logger.info("attr {}={}",attr.getID(), attr.get());
                    }
                    list.add(map);
                    Object attrv = attrs.get("userpassword").get();
                	logger.info("uid:{};pwd:{}",attrs.get("uid").get(), getPWD((byte[])attrv));
                }
            }
        } catch (NamingException ex) {
            logger.warn("exception",ex);
        }
        return list;
    }
   
    public boolean addUser(String usr, String pwd) {
        DirContext ctx = getCtx();
        try {
            BasicAttributes attrsbu = new BasicAttributes();
            BasicAttribute objclassSet = new BasicAttribute("objectclass");
            objclassSet.add("person");
            objclassSet.add("top");
            objclassSet.add("organizationalPerson");
            objclassSet.add("inetOrgPerson");
            attrsbu.put(objclassSet);
            attrsbu.put("sn", usr);
            attrsbu.put("uid", usr);
            attrsbu.put("userPassword", pwd);
            ctx.createSubcontext("uid=" + usr, attrsbu);
             return true;
        } catch (NamingException ex) {
            logger.warn("exception",ex);
        }
        return false;
    }
    
    /**
     * @param userDN  relative the ldap providerUrl
     * @param pwd the plain pwd to be encrypt
     * @return
     */
    public boolean updatePWD(String userDN, String pwd) {
    	return update(userDN,"userPassword", "{md5}"+Digester.md5Base64(pwd));
    }
    	   
    /**
     * @param objDN  DN is relative the ldap providerUrl
     * @param attr the attribute name
     * @param value the value of the attribute,pwd should be encrypted
     * @return  true if success
     */
    public boolean update(String objDN, String attr,String value) {
        boolean success = false;
        DirContext ctx = getCtx();
        try {
            ModificationItem[] modificationItem = new ModificationItem[1];
            modificationItem[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attr, value));
            ctx.modifyAttributes(objDN, modificationItem);
            return true;
        } catch (NamingException ex) {
            logger.warn("exception",ex);
        }
        return success;
    }
    
    public static void main(String[] args) throws NamingException {
    	//DirContext ctx = getCtx();
    	//ctx.destroySubcontext("cn=" + account);
    	String provider = "ldap://ldap.credit2go.cn:389/dc=credit2go,dc=cn";
    	//LDAPInfo ldap = new LDAPInfo(provider);
       	new LDAPInfo(provider,"uid=kjzheng@credit2go.cn,ou=users,dc=credit2go,dc=cn","Abcd1234").getCtx().close(); ;
       	LDAPInfo ldap = new LDAPInfo(provider,"cn=root,dc=credit2go,dc=cn","credit2go");
      	//ldap.authenticate("ou=users","uid=kjzheng@credit2go.cn","Abcd1234"); 
    	//ldap.authenticate("ou=users","uid=credit2go@credit2go.cn","abcd1234");
     	
    	ThreadPool.run(ldap, "updatePWD", "uid=kjzheng@credit2go.cn,ou=users","Abcd1234");
    	List<Map<String,Object>> list = ldap.getAllUsers("ou=users");    
    	//System.out.println(list.size());
    }

} 