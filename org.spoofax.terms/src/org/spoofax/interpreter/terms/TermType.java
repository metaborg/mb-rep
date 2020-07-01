package org.spoofax.interpreter.terms;

/**
 * Specifies the type of a term.
 *
 * Enums can safely be compared using {@code ==}, with the additional
 * benefit that the comparison will be false when either side evaluates to {@code null}.
 */
public enum TermType {
    /** A Constructor Application term type. */
    APPL(1),
    /** A List term type. */
    LIST(2),
    /** An Int term type. */
    INT(3),
    /** A Real term type. */
    REAL(4),
    /** A String term type. */
    STRING(5),
    /** A Term Constructor term type. */
    CTOR(6),
    /** A Tuple term type. */
    TUPLE(7),
    /** A Ref term type. */
    REF(8),
    /** A Blob term type. */
    BLOB(9),
    /** A Placeholder term type. */
    PLACEHOLDER(10);

    @Deprecated
    private final int value;

    TermType(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value, used for legacy code.
     *
     * @deprecated Compare the enum directly.
     */
    @Deprecated
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the {@link TermType} that corresponds to the given integer constant value.
     * @param value the value
     * @return the corresponding {@link TermType}
     * @throws IllegalArgumentException The given value does not represent a valid term type.
     * @deprecated Use {@link TermType} instead.
     */
    @Deprecated
    public static TermType fromValue(int value) {
        switch (value) {
            case 1: return APPL;
            case 2: return LIST;
            case 3: return INT;
            case 4: return REAL;
            case 5: return STRING;
            case 6: return CTOR;
            case 7: return TUPLE;
            case 8: return REF;
            case 9: return BLOB;
            case 10: return PLACEHOLDER;
            default:
                throw new IllegalArgumentException("The value " + value + " does not represent a valid term type.");
        }
    }
}