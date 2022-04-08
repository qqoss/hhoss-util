package com.hhoss.util.token;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenReplacer {

    private static Pattern PATTERN = Pattern.compile(
            "\\$\\s*\\{?\\s*([\\._0-9a-zA-Z]+)\\s*\\}?");
    
	public static String replace(String sources, Properties params) {
        if (sources == null || sources.length() == 0 || sources.indexOf('$') < 0) {
            return sources;
        }
        Matcher matcher = PATTERN.matcher(sources);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) { // 逐个匹配
            String key = matcher.group(1);
            String value = params.getProperty(key);
            if( value==null ){
            	value = TokenProvider.from("system","").get(key);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
	
	public static void main(String[] args){
		System.out.println(replace("java home: $ { JAVA_HOME }, user home:${user.home}. ",new Properties()));
	}

}
