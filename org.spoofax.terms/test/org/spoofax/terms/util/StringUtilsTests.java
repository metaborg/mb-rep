package org.spoofax.terms.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.spoofax.terms.util.Assert.assertEquals;

/** Tests the {@link StringUtils} class. */
@DisplayName("StringUtils")
public final class StringUtilsTests {

    private final static String ETAOIN_SHRDLU = "ETAOIN! SHRDLY! CMFWYP! " +
            "New York, July 18. - Here are two " +
            "reasons why bailiffs, judges, prosecu" +
            "tors and court stenographers die " +
            "young. " +
            "John Ziampettisledibetci was fined " +
            "$1 for owning an unmuzzled dog. " +
            "Robert Tyzyczhowzswiski is ask" +
            "ing the court to change his cogno" +
            "men.";

    /** Tests the {@link StringUtils#escape(String)} method. */
    @DisplayName("escape(String)")
    @Nested
    public final class EscapeTests {

        @TestFactory
        Stream<DynamicTest> escapesSpecialCharacters() {
            return Stream.of(
                    // @formatter:off
                    // Lower Control Characters
                    escapeSpecialCharacterTests("\00",    "\\0",     "NUL"),
                    escapeSpecialCharacterTests("\01",    "\\u0001", "SOH"),
                    escapeSpecialCharacterTests("\02",    "\\u0002", "STX"),
                    escapeSpecialCharacterTests("\03",    "\\u0003", "ETX"),
                    escapeSpecialCharacterTests("\04",    "\\u0004", "EOT"),
                    escapeSpecialCharacterTests("\05",    "\\u0005", "ENQ"),
                    escapeSpecialCharacterTests("\06",    "\\u0006", "ACK"),
                    escapeSpecialCharacterTests("\07",    "\\a",     "BEL"),
                    escapeSpecialCharacterTests("\10",    "\\b",     "BS"),
                    escapeSpecialCharacterTests("\11",    "\\t",     "TAB"),
                    escapeSpecialCharacterTests("\12",    "\\n",     "LF"),
                    escapeSpecialCharacterTests("\13",    "\\v",     "VT"),
                    escapeSpecialCharacterTests("\14",    "\\f",     "FF"),
                    escapeSpecialCharacterTests("\15",    "\\r",     "CR"),
                    escapeSpecialCharacterTests("\16",    "\\u000e", "S0"),
                    escapeSpecialCharacterTests("\17",    "\\u000f", "S1"),
                    escapeSpecialCharacterTests("\20",    "\\u0010", "DLE"),
                    escapeSpecialCharacterTests("\21",    "\\u0011", "DC1"),
                    escapeSpecialCharacterTests("\22",    "\\u0012", "DC2"),
                    escapeSpecialCharacterTests("\23",    "\\u0013", "DC3"),
                    escapeSpecialCharacterTests("\24",    "\\u0014", "DC4"),
                    escapeSpecialCharacterTests("\25",    "\\u0015", "NAK"),
                    escapeSpecialCharacterTests("\26",    "\\u0016", "SYN"),
                    escapeSpecialCharacterTests("\27",    "\\u0017", "ETB"),
                    escapeSpecialCharacterTests("\30",    "\\u0018", "CAN"),
                    escapeSpecialCharacterTests("\31",    "\\u0019", "EM"),
                    escapeSpecialCharacterTests("\32",    "\\u001a", "SUB"),
                    escapeSpecialCharacterTests("\33",    "\\e",     "ESC"),
                    escapeSpecialCharacterTests("\34",    "\\u001c", "FS"),
                    escapeSpecialCharacterTests("\35",    "\\u001d", "GS"),
                    escapeSpecialCharacterTests("\36",    "\\u001e", "RS"),
                    escapeSpecialCharacterTests("\37",    "\\u001f", "US"),
                    // Special Characters
                    escapeSpecialCharacterTests("\"",     "\\\"",    "Double Quote"),
                    escapeSpecialCharacterTests("'",      "\\'",     "Single Quote"),
                    escapeSpecialCharacterTests("\\",     "\\\\",    "Backslash"),
                    // Upper Control Characters
                    escapeSpecialCharacterTests("\u007f", "\\u007f", "DEL"),
                    escapeSpecialCharacterTests("\u0080", "\\u0080", "PAD"),
                    escapeSpecialCharacterTests("\u0081", "\\u0081", "HOP"),
                    escapeSpecialCharacterTests("\u0082", "\\u0082", "BPH"),
                    escapeSpecialCharacterTests("\u0083", "\\u0083", "NBH"),
                    escapeSpecialCharacterTests("\u0084", "\\u0084", "IND"),
                    escapeSpecialCharacterTests("\u0085", "\\u0085", "NEL"),
                    escapeSpecialCharacterTests("\u0086", "\\u0086", "SSA"),
                    escapeSpecialCharacterTests("\u0087", "\\u0087", "ESA"),
                    escapeSpecialCharacterTests("\u0088", "\\u0088", "HTS"),
                    escapeSpecialCharacterTests("\u0089", "\\u0089", "HTJ"),
                    escapeSpecialCharacterTests("\u008a", "\\u008a", "VTS"),
                    escapeSpecialCharacterTests("\u008b", "\\u008b", "PLD"),
                    escapeSpecialCharacterTests("\u008c", "\\u008c", "PLU"),
                    escapeSpecialCharacterTests("\u008d", "\\u008d", "RI"),
                    escapeSpecialCharacterTests("\u008e", "\\u008e", "SS2"),
                    escapeSpecialCharacterTests("\u008f", "\\u008f", "SS3"),
                    escapeSpecialCharacterTests("\u0090", "\\u0090", "DCS"),
                    escapeSpecialCharacterTests("\u0091", "\\u0091", "PU1"),
                    escapeSpecialCharacterTests("\u0092", "\\u0092", "PU2"),
                    escapeSpecialCharacterTests("\u0093", "\\u0093", "STS"),
                    escapeSpecialCharacterTests("\u0094", "\\u0094", "CCH"),
                    escapeSpecialCharacterTests("\u0095", "\\u0095", "MW"),
                    escapeSpecialCharacterTests("\u0096", "\\u0096", "SPA"),
                    escapeSpecialCharacterTests("\u0097", "\\u0097", "EPA"),
                    escapeSpecialCharacterTests("\u0098", "\\u0098", "SOS"),
                    escapeSpecialCharacterTests("\u0099", "\\u0099", "SCGI"),
                    escapeSpecialCharacterTests("\u009a", "\\u009a", "SCI"),
                    escapeSpecialCharacterTests("\u009b", "\\u009b", "CSI"),
                    escapeSpecialCharacterTests("\u009c", "\\u009c", "ST"),
                    escapeSpecialCharacterTests("\u009d", "\\u009d", "OSC"),
                    escapeSpecialCharacterTests("\u009e", "\\u009e", "PM"),
                    escapeSpecialCharacterTests("\u009f", "\\u009f", "APC")
                    // @formatter:on
            ).flatMap(s -> s);
        }

        Stream<DynamicTest> escapeSpecialCharacterTests(String input, String expected, String name) {
            return Stream.of(
                DynamicTest.dynamicTest("escapes special character " + name + " by itself", () -> {
                    // Act
                    String result = StringUtils.escape(input);

                    // Assert
                    assertEquals(expected, result);
                }),
                DynamicTest.dynamicTest("escapes special character " + name + " at the start of a string", () -> {
                    // Arrange
                    String suffix = "bar";

                    // Act
                    String result = StringUtils.escape(input + suffix);

                    // Assert
                    assertEquals(expected + suffix, result);
                }),
                DynamicTest.dynamicTest("escapes special character " + name + " at the end of a string", () -> {
                    // Arrange
                    String prefix = "foo";

                    // Act
                    String result = StringUtils.escape(prefix + input);

                    // Assert
                    assertEquals(prefix + expected, result);
                }),
                DynamicTest.dynamicTest("escapes special character " + name + " in the middle of a string", () -> {
                    // Arrange
                    String prefix = "foo";
                    String suffix = "bar";

                    // Act
                    String result = StringUtils.escape(prefix + input + suffix);

                    // Assert
                    assertEquals(prefix + expected + suffix, result);
                }),
                DynamicTest.dynamicTest("escapes a sequence of special character " + name + "s", () -> {
                    // Act
                    String result = StringUtils.escape(input + input + input);

                    // Assert
                    assertEquals(expected + expected + expected, result);
                }),

                DynamicTest.dynamicTest("unescapes escaped special character " + name + " by itself correctly", () -> {
                    // Act
                    String result = StringUtils.unescape(StringUtils.escape(input));

                    // Assert
                    assertEquals(input, result);
                }),
                DynamicTest.dynamicTest("unescapes escaped special character " + name + " at the start of a string correctly", () -> {
                    // Arrange
                    String suffix = "bar";

                    // Act
                    String result = StringUtils.unescape(StringUtils.escape(input + suffix));

                    // Assert
                    assertEquals(input + suffix, result);
                }),
                DynamicTest.dynamicTest("unescapes escaped special character " + name + " at the end of a string correctly", () -> {
                    // Arrange
                    String prefix = "foo";

                    // Act
                    String result = StringUtils.unescape(StringUtils.escape(prefix + input));

                    // Assert
                    assertEquals(prefix + input, result);
                }),
                DynamicTest.dynamicTest("unescapes escaped special character " + name + " in the middle of a string correctly", () -> {
                    // Arrange
                    String prefix = "foo";
                    String suffix = "bar";

                    // Act
                    String result = StringUtils.unescape(StringUtils.escape(prefix + input + suffix));

                    // Assert
                    assertEquals(prefix + input + suffix, result);
                }),
                DynamicTest.dynamicTest("unescapes escaped a sequence of special character " + name + "s correctly", () -> {
                    // Act
                    String result = StringUtils.unescape(StringUtils.escape(input + input + input));

                    // Assert
                    assertEquals(input + input + input, result);
                })
            );
        }

        @TestFactory
        Stream<DynamicTest> escapesStrings() {
            return Stream.of(
                    escapesStringsTests(null, null, "null"),
                    escapesStringsTests("", "", "empty string"),
                    escapesStringsTests(ETAOIN_SHRDLU, ETAOIN_SHRDLU, "basic characters"),
                    escapesStringsTests("\\\b\t\r", "\\\\\\b\\t\\r", "special characters"),
                    escapesStringsTests("\ntest\b", "\\ntest\\b", "text surrounded by control characters"),
                    escapesStringsTests("\u009F", "\\u009f", "lowercase hex sequences"),
                    escapesStringsTests("Hello, world\b\b\b\b\bcruel world", "Hello, world\\b\\b\\b\\b\\bcruel world", "world\b\b\b\b\bcruel world"),
                    escapesStringsTests("He didn't say, \"stop!\"", "He didn\\'t say, \\\"stop!\\\"", "string with double quotes"),
                    escapesStringsTests("This space is non-breaking:\u00a0", "This space is non-breaking:" + "\240", "non-breaking space"),
                    escapesStringsTests("\ud83c\udF00\u0020\u0045\u006c\u0020\u004e\u0069\u006e\u0303\u006f\u0020\uD83D\uDE00", "🌀 El Niño 😀", "emoji")
            ).flatMap(s -> s);
        }

        Stream<DynamicTest> escapesStringsTests(String input, String expected, String description) {
            return Stream.of(
                DynamicTest.dynamicTest("unescaping escaping correctly: " + description, () -> {
                    // Act
                    String result = StringUtils.unescape(StringUtils.escape(input));

                    // Assert
                    assertEquals(input, result);
                }),
                DynamicTest.dynamicTest("escaping correctly: " + description, () -> {
                    // Act
                    String result = StringUtils.escape(input);

                    // Assert
                    assertEquals(expected, result);
                })
            );
        }

    }


    /** Tests the {@link StringUtils#unescape(String)} method. */
    @DisplayName("unescape(String)")
    @Nested
    public final class UnescapeTests {
        @TestFactory
        Stream<DynamicTest> unescapesSpecialCharacters() {
            return Stream.of(
                    // @formatter:off
                    // Lower Control Characters
                    unescapeSpecialCharacterTests(0x00, "NUL"),
                    unescapeSpecialCharacterTests(0x01, "SOH"),
                    unescapeSpecialCharacterTests(0x02, "STX"),
                    unescapeSpecialCharacterTests(0x03, "ETX"),
                    unescapeSpecialCharacterTests(0x04, "EOT"),
                    unescapeSpecialCharacterTests(0x05, "ENQ"),
                    unescapeSpecialCharacterTests(0x06, "ACK"),
                    unescapeSpecialCharacterTests(0x07, "BEL"),
                    unescapeSpecialCharacterTests(0x08, "BS"),
                    unescapeSpecialCharacterTests(0x09, "TAB"),
                    unescapeSpecialCharacterTests(0x0a, "LF"),
                    unescapeSpecialCharacterTests(0x0b, "VT"),
                    unescapeSpecialCharacterTests(0x0c, "FF"),
                    unescapeSpecialCharacterTests(0x0d, "CR"),
                    unescapeSpecialCharacterTests(0x0e, "S0"),
                    unescapeSpecialCharacterTests(0x0f, "S1"),
                    unescapeSpecialCharacterTests(0x10, "DLE"),
                    unescapeSpecialCharacterTests(0x11, "DC1"),
                    unescapeSpecialCharacterTests(0x12, "DC2"),
                    unescapeSpecialCharacterTests(0x13, "DC3"),
                    unescapeSpecialCharacterTests(0x14, "DC4"),
                    unescapeSpecialCharacterTests(0x15, "NAK"),
                    unescapeSpecialCharacterTests(0x16, "SYN"),
                    unescapeSpecialCharacterTests(0x17, "ETB"),
                    unescapeSpecialCharacterTests(0x18, "CAN"),
                    unescapeSpecialCharacterTests(0x19, "EM"),
                    unescapeSpecialCharacterTests(0x1a, "SUB"),
                    unescapeSpecialCharacterTests(0x1b, "ESC"),
                    unescapeSpecialCharacterTests(0x1c, "FS"),
                    unescapeSpecialCharacterTests(0x1d, "GS"),
                    unescapeSpecialCharacterTests(0x1e, "RS"),
                    unescapeSpecialCharacterTests(0x1f, "US"),
                    // Special Characters
                    unescapeSpecialCharacterTests(0x22, "Double Quote"),
                    unescapeSpecialCharacterTests(0x27, "Single Quote"),
                    unescapeSpecialCharacterTests(0x5c, "Backslash"),
                    // Upper Control Characters
                    unescapeSpecialCharacterTests(0x7f, "DEL"),
                    unescapeSpecialCharacterTests(0x80, "PAD"),
                    unescapeSpecialCharacterTests(0x81, "HOP"),
                    unescapeSpecialCharacterTests(0x82, "BPH"),
                    unescapeSpecialCharacterTests(0x83, "NBH"),
                    unescapeSpecialCharacterTests(0x84, "IND"),
                    unescapeSpecialCharacterTests(0x85, "NEL"),
                    unescapeSpecialCharacterTests(0x86, "SSA"),
                    unescapeSpecialCharacterTests(0x87, "ESA"),
                    unescapeSpecialCharacterTests(0x88, "HTS"),
                    unescapeSpecialCharacterTests(0x89, "HTJ"),
                    unescapeSpecialCharacterTests(0x8a, "VTS"),
                    unescapeSpecialCharacterTests(0x8b, "PLD"),
                    unescapeSpecialCharacterTests(0x8c, "PLU"),
                    unescapeSpecialCharacterTests(0x8d, "RI"),
                    unescapeSpecialCharacterTests(0x8e, "SS2"),
                    unescapeSpecialCharacterTests(0x8f, "SS3"),
                    unescapeSpecialCharacterTests(0x90, "DCS"),
                    unescapeSpecialCharacterTests(0x91, "PU1"),
                    unescapeSpecialCharacterTests(0x92, "PU2"),
                    unescapeSpecialCharacterTests(0x93, "STS"),
                    unescapeSpecialCharacterTests(0x94, "CCH"),
                    unescapeSpecialCharacterTests(0x95, "MW"),
                    unescapeSpecialCharacterTests(0x96, "SPA"),
                    unescapeSpecialCharacterTests(0x97, "EPA"),
                    unescapeSpecialCharacterTests(0x98, "SOS"),
                    unescapeSpecialCharacterTests(0x99, "SCGI"),
                    unescapeSpecialCharacterTests(0x9a, "SCI"),
                    unescapeSpecialCharacterTests(0x9b, "CSI"),
                    unescapeSpecialCharacterTests(0x9c, "ST"),
                    unescapeSpecialCharacterTests(0x9d, "OSC"),
                    unescapeSpecialCharacterTests(0x9e, "PM"),
                    unescapeSpecialCharacterTests(0x9f, "APC")
                    // @formatter:on
            ).flatMap(s -> s);
        }

        Stream<DynamicTest> unescapeSpecialCharacterTests(int codepoint, String name) {
            return Stream.of(
                    DynamicTest.dynamicTest("unescapes special character " + name + " verbatim", () -> {
                        assumeTrue(codepoint != '\\');

                        // Act
                        String expected = String.valueOf(Character.toChars(codepoint));
                        String result = StringUtils.unescape(expected);

                        // Assert
                        assertEquals(expected, result);
                    }),
                    DynamicTest.dynamicTest("unescapes special character " + name + " encoded as octal", () -> {
                        // Arrange
                        assumeTrue(codepoint <= 511);
                        String expected = String.valueOf(Character.toChars(codepoint));
                        String octStr = Integer.toOctalString(codepoint);

                        // Act
                        String result = StringUtils.unescape("\\" + octStr);

                        // Assert
                        assertEquals(expected, result);
                    }),
                    DynamicTest.dynamicTest("unescapes special character " + name + " encoded as hex", () -> {
                        // Arrange
                        String expected = String.valueOf(Character.toChars(codepoint));
                        String hexStr = Integer.toHexString(codepoint);

                        // Act
                        String result = StringUtils.unescape("\\x" + hexStr);

                        // Assert
                        assertEquals(expected, result);
                    }),
                    DynamicTest.dynamicTest("unescapes special character " + name + " encoded as 4-digit hex", () -> {
                        // Arrange
                        String expected = String.valueOf(Character.toChars(codepoint));

                        // Act
                        String result = StringUtils.unescape("\\u" + String.format("%04x", codepoint));

                        // Assert
                        assertEquals(expected, result);
                    }),
                    DynamicTest.dynamicTest("unescapes special character " + name + " encoded as 8-digit hex", () -> {
                        // Arrange
                        String expected = String.valueOf(Character.toChars(codepoint));

                        // Act
                        String result = StringUtils.unescape("\\U" + String.format("%08x", codepoint));

                        // Assert
                        assertEquals(expected, result);
                    }),

                    DynamicTest.dynamicTest("unescapes special character " + name + " verbatim in the middle of a string", () -> {
                        // Assume
                        assumeTrue(codepoint != '\\');

                        // Arrange
                        String prefix = "foo";
                        String suffix = "qux";
                        String expected = prefix + String.valueOf(Character.toChars(codepoint)) + suffix;

                        // Act
                        String result = StringUtils.unescape(expected);

                        // Assert
                        assertEquals(expected, result);
                    }),
                    DynamicTest.dynamicTest("unescapes special character " + name + " encoded as octal in the middle of a string", () -> {
                        // Assume
                        assumeTrue(codepoint <= 511);

                        // Arrange
                        String prefix = "foo";
                        String suffix = "qux";
                        String expected = prefix + String.valueOf(Character.toChars(codepoint)) + suffix;
                        String octStr = Integer.toOctalString(codepoint);

                        // Act
                        String result = prefix + StringUtils.unescape("\\" + octStr) + suffix;

                        // Assert
                        assertEquals(expected, result);
                    }),
                    DynamicTest.dynamicTest("unescapes special character " + name + " encoded as hex in the middle of a string", () -> {
                        // Arrange
                        String prefix = "foo";
                        String suffix = "qux";
                        String expected = prefix + String.valueOf(Character.toChars(codepoint)) + suffix;
                        String hexStr = Integer.toHexString(codepoint);

                        // Act
                        String result = StringUtils.unescape(prefix + "\\x" + hexStr + suffix);

                        // Assert
                        assertEquals(expected, result);
                    }),
                    DynamicTest.dynamicTest("unescapes special character " + name + " encoded as 4-digit hex in the middle of a string", () -> {
                        // Arrange
                        String prefix = "foo";
                        String suffix = "qux";
                        String expected = prefix + String.valueOf(Character.toChars(codepoint)) + suffix;

                        // Act
                        String result = StringUtils.unescape(prefix + "\\u" + String.format("%04x", codepoint) + suffix);

                        // Assert
                        assertEquals(expected, result);
                    }),
                    DynamicTest.dynamicTest("unescapes special character " + name + " encoded as 8-digit hex in the middle of a string", () -> {
                        // Arrange
                        String prefix = "foo";
                        String suffix = "qux";
                        String expected = prefix + String.valueOf(Character.toChars(codepoint)) + suffix;

                        // Act
                        String result = StringUtils.unescape(prefix + "\\U" + String.format("%08x", codepoint) + suffix);

                        // Assert
                        assertEquals(expected, result);
                    })
            );
        }

        @TestFactory
        Stream<DynamicTest> unescapesStrings() {
            return Stream.of(
                    unescapesStringsTests(null, null, "null"),
                    unescapesStringsTests("", "", "empty string"),
                    unescapesStringsTests("\\0", "\0", "NUL"),
                    unescapesStringsTests("\\a", "\u0007", "alarm"),
                    unescapesStringsTests("\\b", "\b", "backspace"),
                    unescapesStringsTests("\\t", "\t", "tab"),
                    unescapesStringsTests("\\n", "\n", "newline"),
                    unescapesStringsTests("\\v", "\u000B", "vertical tab"),
                    unescapesStringsTests("\\f", "\f", "form feed"),
                    unescapesStringsTests("\\r", "\r", "carriage return"),
                    unescapesStringsTests("\\'", "'", "single quote"),
                    unescapesStringsTests("\\\"", "\"", "double quote"),
                    unescapesStringsTests("\\\\", "\\", "backslash"),
                    unescapesStringsTests(ETAOIN_SHRDLU, ETAOIN_SHRDLU, "basic characters"),
                    unescapesStringsTests("\\\\\\b\\t\\r", "\\\b\t\r", "special characters"),
                    unescapesStringsTests("*", "*", "unencoded asterisk"),
                    unescapesStringsTests("\\7", "\u0007", "one-digit alarm character"),
                    unescapesStringsTests("\\52", "*", "two-digit octal asterisk"),
                    unescapesStringsTests("\\052", "*", "three-digit octal asterisk"),
                    unescapesStringsTests("\\x2A", "*", "short uppercase hex sequence asterisk"),
                    unescapesStringsTests("\\x2a", "*", "short lowercase hex sequence asterisk"),
                    unescapesStringsTests("\\x0002A", "*", "medium hex sequence asterisk"),
                    unescapesStringsTests("\\x00000000000000002a", "*", "long hex sequence asterisk"),
                    unescapesStringsTests("\\u002A", "*", "UTF-16 uppercase hex sequence asterisk"),
                    unescapesStringsTests("\\u002a", "*", "UTF-16 lowercase hex sequence asterisk"),
                    unescapesStringsTests("\\U0000002A", "*", "UTF-32 uppercase hex sequence asterisk"),
                    unescapesStringsTests("\\U0000002a", "*", "UTF-32 lowercase hex sequence asterisk"),
                    unescapesStringsTests("\\799", "\u000799", "garbage octal digits are ignored (1)"),
                    unescapesStringsTests("\\719 luftballons", "99 luftballons", "garbage octal digits are ignored (2)"),
                    unescapesStringsTests("Sk\\56789.5fm", "Skŷ89.5fm", "garbage octal digits are ignored (3)"),
                    unescapesStringsTests("\\xfg", "\17g", "garbage hex digits are ignored (1)"),
                    unescapesStringsTests("\\xCapitain", "Êpitain", "garbage hex digits are ignored (2)"),
                    unescapesStringsTests("\\xBB8Skywalker", "ஸSkywalker", "garbage hex digits are ignored (3)"),
                    unescapesStringsTests("\\12345", "S45", "extra octal digits are ignored"),
                    unescapesStringsTests("\\uABCDEF", "ꯍEF", "extra hex digits are ignored"),
                    unescapesStringsTests("\\ud83c\\udF00 \\u0045\\u006c\40N\\x69\\U0000006e\\x303\\157\\u0020\\U0001f600", "🌀 El Niño 😀", "emoji")
            ).flatMap(s -> s);
        }

        Stream<DynamicTest> unescapesStringsTests(String input, String expected, String description) {
            return Stream.of(
                    DynamicTest.dynamicTest("unescapes correctly: " + description, () -> {
                        // Act
                        String result = StringUtils.unescape(input);

                        // Assert
                        assertEquals(expected, result);
                    })
            );
        }

        @TestFactory
        Stream<DynamicTest> throwsExceptionForInvalidInput() {
            return Stream.of(
                    throwsExceptionForInvalidInputTest("\\", "half escape sequence"),
                    throwsExceptionForInvalidInputTest("\\h", "unknown escape sequence"),
                    throwsExceptionForInvalidInputTest("\\x", "hex escape no more digits"),
                    throwsExceptionForInvalidInputTest("\\u123", "short unicode escape no more digits"),
                    throwsExceptionForInvalidInputTest("\\U1234567", "long Unicode escape no more digits"),
                    throwsExceptionForInvalidInputTest("\\9", "oct escape non-octal digit"),
                    throwsExceptionForInvalidInputTest("\\xg", "hex escape non-hex digit"),
                    throwsExceptionForInvalidInputTest("\\u123g", "short unicode escape non-hex digit"),
                    throwsExceptionForInvalidInputTest("\\U1234567g", "long Unicode escape non-hex digit"),
                    throwsExceptionForInvalidInputTest("\\UdeadBEEF", "invalid Unicode code point"),
                    throwsExceptionForInvalidInputTest("\\xDEADcafebabe", "Unicode code point out of bounds")
            );
        }

        @SuppressWarnings("CodeBlock2Expr")
        DynamicTest throwsExceptionForInvalidInputTest(String input, String description) {
            return DynamicTest.dynamicTest("throws exception when " + description, () -> {
                // Act
                assertThrows(IllegalArgumentException.class, () -> {
                    StringUtils.unescape(input);
                });
            });
        }
    }

}
