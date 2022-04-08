package com.hhoss.conf;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import ch.qos.logback.core.status.Status;

import com.hhoss.boot.App;
import com.hhoss.jour.Logger;
import com.hhoss.jour.LoggerConfig;

public class ResHolderTest {
	private static Logger logger; 
	
	public static void test2(){
		ResHolder res = new ResHolder("test2").lookup("cacher,hasher,parent");
		res.setProperty("test.value", "#3");
		res.setProperty("test.value.0", "strs99");
		res.setProperty("test.value.1", "str1r99");
		res.setProperty("test.value.3", "strtr99");
		Collection<String> s1 = res.getList("test.value");
		logger.info("list:{}",s1);
		String[] s2 = res.getArray("test.value");
		logger.info("array:{}",Arrays.toString(s2));
	}
	
	public static void test3(){
		ResHolder res = new ResHolder("test3").lookup("hasher");
		res.setProperty("test.value", "str1,,str2;str3 strff4|strww5\rstre6\nstr7\t strgg8 str2	str99");
		Collection<String> s1 = res.getList("test.value");
		logger.info("split list:{}",s1);
		String[] s2 = res.getArray("test.value");
		logger.info("split array:{}",Arrays.toString(s2));
		
		res.setProperty("var.list.split.delimiters", ",;|");
		Collection<String> s3 = res.getList("test.value");
		logger.info("split[,;|] list:{}",s3);
		String[] s4 = res.getArray("test.value");
		logger.info("split[,;|] array:{}",Arrays.toString(s4));
	}
	
	public static void test1(){
		ResHolder conf = new ResHolder("test1").lookup("hasher,rooter,system");;
		String[] keys = new String[]{"JAVA_HOME","PATH","TMP","TEMP","PATHEXT"};
		for(String key:keys){
			String env = conf.getProperty(key);
			logger.info("{}={}",key,env);
		}
	}

	private static void test4() {
		Properties p = new ResHolder("test4").lookup("hasher");
		p.put("spi.dbms3.hostport","3.190.7.6:5432");
		p.put("spi.dbms3.database","testdb3");
		p.put("spi.dbms3.username","gpadmin3");
		p.put("spi.dbms3.password","credit2go3");
		
		p.put("spi.dbms1.hostport","1.190.7.6:5432");
		p.put("spi.dbms1.database","testdb1");
		p.put("spi.dbms1.username","gpadmin1");
		p.put("spi.dbms1.password","credit2go1");
		
		p.put("spi.dbms2.hostport","2.190.7.6:5432");
		p.put("spi.dbms2.database","testdb2");
		p.put("spi.dbms2.username","gpadmin2");
		p.put("spi.dbms2.password","credit2go2");
		
		p.put("spi.dbms","spi.dbms1");
		
		String[] keys= new String[]{"${spi.dbms}.hostport","${spi.dbms}.database","${spi.dbms}.username","${spi.dbms}.password"};
		for(String key:keys){
			logger.info("{}={}",key,p.getProperty(key));
		}
	}
	private static void test5() {
		ResHolder res = (ResHolder)App.getProperties("res.dev.test1");
		logger.info("array {} size4: {}","test.list1.item",res.getArray("test.list1.item").length);
		logger.info("array {} size7: {}","test.list1.item.",res.getArray("test.list1.item.").length);
		logger.info("array {} size5: {}","test.array.name1",res.getArray("test.array.name1").length);
		logger.info("array {} size1: {}","test.array.name2",res.getArray("test.array.name2").length);
		logger.info("array {} size2: {}","test.array.name2.",res.getArray("test.array.name2.").length);
		
		logger.info("list {} size4: {}","test.list1.item",res.getList("test.list1.item").size());
		logger.info("list {} size7: {}","test.list1.item.",res.getList("test.list1.item.").size());
		logger.info("list {} size5: {}","test.array.name1",res.getList("test.array.name1").size());
		logger.info("list {} size1: {}","test.array.name2",res.getList("test.array.name2").size());
		logger.info("list {} size2: {}","test.array.name2.",res.getList("test.array.name2.").size());
	}
	
	private static void test6() {
		logger.info("test.bool.key... for isTrue or isFalse。 ");

		ResHolder res = (ResHolder)App.getProperties("res.dev.test1");		
		for(int i=1;i<35;i++){
			String key = "test.bool.key"+i;
			logger.info("test.bool.key{} isTrue: {}",i,res.isTrue(key));
			logger.info("test.bool.key{} isFalse: {}",i,res.isFail(key));
		}

	}
	private static void test9() {
		logger.info("test.bool.key. for getBool... ");
		ResHolder res = (ResHolder)App.getProperties("res.dev.test1");		
		
		res.setProperty("test.bool.key.wrong", "setwrong");
		logger.info("test.bool.key.wrong, def true: {}",res.getBool("test.bool.key.wrong",true));
		logger.info("test.bool.key.wrong, def false: {}",res.getBool("test.bool.key.wrong",false));
		logger.info("test.bool.key.none, def true: {}",res.getBool("test.bool.key.none",true));
		logger.info("test.bool.key.none, def false: {}",res.getBool("test.bool.key.none",false));
		
		res.setProperty("test.bool.key.ttt", "True");
		res.setProperty("test.bool.key.fff", "False");
		logger.info("test.bool.key.ttt=true, def true: {}",res.getBool("test.bool.key.ttt",true));
		logger.info("test.bool.key.ttt=true, def false: {}",res.getBool("test.bool.key.ttt",false));
		logger.info("test.bool.key.fff=false, def true: {}",res.getBool("test.bool.key.fff",true));
		logger.info("test.bool.key.fff=false, def false: {}",res.getBool("test.bool.key.fff",false));

	}

	
	private static void test7(){
		List<Status> list = LoggerConfig.getStatus();
		for(Status s:list){
			logger.info(s.toString());
		}
	}
	private static void test8() {
		logger.info("test group key...");
		ResHolder res = (ResHolder)App.getProperties("res.dev.test2");		
		for(int g=0;g<4;g++){	
			String key1="test.group"+g+".key";
			for(int k=0;k<=9;k++){
				String key = key1+k;
				logger.info("{}={} isTrue: {}",key,res.getProperty(key),res.isTrue(key));
				logger.info("{}={} isFalse: {}",key,res.getProperty(key),res.isFail(key));
			}
		}

	}

	public static void main(String[] args){		
		App.defaultInitial();
		LoggerConfig.initial();
		logger = Logger.get();
		test1();
		test2();
		test3();
		test4();
		test5();
		test6();
		test9();
		test8();
		test7();
	}

}
