package com.hhoss.misc;
import java.net.InetSocketAddress;


public class HostNameTest {
	public static void main(String[] args){
		String[] params = new String[2];
		if(args!=null){
			if(args.length>0) params[0]=args[0];
			if(args.length>1) params[1]=args[1];
		}
		if(params[1]==null){
	        System.out.println("usage: command host port");
	        return;
		}
        InetSocketAddress address = new InetSocketAddress(params[0], Integer.parseInt(params[1]));
        System.out.println(address==null?"":address.toString());
	}
}
