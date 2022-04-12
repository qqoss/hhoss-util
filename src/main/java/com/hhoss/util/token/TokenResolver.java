//refer org.apache.ibatis.parsing.GenericTokenParser;
package com.hhoss.util.token;

import java.util.Stack;

import com.hhoss.aspi.Provider;
import com.hhoss.lang.Classes;

/**
 * @author kejun
 * 
 *         allow recurse and replace with substitution
 * 
 */
public class TokenResolver extends TokenProvider {
	private static final long serialVersionUID = 4619607967396536420L;
	private String tokenBegin = "${";
	private String tokenClose = "}";
	private final Provider<String, String> provider;

	public TokenResolver(String tokenBegin, String tokenClose, Provider<String, String> provider) {
		this.tokenBegin = tokenBegin;
		this.tokenClose = tokenClose;
		this.provider = provider;
	}

	public TokenResolver(Provider<String, String> provider) {
		this.provider = provider;
	}

	@Override
	public String get(String text) {
		if (provider == null || text == null) {
			return null;
		}else if(text.indexOf(tokenBegin)>-1){
			return resolve(text, new Stack<String>());
		}
		return text;
	}

	/**
	 * 
	 * @param text
	 * @param stack
	 * @return value replaced token
	 * @see java.util.regex.Matcher#appendReplacement
	 */
	private String resolve(String text, Stack<String> stack) {
		StringBuilder builder = new StringBuilder();
		char[] src = text.toCharArray();
		int offset = 0;
		int start = text.indexOf(tokenBegin, offset);
		while (start > -1) {
			if (start > 0 && src[start - 1] == '\\') {
				// the variable is escaped. remove the backslash.
				builder.append(src, offset, start - offset - 1).append(tokenBegin);
				offset = start + tokenBegin.length();
			} else {
				int close = closeOf(text, start + tokenBegin.length());//text.indexOf(tokenClose,start);
				if (close == -1) {
					builder.append(src, offset, src.length - offset);
					offset = src.length;
				} else {
					builder.append(src, offset, start - offset);
					offset = start + tokenBegin.length();
					String subst = new String(src, offset, close - offset);
					if (stack.contains(subst)) {
						// error #[recurse cycle] endless loop
						builder.append("#RECURSE.CYCLE[").append(subst).append("]");//#RECURSE.CYCLE
					}
					stack.push(subst);
					while (subst.indexOf(tokenBegin) > -1) {
						subst = resolve(subst, stack);
					}
					String content = provider.get(subst);
					builder.append(content);
					stack.pop();
					offset = close + tokenClose.length();
				}
			}
			start = text.indexOf(tokenBegin, offset);
		}
		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return builder.toString();
	}

	private int closeOf(String text, int start) {
		int offset = start;
		while ((offset = text.indexOf(tokenClose, offset)) > start && offset < text.length()) {
			if (count(text, tokenBegin, start, offset) == count(text, tokenClose, start, offset)) {
				return offset;
			} else {
				offset += tokenClose.length();
			}
		}
		return -1;
	}

	public static int count(String text, String token, int start, int end) {
		int c = 0;
		int offset = start;

		while ((offset = text.indexOf(token, offset)) > -1 && offset < end) {
			c++;
			offset += token.length();
		}
		return c;
	}

	private void test_close() {
		String s = null;
		int close;
		s = "ab.${cd.fg}noend";
		System.out.println(s.substring(0, (close = closeOf(s, 0)) > -1 ? close : s.length()));
		s = "ab.${cd.fg}}";
		System.out.println(s.substring(0, closeOf(s, 0)));
		s = "ab.${cd.fg.${efg}}";
		System.out.println(s.substring(5, closeOf(s, 5)));
		s = "ab.${cd.fg${efg}.${efg}}";
		System.out.println(s.substring(5, closeOf(s, 5)));
		s = "ab.${cd.fg${efg}.${efg}}";
		System.out.println(s.substring(12, closeOf(s, 12)));
	}

	public static void main(String[] args) {
		 new TokenResolver(null).test_close();
	}

	@Override
	public String getName() {
		return Classes.referName();
	}

}
