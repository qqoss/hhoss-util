package com.hhoss.lang;

public class JudgeTest {

	private static boolean judge(Object obj, Object val){
		return Judge.expect(obj, val);
	}
	public static void main(String[] args) {
		for(int i=0;i<100_000;i++)
		test();
	}
	
	private static void test(){
		Assert.isTrue(judge("356.000000",356));
		Assert.isTrue(judge(356.000000,"356"));
		Assert.isFalse(judge("356.0000000001",356));
		Assert.isTrue(judge(true," 1"));
		Assert.isTrue(judge(true,"1"));
		Assert.isTrue(judge(0,false));
		Assert.isTrue(judge(false," false"));
		Assert.isTrue(judge(Boolean.FALSE," false"));
		Assert.isTrue(judge("fdsfs "," fdsfs"));
		Assert.isFalse(judge((byte)' ',20));
	}

}
