package org.spoofax.terms.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility method for working with input/output streams.
 */
public final class IOStreamExt {
    private IOStreamExt() { /* Cannot be instantiated. */ }

    /**
     * Ensures the input stream is buffered,
     * while avoiding double buffering.
     *
     * @param inputStream the input stream to buffer
     * @return the buffered input stream
     */
    public static BufferedInputStream ensureBuffered(InputStream inputStream) {
        if (inputStream instanceof BufferedInputStream) return (BufferedInputStream)inputStream;
        return new BufferedInputStream(inputStream);
    }

    /**
     * Ensures the output stream is buffered,
     * while avoiding double buffering.
     *
     * @param outputStream the output stream to buffer
     * @return the buffered output stream
     */
    public static BufferedOutputStream ensureBuffered(OutputStream outputStream) {
        if (outputStream instanceof BufferedOutputStream) return (BufferedOutputStream)outputStream;
        return new BufferedOutputStream(outputStream);
    }
}
