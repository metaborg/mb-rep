package org.spoofax.terms.io;

import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Interface for term readers.
 * <p>
 * A term reader reads a term from some textual or binary format.
 * Configuration parameters on the term reader determine how the terms are read,
 * such as whether to support comments in a textual reader.
 *
 * @implSpec Textual term readers should implement the {@link TextTermReader} interface.
 * @see TextTermReader
 */
public interface TermReader {

    /**
     * Reads a term from the specified byte array.
     *
     * @param bytes the byte array to read from
     * @return the read term
     */
    default IStrategoTerm readFromBytes(byte[] bytes) {
        try(final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            return read(inputStream);
        } catch(IOException e) {
            // The ByteArrayInputStream implementation never throws an IOException.
            throw new RuntimeException("Unexpected exception: " + e.getMessage(), e);
        }
    }

    /**
     * Reads the term from the specified file.
     *
     * @param file the file to read from
     * @return the read term
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    default IStrategoTerm readFromFile(File file) throws IOException, SecurityException {
        try(final FileInputStream fileStream = new FileInputStream(file)) {
            try(final BufferedInputStream inputStream = new BufferedInputStream(fileStream)) {
                return read(inputStream);
            }
        }
    }

    /**
     * Reads the term from the specified path.
     *
     * @param path the path to read from
     * @return the read term
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    default IStrategoTerm readFromPath(Path path) throws IOException, SecurityException {
        try(final InputStream fileStream = Files.newInputStream(path)) {
            try(final BufferedInputStream inputStream = new BufferedInputStream(fileStream)) {
                return read(inputStream);
            }
        }
    }

    /**
     * Reads the term from the specified output stream.
     * <p>
     * This method does not close the stream.
     *
     * @param inputStream the input stream to read from
     * @return the read term
     * @throws IOException an I/O exception occurred
     */
    IStrategoTerm read(InputStream inputStream) throws IOException;
}
