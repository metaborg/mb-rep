package org.spoofax.terms.io;

import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Interface for textual term writers.
 * <p>
 * A textual term writer writes a term to some textual format.
 * Configuration parameters on the term writer determine how the terms are written,
 * such as whether to use indentation or the maximum depth.
 *
 * @implSpec Binary term writers should instead implement the {@link TermWriter} interface.
 * @see TermWriter
 * @see SimpleTextTermWriter
 */
public interface TextTermWriter extends TermWriter {

    /**
     * Writes the term to a byte array,
     * with the UTF-8 character set.
     *
     * @param term the term to write
     * @return the byte representation of the term
     */
    @Override
    default byte[] writeToBytes(IStrategoTerm term) {
        return TermWriter.super.writeToBytes(term);
    }

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
        } catch(IOException e) {
            // The StringBuilder implementation never throws an IOException.
            throw new RuntimeException("Unexpected exception: " + e.getMessage(), e);
        }
        return sb.toString();
    }

    /**
     * Writes the term to the specified output stream,
     * with the UTF-8 character set.
     *
     * @param term         the term to write
     * @param outputStream the output stream
     * @throws IOException an I/O exception occurred
     */
    @Override
    default void write(IStrategoTerm term, OutputStream outputStream) throws IOException {
        write(term, outputStream, StandardCharsets.UTF_8);
    }

    /**
     * Writes the term to the specified output stream.
     *
     * @param term         the term to write
     * @param outputStream the output stream
     * @param characterSet the character set to use
     * @throws IOException an I/O exception occurred
     */
    default void write(IStrategoTerm term, OutputStream outputStream, Charset characterSet) throws IOException {
        try(PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, characterSet))) {
            write(term, writer);
        }
    }

    /**
     * Writes the term to the specified appendable.
     *
     * @param term   the term to write
     * @param writer the appendable
     * @throws IOException an I/O exception occurred
     */
    void write(IStrategoTerm term, Appendable writer) throws IOException;

}
