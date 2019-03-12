package org.spoofax.interpreter.terms;

import java.io.IOException;

public interface ITermPrinter extends Appendable {

	public String getString();

	public void print(String string);

	public void println(String string, boolean b);

	public void indent(int i);

	public void nextIndentOff();

	public void println(String string);

	public void outdent(int i);

    default Appendable append(CharSequence csq) throws IOException {
        this.print(csq.toString());
        return this;
    }

    default Appendable append(CharSequence csq, int start, int end) throws IOException {
        this.append(csq.subSequence(start, end));
        return this;
    }

    default Appendable append(char c) throws IOException {
        this.print(new String(new char[] {c}));
        return this;
    }

}