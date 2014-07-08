package org.spoofax.terms.util;

public class PushbackStringIterator {
	
	public static final int UNICODE_LETTER = 255;
	
	public static final int UNICODE_DIGIT = 254;
	
	public static final int UNICODE_OTHER = 253;

	private final String data;
	private int position;

	public PushbackStringIterator(String data) {
		this.data = data;
		position = 0;
		assert data != null;
	}
	
	public int read() {
		if(position >= data.length())
			return -1;
		char c = data.charAt(position++);
		c = truncateUnicodeChar(c);
		return c;
	}

	public char truncateUnicodeChar(char c) {
		if (c >= UNICODE_OTHER) {
			if (Character.isLetter(c)) {
				c = UNICODE_LETTER;
			} else if (Character.isDigit(c)) {
				c = UNICODE_DIGIT;
			} else {
				c = UNICODE_OTHER;
			}
		}
		return c;
	}
	
	public void unread(int c) {
		position--;
	}

	public int getOffset() {
		return position;
	}

	public void setOffset(int offset) {
		this.position = offset;
	}
}
