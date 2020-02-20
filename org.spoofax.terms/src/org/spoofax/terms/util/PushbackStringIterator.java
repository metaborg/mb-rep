package org.spoofax.terms.util;

public class PushbackStringIterator {

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
