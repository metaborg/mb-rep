package org.spoofax.terms.io;

import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for term writers.
 * <p>
 * A term writer writes a term to some textual or binary format.
 * Configuration parameters on the term writer determine how the terms are written,
 * such as whether to use indentation in a textual writer,
 * or whether to use maximal sharing in a binary writer.
 *
 * @implSpec Textual term writers should implement the {@link TextTermWriter} interface.
 * @see TextTermWriter
 * @see SimpleTextTermWriter
 */
public interface TermWriter {

    /**
     * Writes the term to a byte array.
     *
     * @param term the term to write
     * @return the byte representation of the term
     */
    default byte[] writeToBytes(IStrategoTerm term) {
        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            write(term, stream);
            return stream.toByteArray();
        } catch(IOException e) {
            // The ByteArrayOutputStream implementation never throws an IOException.
            throw new RuntimeException("Unexpected exception: " + e.getMessage(), e);
        }
    }

    /**
     * Writes the term to the specified output stream.
     *
     * @param term         the term to write
     * @param outputStream the output stream
     * @throws IOException an I/O exception occurred
     */
    void write(IStrategoTerm term, OutputStream outputStream) throws IOException;

}
