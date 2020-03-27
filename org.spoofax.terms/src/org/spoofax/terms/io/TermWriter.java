package org.spoofax.terms.io;

import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
     * Writes the term to the specified file.
     *
     * @param term the term to write
     * @param file the file to write to
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    default void writeToFile(IStrategoTerm term, File file) throws IOException, SecurityException {
        try(FileOutputStream fileStream = new FileOutputStream(file)) {
            try(BufferedOutputStream outputStream = new BufferedOutputStream(fileStream)) {
                write(term, outputStream);
            }
        }
    }

    /**
     * Writes the term to the specified path.
     *
     * @param term the term to write
     * @param path the path to write to
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    default void writeToPath(IStrategoTerm term, Path path) throws IOException, SecurityException {
        try(OutputStream fileStream = Files.newOutputStream(path)) {
            try(BufferedOutputStream outputStream = new BufferedOutputStream(fileStream)) {
                write(term, outputStream);
            }
        }
    }

    /**
     * Writes the term to the specified output stream.
     * <p>
     * This method does not close the stream.
     *
     * @param term         the term to write
     * @param outputStream the output stream
     * @throws IOException an I/O exception occurred
     */
    void write(IStrategoTerm term, OutputStream outputStream) throws IOException;

}
