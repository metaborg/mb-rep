package org.spoofax.terms.io;

import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
    @Override default byte[] writeToBytes(IStrategoTerm term) {
        return writeToBytes(term, StandardCharsets.UTF_8);
    }

    /**
     * Writes the term to a byte array,
     * with the specified character set.
     *
     * @param term         the term to write
     * @param characterSet the character set to use
     * @return the byte representation of the term
     */
    default byte[] writeToBytes(IStrategoTerm term, Charset characterSet) {
        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            write(term, stream, characterSet);
            return stream.toByteArray();
        } catch(IOException e) {
            // The ByteArrayOutputStream implementation never throws an IOException.
            throw new RuntimeException("Unexpected exception: " + e.getMessage(), e);
        }
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
     * Writes the term to the specified file,
     * with the UTF-8 character set.
     *
     * @param term the term to write
     * @param file the file to write to
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    @Override default void writeToFile(IStrategoTerm term, File file) throws IOException, SecurityException {
        writeToFile(term, file, StandardCharsets.UTF_8);
    }

    /**
     * Writes the term to the specified file,
     * with the specified character set.
     *
     * @param term         the term to write
     * @param file         the file to write to
     * @param characterSet the character set to use
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    default void writeToFile(IStrategoTerm term, File file, Charset characterSet) throws IOException, SecurityException {
        try(FileOutputStream fileStream = new FileOutputStream(file)) {
            try(BufferedOutputStream outputStream = new BufferedOutputStream(fileStream)) {
                write(term, outputStream, characterSet);
            }
        }
    }

    /**
     * Writes the term to the specified path,
     * with the UTF-8 character set.
     *
     * @param term the term to write
     * @param path the path to write to
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    @Override default void writeToPath(IStrategoTerm term, Path path) throws IOException, SecurityException {
        writeToPath(term, path, StandardCharsets.UTF_8);
    }

    /**
     * Writes the term to the specified path,
     * with the specified character set.
     *
     * @param term         the term to write
     * @param path         the path to write to
     * @param characterSet the character set to use
     * @throws IOException           an I/O exception occurred
     * @throws FileNotFoundException the file is not found, not a regular file (e.g., a directory),
     *                               or cannot be opened for any reason
     * @throws SecurityException     access to the file is denied
     */
    default void writeToPath(IStrategoTerm term, Path path, Charset characterSet) throws IOException, SecurityException {
        try(BufferedWriter writer = Files.newBufferedWriter(path, characterSet)) {
            write(term, writer);
        }
    }

    /**
     * Writes the term to the specified output stream,
     * with the UTF-8 character set.
     * <p>
     * This method does not close the stream.
     *
     * @param term         the term to write
     * @param outputStream the output stream
     * @throws IOException an I/O exception occurred
     */
    @Override default void write(IStrategoTerm term, OutputStream outputStream) throws IOException {
        write(term, outputStream, StandardCharsets.UTF_8);
    }

    /**
     * Writes the term to the specified output stream.
     * <p>
     * This method does not close the stream.
     *
     * @param term         the term to write
     * @param outputStream the output stream
     * @param characterSet the character set to use
     * @throws IOException an I/O exception occurred
     */
    default void write(IStrategoTerm term, OutputStream outputStream, Charset characterSet) throws IOException {
        try(OutputStreamWriter writer = new OutputStreamWriter(outputStream, characterSet)) {
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
