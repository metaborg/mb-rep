package org.spoofax.terms.util;

import jakarta.annotation.Nullable;
import java.io.IOException;

/**
 * (Un)escapes all single quotes, double quotes, backslash, and control characters.
 * <p>
 * Escaping control characters ensures that text editors and other programs that handle files
 * don't mess up the string when they don't understand, trim, or elide control characters.
 * <p>
 * This method uses C-style escape sequences: {@code \a} (alarm), {@code \b} (backspace), {@code \t} (tab),
 * {@code \n} (new line), {@code \v} (vertical tab), {@code \f} (form feed), {@code \r} (carriage return),
 * {@code \e} (escape), {@code \'} (single quote), {@code \"} (double quote), {@code \\} (backslash).
 * For control characters without a pre-defined escape sequence, it uses either an octal escape {@code \nnn},
 * a short hex escape {@code \xhh..}, a short Unicode escape {@code \u005cuhhhh},
 * or a long Unicode escape {@code \Uhhhhhhhh}.
 */
public final class CStringEscaper implements StringEscaper {

    private final boolean escapeSingleQuote;
    private final boolean escapeDoubleQuote;
    private final boolean strictUnescape;

    /**
     * Initializes a new instance of the {@link CStringEscaper} class,
     * that escapes single quotes, double quotes, and unescapes in strict mode.
     */
    public CStringEscaper() {
        this(true, true, true);
    }

    /**
     * Initializes a new instance of the {@link CStringEscaper} class.
     *
     * @param escapeSingleQuote whether to escape single quotes
     * @param escapeDoubleQuote whether to escape double quotes
     * @param strictUnescape whether to throw an exception when an invalid or incomplete escape sequence is encountered
     */
    public CStringEscaper(boolean escapeSingleQuote, boolean escapeDoubleQuote, boolean strictUnescape) {
        this.escapeSingleQuote = escapeSingleQuote;
        this.escapeDoubleQuote = escapeDoubleQuote;
        this.strictUnescape = strictUnescape;
    }

    @Override public boolean appendEscape(@Nullable String unescapedInput, Appendable writer) throws IOException {
        if(unescapedInput == null) {
            // Append null. For example, for a StringBuilder this appends the string "null".
            writer.append(null);
            return false;
        }

        boolean escaped = false;
        for(int i = 0; i < unescapedInput.length(); i++) {
            char c = unescapedInput.charAt(i);
            switch(c) {
                // @formatter:off
                case '\0':     writer.append("\\0"); escaped = true; break; // 0x00 NUL (null)
                case '\u0007': writer.append("\\a"); escaped = true; break; // 0x07 BEL (alarm)
                case '\b':     writer.append("\\b"); escaped = true; break; // 0x08  BS (backspace)
                case '\t':     writer.append("\\t"); escaped = true; break; // 0x09  HT (tab)
                case '\n':     writer.append("\\n"); escaped = true; break; // 0x0A  LF (line feed)
                case '\u000B': writer.append("\\v"); escaped = true; break; // 0x0B  VT (vertical tab)
                case '\f':     writer.append("\\f"); escaped = true; break; // 0x0C  FF (form feed)
                case '\r':     writer.append("\\r"); escaped = true; break; // 0x0D  CR (carriage return)
                case '\u001B': writer.append("\\e"); escaped = true; break; // 0x1B ESC (escape)
                case '\'':     if (escapeSingleQuote) { writer.append("\\'");  escaped = true; } else { writer.append(c); } break; // 0x27     (single quote)
                case '"':      if (escapeDoubleQuote) { writer.append("\\\""); escaped = true; } else { writer.append(c); } break; // 0x22     (double quote)
                case '\\':     writer.append("\\\\"); escaped = true; break; // 0x5C     (backslash)
                // @formatter:on
                default:
                    if(Character.isISOControl(c)) {
                        // Supplementary characters (those that need two Java chars)
                        // are never Unicode control characters.
                        writer.append("\\u").append(String.format("%04x", (int)c));
                        escaped = true;
                    } else {
                        writer.append(c);
                    }
                    break;
            }
        }
        return escaped;
    }

    @Override public boolean appendUnescape(@Nullable String escapedInput, Appendable writer) throws IOException {
        if(escapedInput == null) {
            // Append null. For example, for a StringBuilder this appends the string "null".
            writer.append(null);
            return false;
        }

        boolean unescaped = false;
        for(int i = 0; i < escapedInput.length(); i++) {
            char c = escapedInput.charAt(i);
            if(c != '\\') {
                writer.append(c);
                continue;
            }
            if(i == escapedInput.length() - 1) {
                if (strictUnescape) {
                    throw new IllegalArgumentException("Prematurely terminated escape sequence '\\' at offset " + i + " in string: " + escapedInput);
                } else {
                    // Append the character unchanged.
                    writer.append(c);
                    continue;
                }
            }
            char c0 = escapedInput.charAt(++i);
            switch(c0) {
                // @formatter:off
                case 'a':  writer.append("\u0007"); unescaped = true; break; // 0x07 BEL (alarm)
                case 'b':  writer.append("\b");     unescaped = true; break; // 0x08  BS (backspace)
                case 't':  writer.append("\t");     unescaped = true; break; // 0x09  HT (tab)
                case 'n':  writer.append("\n");     unescaped = true; break; // 0x0A  LF (line feed)
                case 'v':  writer.append("\u000B"); unescaped = true; break; // 0x0B  VT (vertical tab)
                case 'f':  writer.append("\f");     unescaped = true; break; // 0x0C  FF (form feed)
                case 'r':  writer.append("\r");     unescaped = true; break; // 0x0D  CR (carriage return)
                case 'e':  writer.append("\u001B"); unescaped = true; break; // 0x1B ESC (escape)
                case '\'': writer.append("'");      unescaped = true; break; // 0x27     (single quote)
                case '"':  writer.append("\"");     unescaped = true; break; // 0x22     (double quote)
                case '\\': writer.append("\\");     unescaped = true; break; // 0x5C     (backslash)
                // @formatter:on
                case '8':
                case '9':
                    if(strictUnescape) {
                        throw new IllegalArgumentException("Non-octal digit '\\" + c0 + "' at offset " + i + " in string: " + escapedInput);
                    } else {
                        // Append the character unchanged.
                        writer.append(c0);
                        continue;
                    }
                case 'x': {      // \xhh..
                    // Escape with any number of hexadecimal characters
                    int read = appendHexCodepointAt(escapedInput, i + 1, -1, 'x', writer);
                    if(read > 0) {
                        i += read;
                        unescaped = true;
                    } else {
                        // The string of hex characters was invalid.
                        // Append the character unchanged.
                        writer.append(c0);
                        continue;
                    }
                    break;
                }
                case 'u': {      // \\uhhhh
                    // Escape with four hexadecimal characters
                    int read = appendHexCodepointAt(escapedInput, i + 1, 4, 'u', writer);
                    if(read > 0) {
                        i += read;
                        unescaped = true;
                    } else {
                        // The string of hex characters was invalid.
                        // Append the character unchanged.
                        writer.append(c0);
                        continue;
                    }
                    break;
                }
                case 'U': {      // \Uhhhhhhhh
                    // Escape with eight hexadecimal characters
                    int read = appendHexCodepointAt(escapedInput, i + 1, 8, 'U', writer);
                    if(read > 0) {
                        i += read;
                        unescaped = true;
                    } else {
                        // The string of hex characters was invalid.
                        // Append the character unchanged.
                        writer.append(c0);
                        continue;
                    }
                    break;
                }
                default:        // \o, \oo, \ooo
                    if(!isOctDigit(c0)) {
                        if (strictUnescape) {
                            throw new IllegalArgumentException("Unrecognized escape sequence '\\" + c0 + "' at offset " + i + " in string: " + escapedInput);
                        } else {
                            // Append the character unchanged.
                            writer.append(c0);
                            continue;
                        }
                    }
                    int read = appendOctCodepointAt(escapedInput, i, 3, writer);
                    if(read > 0) {
                        i += read - 1;
                        unescaped = true;
                    } else {
                        // The string of hex characters was invalid.
                        // Append the character unchanged.
                        writer.append(c0);
                        continue;
                    }
                    break;
            }
        }
        return unescaped;
    }

    /**
     * Reads a string of octal characters of the specified length,
     * and turns it into a code point which is appended to the given {@link StringBuilder}.
     *
     * @param input     the string from which to read
     * @param index     the zero-based index in the string at which to start reading
     * @param maxLength the maximum length of the string of octal characters to read
     * @param writer    the {@link Appendable} to which the read code point is appended
     * @return the number of characters read
     */
    private int appendOctCodepointAt(String input, int index, int maxLength, Appendable writer) throws IOException {
        assert maxLength > 0 : "Zero or negative (unlimited) length is not supported by this implementation.";

        // Read as many octal characters as needed.
        maxLength = Math.min(maxLength, input.length() - index);
        StringBuilder octStrBuilder = new StringBuilder(maxLength);
        for(int i = 0; i < maxLength; i++) {
            char c = input.charAt(index + i);
            if(!isOctDigit(c)) break;
            octStrBuilder.append(c);
        }
        String octStr = octStrBuilder.toString();

        // This method is only called when there is at least one octal digit to read.
        assert (octStr.length() > 0);

        if(octStr.length() == 1) {
            // Shortcut for the most used octal escapes, e.g., "\0"
            writer.append((char)(octStr.charAt(0) - '0'));
            return 1;
        }

        // This cannot throw a NumberFormatException because the string consists of octal characters and is never too big.
        int codepoint = Integer.parseInt(octStr, 8);
        try {
            writer.append(String.valueOf(Character.toChars(codepoint)));
        } catch(IllegalArgumentException e) {
            if (strictUnescape) {
                throw new IllegalArgumentException("Escape sequence designates invalid Unicode code point '\\" + octStr + "' at offset " + index + " in string: " + input);
            } else {
                // Bail out
                return 0;
            }
        }

        // Return the number of octal characters read.
        return octStrBuilder.length();
    }

    /**
     * Reads a string of hexadecimal characters of the specified length,
     * and turns it into a code point which is appended to the given {@link StringBuilder}.
     *
     * @param input  the string from which to read
     * @param index  the zero-based index in the string at which to start reading
     * @param length the length of the string of hexadecimal characters to read; or -1 to impose no limit
     * @param prefix the prefix of the escape
     * @param writer the {@link Appendable} to which the read code point is appended
     * @return the number of characters read
     */
    private int appendHexCodepointAt(String input, int index, int length, char prefix, Appendable writer) {
        if(length >= 0 && index + length > input.length()) {
            if (strictUnescape) {
                // There are not enough characters left in the input string to read 'length' characters.
                throw new IllegalArgumentException("Prematurely terminated escape sequence '\\" + prefix + input.substring(index) + "' at offset " + index + " in string: " + input);
            } else {
                // Bail out
                return 0;
            }
        }

        // Read as many hexadecimal characters as needed.
        int maxLength = length >= 0 ? length : input.length() - index;
        StringBuilder hexStrBuilder = new StringBuilder(length >= 0 ? length : 4);
        for(int i = 0; i < maxLength; i++) {
            char c = input.charAt(index + i);
            if(!isHexDigit(c)) break;
            hexStrBuilder.append(c);
        }
        String hexStr = hexStrBuilder.toString();

        // Did we read enough?
        if(hexStr.length() == 0 || (length >= 0 && hexStr.length() != length)) {
            if (strictUnescape) {
                // There where not enough hexadecimal characters left in the input string to read 'length' characters.
                throw new IllegalArgumentException("Prematurely terminated escape sequence '\\" + prefix + hexStr + "' at offset " + index + " in string: " + input);
            } else {
                // Bail out
                return 0;
            }
        }

        int codepoint;
        try {
            codepoint = Integer.parseInt(hexStr, 16);
        } catch(NumberFormatException e) {
            if (strictUnescape) {
                // We know the string contains only hex digits, so the parsed number must be too big.
                throw new IllegalArgumentException("Escape sequence designates Unicode code point out of bounds '\\" + prefix + hexStrBuilder.toString() + "' at offset " + index + " in string: " + input);
            } else {
                // Bail out
                return 0;
            }
        }

        try {
            writer.append(String.valueOf(Character.toChars(codepoint)));
        } catch(IllegalArgumentException | IOException e) {
            if (strictUnescape) {
                // We use `hexStrBuilder.toString()` here instead of `hexStr` to print the whole escape code that was read.
                throw new IllegalArgumentException("Escape sequence designates invalid Unicode code point '\\" + prefix + hexStrBuilder.toString() + "' at offset " + index + " in string: " + input);
            } else {
                // Bail out
                return 0;
            }
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
