package com.hhoss.util.crypto;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

import com.hhoss.boot.App;
import com.hhoss.jour.Logger;
import com.hhoss.util.coder.Base64;
import com.hhoss.util.coder.HexCoder;

/**
 * @author kejun
 * @since 2015-8-1
 */
public class RSAKeyUtil {
	
	private static final Logger logger = Logger.get();
	private PublicKey publicKey;
	private PrivateKey privateKey;

	/**
	 * @return key privateKey or publicKey if privateKey is null
	 */
	public Key getKey(){
		return privateKey==null?publicKey:privateKey;
	}
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	/**
	 * @param certFile
	 * @throws IOException
	 */
	public RSAKeyUtil(File certFile) throws IOException {
		try(InputStream is = new FileInputStream(certFile)){
			this.publicKey=getPublicKey(is);
		}
	}
	
	/**
	 * @param certPath relative to classPath
	 * @throws IOException
	 */
	public RSAKeyUtil(String certPath) throws IOException {
		try(InputStream is = getStream(certPath)){
			this.publicKey=getPublicKey(is);
		}
	}
	
	/**
	 * @param keysFile
	 * @param pwd
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public RSAKeyUtil(File keysFile,String pwd) throws GeneralSecurityException, IOException {
		try(InputStream is = new FileInputStream(keysFile)){
			getKeys(is,pwd);
		}
	}
	
	/**
	 * @param keysPath relative to classPath
	 * @throws IOException
	 */
	public RSAKeyUtil(String keysPath,String pwd) throws GeneralSecurityException, IOException {
		try(InputStream is = getStream(keysPath)){
			getKeys(is,pwd);
		}
	}
	
	private void getKeys(InputStream is,String pwd) throws GeneralSecurityException, IOException{
		char[] pwds = (pwd==null)?null:pwd.toCharArray();
		KeyStore ks = getKeyStore(is,pwds);
		String alias = getKeyAlias(ks);
		if(alias!=null){
			this.publicKey=ks.getCertificate(alias).getPublicKey();
			this.privateKey=(PrivateKey) ks.getKey(alias, pwds);
		}
	}
	

	/**
	 * 获取公钥 publickey:
	 * @param cert file pem file
	 * @return
	 * @throws IOException 
	 */
	public static PublicKey getPublicKey(String certPath) throws IOException {
		try(InputStream is = getStream(certPath)){
			return getPublicKey(is);
		}
	}

	/**
	 * @param pubKey base64Encoded
	 * @return
	 */
	public static PublicKey getPublicKeyBase64(String keyEncode) {
		return getPublicKey(Base64.decodeString(keyEncode));
	}

	/**
	 * @param cert Stream
	 * @return
	 */
	public static PublicKey getPublicKey(InputStream certStream) {
		try {
			if (certStream != null) {
				CertificateFactory cf = CertificateFactory.getInstance("X.509"); // 指定证书类型
				Certificate	cert = cf.generateCertificate(certStream); // 获取证书
				return cert.getPublicKey(); // 获得公钥
			}
		} catch (CertificateException e) {
			logger.warn("获取证书失败:{}",e.getMessage());
		}
		return null;
	}
	
	/**
	 * @param keyBytes
	 * @return
	 */
	public static PublicKey getPublicKey(byte[] keyBytes) {
		try {			
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("初始化加密算法时报错");
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			System.out.println("初始化公钥时报错");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从公钥数据取得公钥
	 * @param keyBytes only keyBytes, no header
	 * @return
	 */
	public static PublicKey getPublicKey1(byte[] keyBytes) {
		byte[] bX509PubKeyHeader = { 48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0 };
		byte[] bPubKey = new byte[keyBytes.length + bX509PubKeyHeader.length];
		System.arraycopy(bX509PubKeyHeader, 0, bPubKey, 0, bX509PubKeyHeader.length);
		System.arraycopy(keyBytes, 0, bPubKey, bX509PubKeyHeader.length, keyBytes.length);
		return	getPublicKey(keyBytes);
	}
	
	/**
	 * @param keyFile the path related APP_RUNTIME_CONF
	 * the file of RSA Private Key file without password
	 * @return PrivateKey
	 * @throws IOException
	 */
	public static PrivateKey getPrivateKey(String keyFile) throws IOException{
		return getPrivateKey(new File(App.getConfPath(),keyFile));
	}
	
	/**
	 * @param keyFile  the file of RSA Private Key file without password
	 * @return PrivateKey
	 * @throws IOException
	 */
	public static PrivateKey getPrivateKey(File keyFile) throws IOException{
	    StringBuilder keyEncode = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new FileReader(keyFile))){
			//BufferedReader br = new BufferedReader(new InputStreamReader(getStream(keyFile)));
		    String line = br.readLine();
		    while (line.charAt(0) != '-') {
		    	keyEncode.append(line);// + "\r";
		    	line = br.readLine();
		    	if(line==null){break;}
		    }
		}
	    return getPrivateKey(Base64.decodeString(keyEncode.toString()));
	}
	/**
	 * @param p12Path the pcks12 keystore file path in class path
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static PrivateKey getPrivateKey(String keysPath, String pwd) throws GeneralSecurityException, IOException {
		return getPrivateKey(getStream(keysPath), pwd);
	}


	public static PrivateKey getPrivateKeyBase64(String keyEncode) {
		return getPrivateKey(Base64.decodeString(keyEncode));
	}
	
	/**
	 * @param keyBytes PKCS8Encoded key bytes
	 * @return
	 */
	public static PrivateKey getPrivateKey(byte[] keyBytes) {
		try {			
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			logger.warn("初始化加密算法时报错:{}",e.getMessage());
		} catch (InvalidKeySpecException e) {
			logger.warn("初始化私钥时报错:{}",e.getMessage());
		}
		return null;
	}
	
	/**
	 * @param PKCS12 keystore inutstream 
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static PrivateKey getPrivateKey(InputStream is, String pwd) throws GeneralSecurityException, IOException {
		char[] pwds = pwd.toCharArray();
		KeyStore ks = getKeyStore(is,pwds);
		String alias = getKeyAlias(ks);
		if(alias!=null){
			return (PrivateKey) ks.getKey(alias, pwds);
		}
		return null;
	}
	
	/**
	 * @param private encode key inputstream 
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static PrivateKey getPrivateKey2(InputStream is) throws IOException  {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int count = 0;
		while ((count = is.read(buf)) != -1) {
			bout.write(buf, 0, count);
			buf = new byte[1024];
		}
		return getPrivateKey(bout.toByteArray());
	}
	
	public static String getPrivateKey3(InputStream is) throws Exception{
		StringBuffer privateBuffer=new StringBuffer();
			InputStreamReader inputReader = new InputStreamReader(is);
			BufferedReader bufferReader = new BufferedReader(inputReader);
			// 读取一行
			String line = "";
			while ((line=bufferReader.readLine())!=null) {
				privateBuffer.append(line);
			}
		return privateBuffer.toString();
	}

	
	private static InputStream getStream(String path){
		InputStream is = App.getResourceAsStream(path);
		if(is==null){
			try {
				is = new FileInputStream(path);
			} catch (FileNotFoundException e) {				
				e.printStackTrace();
			}
		}
		return is;
	}
	
	private static KeyStore getKeyStore(InputStream is, char[] pwds) throws IOException, GeneralSecurityException{
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(is,  pwds);
		is.close();
		//System.out.println("keystore type = " + ks.getType());
		return ks;
	}
	
	private static String getKeyAlias(KeyStore ks) throws KeyStoreException {
		Enumeration<String> enuml = ks.aliases();
		String keyAlias = null;
		if (enuml.hasMoreElements()) {
			keyAlias = (String) enuml.nextElement();
			//System.out.println("alias=[" + keyAlias + "]");
			if(ks.isKeyEntry(keyAlias)){
				return keyAlias;
			}
		}
		return null;
	}

	///////////////////////test method//////////////////////
	public static void testGenerateKeyPair() {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(1024,  new SecureRandom("credit2go".getBytes()));
			KeyPair keys = keygen.genKeyPair();
			PublicKey publicKey = keys.getPublic();
			PrivateKey privateKey = keys.getPrivate();

			System.out.println("publicKey : " + HexCoder.toHex(publicKey.getEncoded()));
			System.out.println("publicKey : " + Base64.encodeBytes(publicKey.getEncoded()));

			System.out.println("privateKey: " + HexCoder.toHex(privateKey.getEncoded()));
			System.out.println("privateKey: " + Base64.encodeBytes(privateKey.getEncoded()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testGenerateKeys() throws Exception {
		byte[] keyBytes1 = HexCoder.hexToBytes("30819f300d06092a864886f70d010101050003818d0030818902818100886a4ba7ad410db5e579244afefa34dbd42dbfa0aa9d9d7079940b95d1b4e8f8f2d598c45ace42fb5ccc15941300d4723f22acc90c222ae5631fe9a4c7e5a6d8cc5333da22404a4fd2ba0dd58e1e77be12b590077e6ac3ee30658619669760036b7721ad6229289b13a6801dfcf55fefa20b43fbdbbfbc3034a15bf9dc8b970f0203010001");
		PublicKey publicKey1 = getPublicKey(keyBytes1);
		System.out.println("PublicKey1: " + HexCoder.toHex(publicKey1.getEncoded()));
		System.out.println("PublicKey1: " +  Base64.encodeBytes(publicKey1.getEncoded()));

		byte[] keyBytes2 = Base64.decodeString("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGR1Tvv5OUwUuonkTgqbzN53+4wqB5mr+k6mMFKqUJ7Gu0t22urHBDC27zyARwMf45V/CRoRvmJCQi2VfntEuKkhAiSDjBz3AUuHrcpnSV0d+iGFSQ2IcbWVhI2MoUuWCiGFoRWbsQa5cUjztuXJfakvROcLYNhXTnGlCq83LFmQIDAQAB");
		PublicKey publicKey2 = getPublicKey(keyBytes2);
		System.out.println("PublicKey2: " + HexCoder.toHex(publicKey2.getEncoded()));
		System.out.println("PublicKey2: " +  Base64.encodeBytes(publicKey2.getEncoded()));

		byte[] keyBytes3 = Base64.decodeString("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMZHVO+/k5TBS6ieROCpvM3nf7jCoHmav6TqYwUqpQnsa7S3ba6scEMLbvPIBHAx/jlX8JGhG+YkJCLZV+e0S4qSECJIOMHPcBS4etymdJXR36IYVJDYhxtZWEjYyhS5YKIYWhFZuxBrlxSPO25cl9qS9E5wtg2FdOcaUKrzcsWZAgMBAAECgYEAsTKEdLJoKPPKMsom0gS/Z+Bwo2TEV4j1pmOV1MM0tTalVt7q1cTjmvc31AO3+7Ch+wtdQjiSH03DQaWtSrQ1IPINOpl22wiA5pS1iHSOJQtCIxqvAwoEs78E5D1/0TUs8O4P1SfZbpMabb0pLgFhtBQWGwvJN6zETSH/jUa1pNUCQQDjCikIh06qGA4Ws2l2H760n5FTzVXZhDMFZSqVEFsJa3ocumbdcQVPFE3eLBv3ysMv15MJ5JbtlpctSf0slrOHAkEA35H/UkNf+lR58nPZ5Wgu7tE3siFsou9RcNxIj2c5w4wj4gqyD8q3g+wZLUtvLZomlfMTn+a0MUz4luB0OC9F3wJALbflDWeZypyvcZjNOtEnqN2c+SAXEocRcxutGHlDq8DtxQ0wG5VfuU3gZEXDmAFHjsG9RTe3wL4bUS5eAfWSzwJBAJP7PCfERtwYXFt6CWlKa35R5261pwW2KI7uj+yzo81mjj3JXJENWiks9oE/pAhoN1AbhArcFrtnOkgLq4Gg8LsCQEKy7soJ4tcYvWED8uKq8x5fPoPHPk2OdJ3G+OVhKx1/yyfUFLjkTeN3+YHAmg89fyb4Yj/lQjPOoUJvonjIp4M=");
		PrivateKey privateKey = getPrivateKey(keyBytes3);
		System.out.println("privateKey3: " + HexCoder.toHex(privateKey.getEncoded()));
		System.out.println("privateKey3: " + Base64.encodeBytes(privateKey.getEncoded()));
		
		RSAKeyUtil ru1 = new RSAKeyUtil("certs/credit2go.p12","credit2go"); 
		System.out.println("PublicKey5: " + HexCoder.toHex(ru1.getPublicKey().getEncoded()));
		System.out.println("PublicKey5: " + Base64.encodeBytes(ru1.getPublicKey().getEncoded()));
		System.out.println("privateKey6: " + HexCoder.toHex(ru1.getPrivateKey().getEncoded()));
		System.out.println("privateKey6: " + Base64.encodeBytes(ru1.getPrivateKey().getEncoded()));
	
		RSAKeyUtil ru2 = new RSAKeyUtil(new File("S:/work/dev/openssl/_cedit2go/certs/credit2go.crt")); 
		System.out.println("PublicKey7: " + HexCoder.toHex(ru2.getPublicKey().getEncoded()));
		System.out.println("PublicKey7: " + Base64.encodeBytes(ru2.getPublicKey().getEncoded()));
	}

	public static void main(String[] args) {
		try {
			//String keyEncode = EscrowPropUtil.getFixed("jx.cg.fixed.keyEncode");
			String keyEncode  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD6Q+izz0AJxPkYSUFljM6NpwHMhlO9w5hdV4oVHrr14mR3jbZ3awF0gLBzJAC5A30aNI8wvc+ZfN10vqD7Rz31OKVm8qfh/lGw3yETnd5HA7jtXH7nC5tTnd73n5FzCF0l3DVVw55IR9pCZi1KQekvq0Xm6gphJBWNdFDPyJfJ4wIDAQAB";
			PublicKey pubKey=RSAKeyUtil.getPublicKeyBase64(keyEncode);
			logger.info("keys hex:{}",HexCoder.toHex(pubKey.getEncoded()));

			File fff1= new File("S:/work/scm/jixin/trunk/escrow/online2/src/main/webapp/WEB-INF/certs/3001103002/21_3001103002_credit.p12");
			logger.info("p12k hex:{}",HexCoder.toHex(new RSAKeyUtil(fff1,"credit2go").getPublicKey().getEncoded()));
			File fff2= new File("S:/work/scm/jixin/trunk/escrow/online2/src/main/webapp/WEB-INF/certs/3001103002/22_3001103002_credit.crt");
			logger.info("cert hex:{}",HexCoder.toHex(new RSAKeyUtil(fff2).getPublicKey().getEncoded()));
			
			String folder ="S:/work/dev/openssl/_credit/certs";
			for(File f: new File(folder).listFiles()){
				if(f.getName().endsWith(".crt")){
					logger.info("file:{}",f);
					logger.info("encode:{}",HexCoder.toHex(new RSAKeyUtil(f).getPublicKey().getEncoded()));
				}
			}
			
			


			String fs2 ="S:/work/scm/caizhong/trunk/credit2go-core/src/test/resources/certs/fdep_topk8.pem";
			getPrivateKey(fs2);
			//getPrivateKey("certs/credit2go.p12","pwd");
			testGenerateKeyPair();
			testGenerateKeys();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void getSignPrivateKey4Client(String signPrivatePath) throws Exception{
		StringBuffer privateBuffer=new StringBuffer();
		try {
			InputStream inputStream = new FileInputStream(signPrivatePath);
			InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader bufferReader = new BufferedReader(inputReader);
			// 读取一行
			String line = "";
			while ((line=bufferReader.readLine())!=null) {
				privateBuffer.append(line);
			}
			inputStream.close();
			
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(privateBuffer.toString());
		
        byte[] keyBytes = Base64.decodeString(privateBuffer.toString());
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);

		}

}
