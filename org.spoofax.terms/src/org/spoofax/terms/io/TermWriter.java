package org.spoofax.terms.io;

import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Interface for term writers.
 */
public interface TermWriter {

    /**
     * Gets a singleton instance of the default {@link TextTermWriter} implementation.
     *
     * @return the singleton instance
     */
    static TextTermWriter getInstance() { return TextTermWriter.INSTANCE; }

    /**
     * Writes the term to a string.
     *
     * @param term the term to write
     * @return the string representation of the term
     */
    default String writeToString(IStrategoTerm term) {
        StringBuilder sb = new StringBuilder();
        try {
            write(term, sb);
        } catch (IOException e) {
            // This couldn't have happened!
            throw new RuntimeException("Unexpected exception: ", e);
        }
        return sb.toString();
    }

    /**
     * Writes the term to the specified output stream.
     *
     * @param term the term to write
     * @param outputStream the output stream
     * @throws IOException an I/O exception occurred
     */
    default void write(IStrategoTerm term, OutputStream outputStream) throws IOException {
        try (PrintWriter writer = new PrintWriter(outputStream)) {
            write(term, writer);
        }
    }

    /**
     * Writes the term to the specified appendable.
     *
     * @param term the term to write
     * @param writer the appendable
     * @throws IOException an I/O exception occurred
     */
    void write(IStrategoTerm term, Appendable writer) throws IOException;

}
