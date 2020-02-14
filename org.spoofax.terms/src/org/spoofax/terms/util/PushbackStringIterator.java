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
		int c = data.codePointAt(position);
		position += Character.charCount(c);
		return c;
	}

	// TODO should not be needed anymore, but is used in JSGLR1 because it does not support Unicode
	public char truncateUnicodeChar(int c) {
		if (c >= UNICODE_OTHER) {
			if (Character.isLetter(c)) {
				return UNICODE_LETTER;
			} else if (Character.isDigit(c)) {
				return UNICODE_DIGIT;
			} else {
				return UNICODE_OTHER;
			}
		}
		return (char) c;
	}

	public void unread(int c) {
		position -= Character.charCount(c);
	}

	public int getOffset() {
		return position;
	}

	public void setOffset(int offset) {
		this.position = offset;
	}
}
