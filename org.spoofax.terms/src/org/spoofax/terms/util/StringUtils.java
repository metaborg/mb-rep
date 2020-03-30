package org.spoofax.terms.util;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Utility functions for working with strings.
 */
public final class StringUtils {
    /* Prevent instantiation. */
    private StringUtils() { }

    private static final CStringEscaper CESCAPE = new CStringEscaper(false, true, true);

    /**
     * Gets the C-escape instance that also escapes double quotes,
     * and operates in strict mode. Single quotes are not escaped.
     *
     * @return an instance of the {@link StringEscaper} interface.
     */
    public static StringEscaper getCEscape() { return CESCAPE; }

    /**
     * Escapes all double quotes, backslash, and control characters.
     * <p>
     * Escaping control characters ensures that text editors don't mess up the string when they don't understand,
     * trim, or elide control characters.
     * <p>
     * This method uses C-style escape sequences.
     *
     * @param unescapedInput the string value to escape; or {@code null}
     * @return the escaped string; or {@code null} when the input was {@code null}
     */
    public static @Nullable String escape(@Nullable String unescapedInput) {
        return getCEscape().escape(unescapedInput);
    }

    /**
     * Escapes all double quotes, backslash, and control characters,
     * and writes the result to the given {@link Appendable}.
     * <p>
     * Escaping control characters ensures that text editors and other programs that handle files
     * don't mess up the string when they don't understand, trim, or elide control characters.
     * <p>
     * This method uses C-style escape sequences: {@code \a} (alarm), {@code \b} (backspace), {@code \t} (tab),
     * {@code \n} (new line), {@code \v} (vertical tab), {@code \f} (form feed), {@code \r} (carriage return),
     * {@code \e} (escape), ({@code \'} (single quote)), {@code \"} (double quote), {@code \\} (backslash).
     * For control characters without a pre-defined escape sequence, it uses either an octal escape {@code \nnn},
     * a short hex escape {@code \xhh..}, a short Unicode escape {@code \u005cuhhhh},
     * or a long Unicode escape {@code \Uhhhhhhhh}.
     *
     * @param unescapedInput the string value to escape; or {@code null}
     * @param writer         the writer to append to
     * @throws IOException an I/O exception occurred
     */
    public static void appendEscape(@Nullable String unescapedInput, Appendable writer) throws IOException {
        getCEscape().appendEscape(unescapedInput, writer);
    }

    /**
     * Unescapes all C-style escape sequences.
     * <p>
     * This method recognizes all C-style escape sequences: {@code \a} (alarm), {@code \b} (backspace), {@code \t} (tab),
     * {@code \n} (new line), {@code \v} (vertical tab), {@code \f} (form feed), {@code \r} (carriage return),
     * {@code \e} (escape), {@code \'} (single quote), {@code \"} (double quote), {@code \\} (backslash),
     * octal escapes {@code \nnn}, short hex escapes {@code \xhh..}, short Unicode escapes {@code \u005cuhhhh},
     * and long Unicode escapes {@code \Uhhhhhhhh}.
     *
     * @param escapedInput the string value to unescape; or {@code null}
     * @return the unescaped string; or {@code null} when the input was {@code null}
     * @throws IllegalArgumentException the input has invalid escape sequences
     */
    public static @Nullable String unescape(@Nullable String escapedInput) {
        return getCEscape().unescape(escapedInput);
    }

    /**
     * Unescapes all C-style escape sequences.
     * <p>
     * This method recognizes all C-style escape sequences: {@code \a} (alarm), {@code \b} (backspace), {@code \t} (tab),
     * {@code \n} (new line), {@code \v} (vertical tab), {@code \f} (form feed), {@code \r} (carriage return),
     * {@code \e} (escape), {@code \'} (single quote), {@code \"} (double quote), {@code \\} (backslash),
     * octal escapes {@code \nnn}, short hex escapes {@code \xhh..}, short Unicode escapes {@code \u005cuhhhh},
     * and long Unicode escapes {@code \Uhhhhhhhh}.
     *
     * @param escapedInput the string value to unescape; or {@code null}
     * @param writer       the writer to append to
     * @throws IllegalArgumentException the input has invalid escape sequences
     * @throws IOException              an I/O exception occurred
     */
    public static void appendUnescape(@Nullable String escapedInput, Appendable writer) throws IOException {
        getCEscape().appendUnescape(escapedInput, writer);
    }

}
