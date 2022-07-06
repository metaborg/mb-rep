package org.spoofax.terms.io;

import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Interface for textual term readers.
 * <p>
 * A textual term reader reads a term from some textual format.
 * Configuration parameters on the term reader determine how the terms are read,
 * such as whether to support comments.
 *
 * @implSpec Binary term readers should instead implement the {@link TermReader} interface.
 * @see TermReader
 */
public interface TextTermReader extends TermReader {

    /**
     * Reads a term from the specified byte array,
     * with the UTF-8 character set.
     *
     * @param bytes the byte array to read from
     * @return the read term
     */
    @Override default IStrategoTerm readFromBytes(byte[] bytes) {
        return readFromBytes(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Reads a term from the specified byte array,
     * with the specified character set.
     *
     * @param bytes the byte array to read from
     * @param characterSet the character set to use
     * @return the read term
     */
    default IStrategoTerm readFromBytes(byte[] bytes, Charset characterSet) {
        try(final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            return read(inputStream, characterSet);
        } catch(IOException e) {
            // The ByteArrayInputStream implementation never throws an IOException.
            throw new RuntimeException("Unexpected exception: " + e.getMessage(), e);
        }
    }

    /**
     * Reads the term from a string.
     *
     * @param str the string to read from
     * @return the read term
     */
    default IStrategoTerm readFromString(String str) {
        final StringReader reader = new StringReader(str);
        try {
            return read(reader);
        } catch(IOException e) {
            // The StringReader implementation never throws an IOException.
            throw new RuntimeException("Unexpected exception: " + e.getMessage(), e);
        }
    }

    /**
     * Reads the term from the specified file,
     * with the UTF-8 character set.
     *
     * @param file the file to read from
     * @return the read term
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    @Override default IStrategoTerm readFromFile(File file) throws IOException, SecurityException {
        return readFromFile(file, StandardCharsets.UTF_8);
    }

    /**
     * Reads the term from the specified file,
     * with the specified character set.
     *
     * @param file the file to read from
     * @param characterSet the character set to use
     * @return the read term
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    default IStrategoTerm readFromFile(File file, Charset characterSet) throws IOException, SecurityException {
        try(final FileInputStream fileStream = new FileInputStream(file)) {
            try(final BufferedInputStream inputStream = new BufferedInputStream(fileStream)) {
                return read(inputStream, characterSet);
            }
        }
    }

    /**
     * Reads the term from the specified path,
     * with the specified character set.
     *
     * @param path the path to read from
     * @return the read term
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    @Override default IStrategoTerm readFromPath(Path path) throws IOException, SecurityException {
        return readFromPath(path, StandardCharsets.UTF_8);
    }

    /**
     * Reads the term from the specified path,
     * with the specified character set.
     *
     * @param path the path to read from
     * @param characterSet the character set to use
     * @return the read term
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    default IStrategoTerm readFromPath(Path path, Charset characterSet) throws IOException, SecurityException {
        try(final BufferedReader reader = Files.newBufferedReader(path, characterSet)) {
            return read(reader);
        }
    }

    /**
     * Reads the term from the specified output stream,
     * with the UTF-8 character set.
     * <p>
     * This method does not close the stream.
     *
     * @param inputStream the input stream to read from
     * @return the read term
     * @throws IOException an I/O exception occurred
     */
    @Override default IStrategoTerm read(InputStream inputStream) throws IOException {
        return read(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * Reads the term from the specified output stream,
     * with the specified character set.
     * <p>
     * This method does not close the stream.
     *
     * @param inputStream the input stream to read from
     * @param characterSet the character set to use
     * @return the read term
     * @throws IOException an I/O exception occurred
     */
    default IStrategoTerm read(InputStream inputStream, Charset characterSet) throws IOException {
        final InputStreamReader reader = new InputStreamReader(inputStream, characterSet);
        return read(reader);
        // Do not close the reader, doing so will also close the backing stream.
    }

    /**
     * Reads the term from the specified reader.
     * <p>
     * This method does not close the reader.
     *
     * @param reader the reader to read from
     * @return the read term
     * @throws IOException an I/O exception occurred
     */
    IStrategoTerm read(Reader reader) throws IOException;

}
