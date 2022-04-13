package com.hhoss.lang;


/**
 * assert handle
 * @author kejun
 * @see org.springframework.util.Assert;
 *
 */
public class Assert {
	
	public static void isFalse(boolean expression, String message) {
		if (expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isFalse(boolean expression) {
		isFalse(expression, "[Assertion failed] - this expression must be true");
	}
	
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void verify(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	public static void hasText(CharSequence text) {
		hasText(text, "[Assertion failed] - this text must NOT be blank");
	}
	
	public static void hasText(CharSequence text, String message) {
		if (text==null) {
			throw new IllegalArgumentException(message);
		}
		int strLen = text.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(text.charAt(i))) {
				return;
			}
		}
        throw new IllegalArgumentException(message);
	}

	/**
	 * Assert that an object is not {@code null} .
	 * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that an object is not {@code null} .
	 * <pre class="code">Assert.notNull(clazz);</pre>
	 * @param object the object to check
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notNull(Object object) {
		notNull(object, "[Assertion failed] - this argument is required; it must not be null");
	}

	/**
	 * Assert that an array has elements; that is, it must not be
	 * {@code null} and must have at least one element.
	 * <pre class="code">Assert.notEmpty(array, "The array must have elements");</pre>
	 * @param array the array to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object array is {@code null} or has no elements
	 */
	public static void notEmpty(Object[] array, String message) {
		if (array==null||array.length==0) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that an array has elements; that is, it must not be
	 * {@code null} and must have at least one element.
	 * <pre class="code">Assert.notEmpty(array);</pre>
	 * @param array the array to check
	 * @throws IllegalArgumentException if the object array is {@code null} or has no elements
	 */
	public static void notEmpty(Object[] array) {
		notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
	}

	
	public static void main(String[] args){
		
	}
	
}
