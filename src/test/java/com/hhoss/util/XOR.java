package com.hhoss.util;

/**
 * @author kejun
 *
 */
public class XOR {
	private static final int A = 0XAAAAAAAA;
	private static final int B = A>>>1;
	static final int C = B<<1;
	static final int D = -1-A;
	
	public static void main(String[] args) {
		testXor(5235,9873);
		testXor(2382,987234);
		testXor(590235,94873);
	}
	
	static void testXor(int x,int y) {
		System.out.println(xor1(x,y)-xor2(x,y));
		assert xor1(x,y)==xor2(x,y); 
	}
	
	static int xor2(int x,int y){
		return  ((x&A)+(y&A)&A)|((x&B)+(y&B)&B);
	}
	
	static int xor1(int x,int y){
		return  x^y;
	}
	
}
