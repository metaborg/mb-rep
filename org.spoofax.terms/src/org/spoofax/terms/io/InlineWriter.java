package org.spoofax.terms.io;

import java.io.IOException;
import java.io.Writer;

import org.spoofax.interpreter.terms.ITermPrinter;

/**
 * Term printer that writes directly to a {@link Writer}, without building the
 * entire string representation in-memory. Does not support {@link #reset()} or
 * {@link #getString()}. If an {@link IOException} is thrown by the
 * {@link Writer}, it is wrapped in a {@link RuntimeException}.
 * 
 * Based on {@link InlinePrinter}.
 * 
 * @author Nathan Bruning
 * 
 */
public class InlineWriter implements ITermPrinter {

    Writer writer;

    public InlineWriter(Writer writer) {
        this.writer = writer;
    }

    public void print(String string) {
        /*
         * UNDONE: don't break empty strategostrings if (string.matches("^ *$"))
         * { return ; }
         */
        tryWrite(string);
    }

    public String getString() {
        throw new UnsupportedOperationException();
    }

    public void indent(int i) {
    }

    public void nextIndentOff() {
    }

    public void outdent(int i) {
    }

    @Override
    public String toString() {
        return getString();
    }

    public void reset() {
        throw new UnsupportedOperationException();
    }

    public void println(String string, boolean b) {
        /*
         * if (string.matches("^ *$")) { return ; }
         */
        tryWrite(string);
    }

    public void println(String string) {
        tryWrite(string);
    }

    protected void tryWrite(String string) {
        try {
            writer.write(string);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write term", e);
        }
    }
}
