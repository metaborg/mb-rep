package org.spoofax.terms.util;

import jakarta.annotation.Nullable;
import java.io.IOException;

/**
 * Escapes and unescapes strings.
 */
public interface StringEscaper {

    /**
     * Escapes the specified input string.
     *
     * @param unescapedInput the string value to escape; or {@code null}
     * @return the escaped string; or {@code null} when the input was {@code null}
     */
    default @Nullable String escape(@Nullable String unescapedInput) {
        if(unescapedInput == null) return null;

        StringBuilder sb = new StringBuilder(unescapedInput.length());

        boolean changed;
        try {
            changed = appendEscape(unescapedInput, sb);
        } catch(IOException e) {
            // This should never happen: a StringBuilder never throws an IOException.
            throw new RuntimeException("Unexpected exception: " + e.getMessage(), e);
        }

        if(!changed) return unescapedInput;
        return sb.toString();
    }

    /**
     * Appends the escaped specified input string.
     *
     * @param unescapedInput the string value to escape; or {@code null}
     * @param writer         the writer to append to
     * @return whether any characters where escaped
     * @throws IOException an I/O exception occurred
     */
    boolean appendEscape(@Nullable String unescapedInput, Appendable writer) throws IOException;

    /**
     * Unescapes the specified input string.
     *
     * @param escapedInput the string value to unescape; or {@code null}
     * @return the unescaped string; or {@code null} when the input was {@code null}
     * @throws IllegalArgumentException the input has invalid escape sequences
     */
    default @Nullable String unescape(@Nullable String escapedInput) {
        if(escapedInput == null) return null;

        StringBuilder sb = new StringBuilder(escapedInput.length());

        boolean changed;
        try {
            changed = appendUnescape(escapedInput, sb);
        } catch(IOException e) {
            // This should never happen: a StringBuilder never throws an IOException.
            throw new RuntimeException("Unexpected exception: " + e.getMessage(), e);
        }

        if(!changed) return escapedInput;
        return sb.toString();
    }

    /**
     * Appends the unescaped specified input string.
     *
     * @param escapedInput the string value to unescape; or {@code null}
     * @param writer       the writer to append to
     * @return whether any characters where unescaped
     * @throws IllegalArgumentException the input has invalid escape sequences
     * @throws IOException              an I/O exception occurred
     */
    boolean appendUnescape(@Nullable String escapedInput, Appendable writer) throws IOException;

}
