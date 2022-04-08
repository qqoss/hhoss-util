package com.hhoss.util.thread;

import com.hhoss.Constant;

public class ThreadKeys implements Constant {
	private static final String activeDS = "spi.datasource.current";
	
	//skey>rkey>qkey>pkey  okey tkey
	//public static final String REQ_OWNER = "req.okey"; // owner object opera open others  ID
	//public static final String REQ_PRIME = "req.pkey"; // probe print prize prime prior privy post parent ID
	//public static final String REQ_QUEST = "req.qkey"; // query quest quota ID
	
	public static final String REQ_REFER = "req.rkey";  //REQ_TERM refer random request response root ID
	//public static final String REQ_SHARE = "req.skey"; //REQ_TICK refer random request response root ID
	//public static final String REQ_TOKEN = "req.tkey"; //REQ_TOCK trace tickets token task term ID
	
	public static final String REQ_HOST = "req.host"; // client request remote host ip 
	public static final String REQ_TERM = "req.term"; // share sequence serial service session ID
	public static final String REQ_TICK = "req.tick"; // session ID, ticketId
	public static final String REQ_TOCK = "req.tock"; // token ID 
	
	@Deprecated /** using LOG_TERM */
	public static final String TECK = "pipe.teck"; // logger level key
	public static final String LOG_TERM = "log.term"; // logger rule key for filterReply
	// APM: app performance/pipe monitor, process invoke probe enclose(PIPE); service process invoke(SPI)
	public static final String APM_TACK = "apm.tack"; // apm pipe:fixed root, inherit and service through
	public static final String APM_TICK = "apm.tick"; // apm pipe:inherit parent id
	public static final String APM_TOCK = "apm.tock"; // apm pipe:current create id	

	//customer be operated, data of user 
	public static final String CUST_UNID="cust.unid";
	public static final String CUST_CODE="cust.code";
	public static final String CUST_NAME="cust.name";
	public static final String CUST_ACCT="cust.acct";

	//organize,company unit 
	public static final String UNIT_UNID="unit.unid"; 
	public static final String UNIT_CODE="unit.code";
	public static final String UNIT_NAME="unit.name"; 
	public static final String UNIT_ACCT="unit.acct";

	// operation, login user 
	public static final String USER_UNID="user.unid";
	public static final String USER_CODE="user.code";
	public static final String USER_NAME="user.name";
	public static final String USER_ACCT="user.acct";
	public static final String USER_AUTH="user.auth";


	@Deprecated	public static final String referKey = "req.rkey"; // refer random request response root ID
	@Deprecated	public static final String shareKey = "req.skey"; // share sequence serial service session ID
	@Deprecated	public static final String serveKey = "req.skey"; // share sequence serial service session ID
	
    public static String getDS(){
    	return ThreadHold.get(activeDS);
    }
    
    public static void setDS(String key){
    	ThreadHold.set(activeDS,key);
    }    

	
}
