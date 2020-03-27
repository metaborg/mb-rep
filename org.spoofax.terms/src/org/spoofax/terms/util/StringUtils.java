package org.spoofax.terms.util;

import javax.annotation.Nullable;

/**
 * Utility functions for working with strings.
 */
public final class StringUtils {
    /* Prevent instantiation. */
    private StringUtils() {}

    /**
     * Escapes all single quotes, double quotes, backslash, and control characters.
     *
     * Escaping control characters ensures that text editors don't mess up the string when they don't understand,
     * trim, or elide control characters.
     *
     * This method uses C-style escape sequences.
     *
     * @param unescapedInput the string value to escape; or {@code null}
     * @return the escaped string; or {@code null} when the input was {@code null}
     */
    public static @Nullable String escape(@Nullable String unescapedInput) {
        if (unescapedInput == null) return null;

        StringBuilder sb = new StringBuilder(unescapedInput.length());

        for (int i = 0; i < unescapedInput.length(); i++) {
            char c = unescapedInput.charAt(i);
            switch (c) {
                // @formatter:off
                case '\0':     sb.append("\\0");  break; // 0x00 NUL (null)
                case '\u0007': sb.append("\\a");  break; // 0x07 BEL (alarm)
                case '\b':     sb.append("\\b");  break; // 0x08  BS (backspace)
                case '\t':     sb.append("\\t");  break; // 0x09  HT (tab)
                case '\n':     sb.append("\\n");  break; // 0x0A  LF (line feed)
                case '\u000B': sb.append("\\v");  break; // 0x0B  VT (vertical tab)
                case '\f':     sb.append("\\f");  break; // 0x0C  FF (form feed)
                case '\r':     sb.append("\\r");  break; // 0x0D  CR (carriage return)
                case '\u001B': sb.append("\\e");  break; // 0x1B ESC (escape)
                case '\'':     sb.append("\\'");  break; // 0x27     (single quote)
                case '"':      sb.append("\\\""); break; // 0x22     (double quote)
                case '\\':     sb.append("\\\\"); break; // 0x5C     (backslash)
                // @formatter:on
                default:
                    if (Character.isISOControl(c)) {
                        // Supplementary characters (those that need two Java chars)
                        // are never Unicode control characters.
                        sb.append("\\u").append(String.format("%04x", (int)c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }

        if (sb.length() > unescapedInput.length()) {
            // Any escape always make the string longer.
            return sb.toString();
        } else {
            // If it still has the same length, we didn't escape anything.
            assert sb.length() == unescapedInput.length();
            return unescapedInput;
        }
    }

    /**
     * Unescapes all C-style escape sequences.
     *
     * @param escapedInput the string value to unescape; or {@code null}
     * @return the unescaped string; or {@code null} when the input was {@code null}
     * @throws IllegalArgumentException the input has invalid escape sequences
     */
    public static @Nullable String unescape(@Nullable String escapedInput) {
        if (escapedInput == null) return null;

        StringBuilder sb = new StringBuilder(escapedInput.length());

        for (int i = 0; i < escapedInput.length(); i++) {
            char c = escapedInput.charAt(i);
            if (c != '\\') {
                sb.append(c);
                continue;
            }
            if (i == escapedInput.length() - 1) {
                throw new IllegalArgumentException("Prematurely terminated escape sequence '\\' at offset " + i + " in string: " + escapedInput);
            }
            char c0 = escapedInput.charAt(++i);
            switch (c0) {
                // @formatter:off
                case 'a':      sb.append("\u0007"); break; // 0x07 BEL (alarm)
                case 'b':      sb.append("\b");     break; // 0x08  BS (backspace)
                case 't':      sb.append("\t");     break; // 0x09  HT (tab)
                case 'n':      sb.append("\n");     break; // 0x0A  LF (line feed)
                case 'v':      sb.append("\u000B"); break; // 0x0B  VT (vertical tab)
                case 'f':      sb.append("\f");     break; // 0x0C  FF (form feed)
                case 'r':      sb.append("\r");     break; // 0x0D  CR (carriage return)
                case 'e':      sb.append("\u001B"); break; // 0x1B ESC (escape)
                case '\'':     sb.append("'");      break; // 0x27     (single quote)
                case '"':      sb.append("\"");     break; // 0x22     (double quote)
                case '\\':     sb.append("\\");     break; // 0x5C     (backslash)
                // @formatter:on
                case '8':
                case '9':
                    throw new IllegalArgumentException("Non-octal digit '\\" + c0 + "' at offset " + i + " in string: " + escapedInput);
                case 'x':       // \xhh..
                    // Escape with any number of hexadecimal characters
                    i += appendHexCodepointAt(escapedInput, i + 1, -1, sb);
                    break;
                case 'u':       // \\uhhhh
                    // Escape with four hexadecimal characters
                    i += appendHexCodepointAt(escapedInput, i + 1, 4, sb);
                    break;
                case 'U':       // \Uhhhhhhhh
                    // Escape with eight hexadecimal characters
                    i += appendHexCodepointAt(escapedInput, i + 1, 8, sb);
                    break;
                default:        // \o, \oo, \ooo
                    if (!isOctDigit(c0)) {
                        throw new IllegalArgumentException("Unrecognized escape sequence '\\" + c0 + "' at offset " + i + " in string: " + escapedInput);
                    }
                    i += appendOctCodepointAt(escapedInput, i, 3, sb) - 1;
                    break;
            }
        }

        if (sb.length() < escapedInput.length()) {
            // Any unescape always make the string shorter.
            return sb.toString();
        } else {
            // If it still has the same length, we didn't unescape anything.
            assert sb.length() == escapedInput.length();
            return escapedInput;
        }
    }

    /**
     * Reads a string of octal characters of the specified length,
     * and turns it into a code point which is appended to the given {@link StringBuilder}.
     *
     * @param input the string from which to read
     * @param index the zero-based index in the string at which to start reading
     * @param maxLength the maximum length of the string of octal characters to read
     * @param builder the {@link StringBuilder} to which the read code point is appended
     * @return the number of characters read
     */
    private static int appendOctCodepointAt(String input, int index, int maxLength, StringBuilder builder) {
        assert maxLength > 0 : "Zero or negative (unlimited) length is not supported by this implementation.";

        // Read as many octal characters as needed.
        maxLength = Math.min(maxLength, input.length() - index);
        StringBuilder octStrBuilder = new StringBuilder(maxLength);
        for (int i = 0; i < maxLength; i++) {
            char c = input.charAt(index + i);
            if (!isOctDigit(c)) break;
            octStrBuilder.append(c);
        }
        String octStr = octStrBuilder.toString();

        // This method is only called when there is at least one octal digit to read.
        assert (octStr.length() > 0);

        if (octStr.length() == 1) {
            // Shortcut for the most used octal escapes, e.g., "\0"
            builder.append((char)(octStr.charAt(0) - '0'));
            return 1;
        }

        // This cannot throw a NumberFormatException because the string consists of octal characters and is never too big.
        int codepoint = Integer.parseInt(octStr, 8);
        try {
            builder.append(String.valueOf(Character.toChars(codepoint)));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Escape sequence designates invalid Unicode code point '\\" + octStr + "' at offset " + index + " in string: " + input);
        }

        // Return the number of octal characters read.
        return octStrBuilder.length();
    }

    /**
     * Reads a string of hexadecimal characters of the specified length,
     * and turns it into a code point which is appended to the given {@link StringBuilder}.
     *
     * @param input   the string from which to read
     * @param index   the zero-based index in the string at which to start reading
     * @param length  the length of the string of hexadecimal characters to read; or -1 to impose no limit
     * @param builder the {@link StringBuilder} to which the read code point is appended
     * @return the number of characters read
     */
    private static int appendHexCodepointAt(String input, int index, int length, StringBuilder builder) {
        if (length >= 0 && index + length > input.length()) {
            // There are not enough characters left in the input string to read 'length' characters.
            throw new IllegalArgumentException("Prematurely terminated escape sequence '\\" + input.substring(index) + "' at offset " + index + " in string: " + input);
        }

        // Read as many hexadecimal characters as needed.
        int maxLength = length >= 0 ? length : input.length() - index;
        StringBuilder hexStrBuilder = new StringBuilder(length >= 0 ? length : 4);
        for (int i = 0; i < maxLength; i++) {
            char c = input.charAt(index + i);
            if (!isHexDigit(c)) break;
            hexStrBuilder.append(c);
        }
        String hexStr = hexStrBuilder.toString();

        // Did we read enough?
        if (hexStr.length() == 0 || (length >= 0 && hexStr.length() != length)) {
            // There where not enough hexadecimal characters left in the input string to read 'length' characters.
            throw new IllegalArgumentException("Prematurely terminated escape sequence '\\" + hexStr + "' at offset " + index + " in string: " + input);
        }

        int codepoint;
        try {
            codepoint = Integer.parseInt(hexStr, 16);
        } catch (NumberFormatException e) {
            // We know the string contains only hex digits, so the parsed number must be too big.
            throw new IllegalArgumentException("Escape sequence designates Unicode code point out of bounds '\\" + hexStrBuilder.toString() + "' at offset " + index + " in string: " + input);
        }

        try {
            builder.append(String.valueOf(Character.toChars(codepoint)));
        } catch (IllegalArgumentException e) {
            // We use `hexStrBuilder.toString()` here instead of `hexStr` to print the whole escape code that was read.
            throw new IllegalArgumentException("Escape sequence designates invalid Unicode code point '\\" + hexStrBuilder.toString() + "' at offset " + index + " in string: " + input);
        }

        // Return the number of hex characters read.
        return hexStrBuilder.length();
    }

    /**
     * Determines whether a given character is a decimal digit.
     *
     * @param c the character to test
     * @return {@code true} when it is a decimal digit; otherwise, {@code false}.
     */
    private static boolean isDecDigit(char c) {
        // Note that Character.isDigit() also allows Unicode digits,
        // which we do not allow.

        return c >= '0' && c <= '9';
    }

    /**
     * Determines whether a given character is a hexadecimal digit.
     *
     * @param c the character to test
     * @return {@code true} when it is a hexadecimal digit; otherwise, {@code false}.
     */
    private static boolean isHexDigit(char c) {
        // @formatter:off
        return isDecDigit(c)
            || (c >= 'A' && c <= 'F')
            || (c >= 'a' && c <= 'f');
        // @formatter:on
    }

    /**
     * Determines whether a given character is an octal digit.
     *
     * @param c the character to test
     * @return {@code true} when it is an octal digit; otherwise, {@code false}.
     */
    private static boolean isOctDigit(char c) {
        return c >= '0' && c <= '7';
    }

}
