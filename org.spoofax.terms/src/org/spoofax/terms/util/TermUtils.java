package org.spoofax.terms.util;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoPlaceholder;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.TermType;

import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Functions for working with terms.
 */
public final class TermUtils {

    private TermUtils() {}  // Prevent instantiation.


    /// Term Assertions

    /**
     * Determines whether the given term is a String term.
     *
     * @param term the term to check
     * @return {@code true} when the term is a String term; otherwise, {@code false}
     */
    public static boolean isString(IStrategoTerm term) {
        boolean isString = (term != null && term.getType() == TermType.STRING);
        assert !isString || (term instanceof IStrategoString) : getTypeMismatchAssertionMessage(IStrategoString.class, TermType.STRING, term);
        return isString;
    }

    /**
     * Determines whether the given term is a String term with the specified value.
     *
     * @param term the term to check
     * @param value the expected value of the term
     * @return {@code true} when the term is a String term with the specified value; otherwise, {@code false}
     */
    public static boolean isString(IStrategoTerm term, String value) {
        return isString(term) && ((IStrategoString)term).stringValue().equals(value);
    }

    /**
     * Determines whether the given term is an Int term.
     *
     * @param term the term to check
     * @return {@code true} when the term is an Int term; otherwise, {@code false}
     */
    public static boolean isInt(IStrategoTerm term) {
        boolean isInt = (term != null && term.getType() == TermType.INT);
        assert !isInt || (term instanceof IStrategoInt) : getTypeMismatchAssertionMessage(IStrategoInt.class, TermType.INT, term);
        return isInt;
    }

    /**
     * Determines whether the given term is an Int term with the specified value.
     *
     * @param term the term to check
     * @param value the expected value of the term
     * @return {@code true} when the term is an Int term with the specified value; otherwise, {@code false}
     */
    public static boolean isInt(IStrategoTerm term, int value) {
        return isInt(term) && ((IStrategoInt)term).intValue() == value;
    }

    /**
     * Determines whether the given term is a Real term.
     *
     * @param term the term to check
     * @return {@code true} when the term is a Real term; otherwise, {@code false}
     */
    public static boolean isReal(IStrategoTerm term) {
        boolean isReal = (term != null && term.getType() == TermType.REAL);
        assert !isReal || (term instanceof IStrategoReal) : getTypeMismatchAssertionMessage(IStrategoReal.class, TermType.REAL, term);
        return isReal;
    }

    /**
     * Determines whether the given term is a Real term with the specified value.
     *
     * @param term the term to check
     * @param value the expected value of the term
     * @return {@code true} when the term is a Real term with the specified value; otherwise, {@code false}
     */
    public static boolean isReal(IStrategoTerm term, double value) {
        return isReal(term) && fuzzyEquals(((IStrategoReal)term).realValue(), value);
    }

    /**
     * Determines whether the given term is a constructor application term.
     *
     * @param term the term to check
     * @return {@code true} when the term is a constructor application term; otherwise, {@code false}
     */
    public static boolean isAppl(IStrategoTerm term) {
        boolean isAppl = (term != null && term.getType() == TermType.APPL);
        assert !isAppl || (term instanceof IStrategoAppl) : getTypeMismatchAssertionMessage(IStrategoAppl.class, TermType.APPL, term);
        return isAppl;
    }

    /**
     * Determines whether the given term is a constructor application term with the specified constructor.
     *
     * @param term the term to check
     * @param constructor the expected constructor of the term
     * @return {@code true} when the term is a constructor application term with the specified constructor; otherwise, {@code false}
     */
    public static boolean isAppl(IStrategoTerm term, IStrategoConstructor constructor) {
        return isAppl(term) && ((IStrategoAppl) term).getConstructor().equals(constructor);
    }

    /**
     * Determines whether the given term is a constructor application term with the specified constructor name
     * and any arity.
     *
     * @param term the term to check
     * @param constructorName the expected constructor name; or {@code null} to accept any constructor name
     * @return {@code true} when the term is a constructor application term with the specified constructor name and any arity;
     * otherwise, {@code false}
     */
    public static boolean isAppl(IStrategoTerm term, @Nullable String constructorName) {
        return isAppl(term, constructorName, -1);
    }

    /**
     * Determines whether the given term is a constructor application term with the specified constructor name and arity.
     *
     * @param term the term to check
     * @param constructorName the expected constructor name; or {@code null} to accept any constructor name
     * @param arity the expected arity; or -1 to accept any arity
     * @return {@code true} when the term is a constructor application term with the specified constructor name and arity;
     * otherwise, {@code false}
     */
    public static boolean isAppl(IStrategoTerm term, @Nullable String constructorName, int arity) {
        if (!isAppl(term)) return false;
        IStrategoConstructor constructor = ((IStrategoAppl) term).getConstructor();
        // @formatter:off
        return (constructorName == null || constructor.getName().equals(constructorName))
            && (arity < 0 || constructor.getArity() == arity);
        // @formatter:on
    }

    /**
     * Determines whether the given term is a List term.
     *
     * @param term the term to check
     * @return {@code true} when the term is a List term; otherwise, {@code false}
     */
    public static boolean isList(IStrategoTerm term) {
        boolean isList = (term != null && term.getType() == TermType.LIST);
        assert !isList || (term instanceof IStrategoList) : getTypeMismatchAssertionMessage(IStrategoList.class, TermType.LIST, term);
        return isList;
    }

    /**
     * Determines whether the given term is a List term with the specified size.
     *
     * @param term the term to check
     * @param size the expected size of the term
     * @return {@code true} when the term is a List term with the specified size; otherwise, {@code false}
     */
    public static boolean isList(IStrategoTerm term, int size) {
        return isList(term) && ((IStrategoList)term).size() == size;
    }

    /**
     * Determines whether the given term is a Tuple term.
     *
     * @param term the term to check
     * @return {@code true} when the term is a Tuple term; otherwise, {@code false}
     */
    public static boolean isTuple(IStrategoTerm term) {
        boolean isTuple = (term != null && term.getType() == TermType.TUPLE);
        assert !isTuple || (term instanceof IStrategoTuple) : getTypeMismatchAssertionMessage(IStrategoTuple.class, TermType.TUPLE, term);
        return isTuple;
    }

    /**
     * Determines whether the given term is a Tuple term with the specified size.
     *
     * @param term the term to check
     * @param size the expected size of the term
     * @return {@code true} when the term is a Tuple term with the specified size; otherwise, {@code false}
     */
    public static boolean isTuple(IStrategoTerm term, int size) {
        return isTuple(term) && ((IStrategoTuple)term).size() == size;
    }

    /**
     * Determines whether the given term is a Placeholder term.
     *
     * @param term the term to check
     * @return {@code true} when the term is a Placeholder term; otherwise, {@code false}
     */
    public static boolean isPlaceholder(IStrategoTerm term) {
        boolean isTuple = (term != null && term.getType() == TermType.PLACEHOLDER);
        assert !isTuple || (term instanceof IStrategoPlaceholder) : getTypeMismatchAssertionMessage(IStrategoTuple.class, TermType.PLACEHOLDER, term);
        return isTuple;
    }

    /// Term Conversions

    /**
     * Converts the term to a String term, if possible.
     *
     * @param term the term
     * @return an option with the converted term when the term is a String term; otherwise, nothing
     */
    public static Optional<IStrategoString> asString(IStrategoTerm term) {
        return isString(term) ? Optional.of((IStrategoString)term) : Optional.empty();
    }

    /**
     * Converts the term to an Int term, if possible.
     *
     * @param term the term
     * @return an option with the converted term when the term is an Int term; otherwise, nothing
     */
    public static Optional<IStrategoInt> asInt(IStrategoTerm term) {
        return isInt(term) ? Optional.of((IStrategoInt)term) : Optional.empty();
    }

    /**
     * Converts the term to a Real term, if possible.
     *
     * @param term the term
     * @return an option with the converted term when the term is a Real term; otherwise, nothing
     */
    public static Optional<IStrategoReal> asReal(IStrategoTerm term) {
        return isReal(term) ? Optional.of((IStrategoReal)term) : Optional.empty();
    }

    /**
     * Converts the term to a constructor Application term, if possible.
     *
     * @param term the term
     * @return an option with the converted term when the term is a constructor Application term; otherwise, nothing
     */
    public static Optional<IStrategoAppl> asAppl(IStrategoTerm term) {
        return isAppl(term) ? Optional.of((IStrategoAppl)term) : Optional.empty();
    }

    /**
     * Converts the term to a List term, if possible.
     *
     * @param term the term
     * @return an option with the converted term when the term is a List term; otherwise, nothing
     */
    public static Optional<IStrategoList> asList(IStrategoTerm term) {
        return isList(term) ? Optional.of((IStrategoList)term) : Optional.empty();
    }

    /**
     * Converts the term to a Tuple term, if possible.
     *
     * @param term the term
     * @return an option with the converted term when the term is a Tuple term; otherwise, nothing
     */
    public static Optional<IStrategoTuple> asTuple(IStrategoTerm term) {
        return isTuple(term) ? Optional.of((IStrategoTuple)term) : Optional.empty();
    }

    /**
     * Converts the term to a Placeholder term, if possible.
     *
     * @param term the term
     * @return an option with the converted term when the term is a Placeholder term; otherwise, nothing
     */
    public static Optional<IStrategoPlaceholder> asPlaceholder(IStrategoTerm term) {
        return isPlaceholder(term) ? Optional.of((IStrategoPlaceholder)term) : Optional.empty();
    }



    /// Term Conversions to Java

    /**
     * Returns the given term as a Java string, if possible.
     *
     * @param term the term
     * @return an option with the Java string when the term is a String term; otherwise, nothing
     */
    public static Optional<String> asJavaString(IStrategoTerm term) {
        return asString(term).map(IStrategoString::stringValue);
    }

    /**
     * Returns the given term as a Java integer, if possible.
     *
     * @param term the term
     * @return an option with the Java integer when the term is a Int term; otherwise, nothing
     */
    public static Optional<Integer> asJavaInt(IStrategoTerm term) {
        return asInt(term).map(IStrategoInt::intValue);
    }

    /**
     * Returns the given term as a Java real value (double), if possible.
     *
     * @param term the term
     * @return an option with the Java real value (double) when the term is a Real term; otherwise, nothing
     */
    public static Optional<Double> asJavaReal(IStrategoTerm term) {
        return asReal(term).map(IStrategoReal::realValue);
    }

    /**
     * Returns the given term as a Java list, if possible.
     *
     * @param term the term
     * @return an option with the Java unmodifiable list when the term is a List term; otherwise, nothing
     */
    public static Optional<List<IStrategoTerm>> asJavaList(IStrategoTerm term) {
        return asList(term).map(t -> {
            // TODO: Get the term's immutable subterm list instead
            return Collections.unmodifiableList(Arrays.asList(t.getAllSubterms()));
        });
    }


    /// Term Conversions or Exception

    /**
     * Converts the term to a String term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a List term.
     * @throws NullPointerException The term is null.
     */
    public static IStrategoString toString(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.STRING);
        return asString(term).orElseThrow(() -> newTermCastException(TermType.STRING, term.getType()));
    }

    /**
     * Converts the term to an Int term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a List term.
     * @throws NullPointerException The term is null.
     */
    public static IStrategoInt toInt(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.INT);
        return asInt(term).orElseThrow(() -> newTermCastException(TermType.INT, term.getType()));
    }

    /**
     * Converts the term to a Real term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a List term.
     * @throws NullPointerException The term is null.
     */
    public static IStrategoReal toReal(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.REAL);
        return asReal(term).orElseThrow(() -> newTermCastException(TermType.REAL, term.getType()));
    }

    /**
     * Converts the term to a constructor Application term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a List term.
     * @throws NullPointerException The term is null.
     */
    public static IStrategoAppl toAppl(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.APPL);
        return asAppl(term).orElseThrow(() -> newTermCastException(TermType.APPL, term.getType()));
    }

    /**
     * Converts the term to a List term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a List term.
     * @throws NullPointerException The term is null.
     */
    public static IStrategoList toList(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.LIST);
        return asList(term).orElseThrow(() -> newTermCastException(TermType.LIST, term.getType()));
    }

    /**
     * Converts the term to a Tuple term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a Tuple term.
     * @throws NullPointerException The term is null.
     */
    public static IStrategoTuple toTuple(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.TUPLE);
        return asTuple(term).orElseThrow(() -> newTermCastException(TermType.TUPLE, term.getType()));
    }

    /**
     * Converts the term to a Placeholder term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a Placeholder term.
     * @throws NullPointerException The term is null.
     */
    public static IStrategoPlaceholder toPlaceholder(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.PLACEHOLDER);
        return asPlaceholder(term).orElseThrow(() -> newTermCastException(TermType.PLACEHOLDER, term.getType()));
    }



    /// Term Conversions to Java

    /**
     * Returns the given term as a Java string.
     *
     * @param term the term
     * @return the Java string
     * @throws ClassCastException The term is not a String term.
     * @throws NullPointerException The term is null.
     */
    public static String toJavaString(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.STRING);
        return asJavaString(term).orElseThrow(() -> newTermCastException(TermType.STRING, term.getType()));
    }

    /**
     * Returns the given term as a Java integer.
     *
     * @param term the term
     * @return the Java integer
     * @throws ClassCastException The term is not an Int term.
     * @throws NullPointerException The term is null.
     */
    public static int toJavaInt(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.INT);
        return asJavaInt(term).orElseThrow(() -> newTermCastException(TermType.INT, term.getType()));
    }

    /**
     * Returns the given term as a Java real value (double).
     *
     * @param term the term
     * @return the Java real value (double)
     * @throws ClassCastException The term is not a Real term.
     * @throws NullPointerException The term is null.
     */
    public static double toJavaReal(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.REAL);
        return asJavaReal(term).orElseThrow(() -> newTermCastException(TermType.REAL, term.getType()));
    }

    /**
     * Returns the given term as a Java list.
     *
     * @param term the term
     * @return the Java unmodifiable list
     * @throws ClassCastException The term is not a List term.
     * @throws NullPointerException The term is null.
     */
    public static List<IStrategoTerm> toJavaList(IStrategoTerm term) {
        if (term == null) throw newTermNullException(TermType.LIST);
        return asJavaList(term).orElseThrow(() -> newTermCastException(TermType.LIST, term.getType()));
    }


    /**
     * Gets the subterm at the specified index.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the subterm when found; otherwise, nothing
     */
    private static Optional<IStrategoTerm> tryGetTermAt(IStrategoTerm term, int index) {
        try {
            if (0 <= index && index < term.getSubtermCount()) {
                return Optional.ofNullable(term.getSubterm(index));
            } else
                return Optional.empty();
        } catch (IndexOutOfBoundsException e) {
            // This should never happen, but not all implementations are perfect.
            return Optional.empty();
        }
    }

    /// Indexed Subterm Assertions

    /**
     * Determines whether the given subterm is a String term.
     *
     * @param term  the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @return {@code true} when the subterm with the given index exists and is a String term; otherwise, {@code false}
     */
    public static boolean isStringAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).map(TermUtils::isString).orElse(false);
    }

    /**
     * Determines whether the given subterm is a String term with the specified value.
     *
     * @param term  the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @param value the expected value of the subterm
     * @return {@code true} when the subterm with the given index exists and is a String term with the specified value; otherwise, {@code false}
     */
    public static boolean isStringAt(IStrategoTerm term, int index, String value) {
        return tryGetTermAt(term, index).map(t -> isString(t, value)).orElse(false);
    }

    /**
     * Determines whether the given subterm is an Int term.
     *
     * @param term  the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @return {@code true} when the subterm with the given index exists and is an Int term; otherwise, {@code false}
     */
    public static boolean isIntAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).map(TermUtils::isInt).orElse(false);
    }

    /**
     * Determines whether the given subterm is an Int term with the specified value.
     *
     * @param term  the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @param value the expected value of the subterm
     * @return {@code true} when the subterm with the given index exists and is an Int term with the specified value; otherwise, {@code false}
     */
    public static boolean isIntAt(IStrategoTerm term, int index, int value) {
        return tryGetTermAt(term, index).map(t -> isInt(t, value)).orElse(false);
    }

    /**
     * Determines whether the given subterm is a Real term.
     *
     * @param term  the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @return {@code true} when the subterm with the given index exists and is a Real term; otherwise, {@code false}
     */
    public static boolean isRealAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).map(TermUtils::isReal).orElse(false);
    }

    /**
     * Determines whether the given subterm is a Real term with the specified value.
     *
     * @param term  the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @param value the expected value of the subterm
     * @return {@code true} when the subterm with the given index exists and is a Real term with the specified value; otherwise, {@code false}
     */
    public static boolean isRealAt(IStrategoTerm term, int index, double value) {
        return tryGetTermAt(term, index).map(t -> isReal(t, value)).orElse(false);
    }

    /**
     * Determines whether the given subterm is a constructor application term.
     *
     * @param term  the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @return {@code true} when the subterm with the given index exists and is a constructor application term; otherwise, {@code false}
     */
    public static boolean isApplAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).map(TermUtils::isAppl).orElse(false);
    }

    /**
     * Determines whether the given subterm is a constructor application term with the specified constructor.
     *
     * @param term        the term whose subterm to check
     * @param index       the zero-based index of the subterm
     * @param constructor the expected constructor of the subterm
     * @return {@code true} when the subterm with the given index exists and is a constructor application term with the specified constructor; otherwise, {@code false}
     */
    public static boolean isApplAt(IStrategoTerm term, int index, IStrategoConstructor constructor) {
        return tryGetTermAt(term, index).map(t -> isAppl(t, constructor)).orElse(false);
    }

    /**
     * Determines whether the given subterm is a constructor application term with the specified constructor name and any arity.
     *
     * @param term            the term whose subterm to check
     * @param index           the zero-based index of the subterm
     * @param constructorName the expected constructor name; or {@code null} to accept any constructor name
     * @return {@code true} when the subterm with the given index exists and is a constructor application term with the specified constructor name and any arity;
     * otherwise, {@code false}
     */
    public static boolean isApplAt(IStrategoTerm term, int index, @Nullable String constructorName) {
        return isApplAt(term, index, constructorName, -1);
    }

    /**
     * Determines whether the given subterm is a constructor application term with the specified constructor name and arity.
     *
     * @param term            the term whose subterm to check
     * @param index           the zero-based index of the subterm
     * @param constructorName the expected constructor name; or {@code null} to accept any constructor name
     * @param arity           the expected arity; or -1 to accept any arity
     * @return {@code true} when the subterm with the given index exists and is a constructor application term with the specified constructor name and arity;
     * otherwise, {@code false}
     */
    public static boolean isApplAt(IStrategoTerm term, int index, @Nullable String constructorName, int arity) {
        return tryGetTermAt(term, index).map(t -> isAppl(t, constructorName, arity)).orElse(false);
    }

    /**
     * Determines whether the given subterm is a List term.
     *
     * @param term the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @return {@code true} when the subterm with the given index exists and is a List term; otherwise, {@code false}
     */
    public static boolean isListAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).map(TermUtils::isList).orElse(false);
    }

    /**
     * Determines whether the given subterm is a List term with the specified size.
     *
     * @param term the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @param size the expected size of the subterm
     * @return {@code true} when the subterm with the given index exists and is a List term with the specified size; otherwise, {@code false}
     */
    public static boolean isListAt(IStrategoTerm term, int index, int size) {
        return tryGetTermAt(term, index).map(t -> isList(t, size)).orElse(false);
    }

    /**
     * Determines whether the given subterm is a Tuple term.
     *
     * @param term the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @return {@code true} when the subterm with the given index exists and is a Tuple term; otherwise, {@code false}
     */
    public static boolean isTupleAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).map(TermUtils::isTuple).orElse(false);
    }

    /**
     * Determines whether the given subterm is a Tuple term with the specified size.
     *
     * @param term the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @param size the expected size of the subterm
     * @return {@code true} when the subterm with the given index exists and is a Tuple term with the specified size; otherwise, {@code false}
     */
    public static boolean isTupleAt(IStrategoTerm term, int index, int size) {
        return tryGetTermAt(term, index).map(t -> isTuple(t, size)).orElse(false);
    }

    /**
     * Determines whether the given subterm is a Placeholder term.
     *
     * @param term the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @return {@code true} when the subterm with the given index exists and is a Placeholder term; otherwise, {@code false}
     */
    public static boolean isPlaceholderAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).map(TermUtils::isPlaceholder).orElse(false);
    }


    /// Indexed Subterm Conversions

    /**
     * Converts the subterm to a String term, if possible.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the converted subterm when the subterm with the given index exists
     * and is a String term; otherwise, nothing
     */
    public static Optional<IStrategoString> asStringAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asString);
    }

    /**
     * Converts the subterm to an Int term, if possible.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the converted subterm when the subterm with the given index exists
     * and is an Int term; otherwise, nothing
     */
    public static Optional<IStrategoInt> asIntAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asInt);
    }

    /**
     * Converts the subterm to a Real term, if possible.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the converted subterm when the subterm with the given index exists
     * and is a Real term; otherwise, nothing
     */
    public static Optional<IStrategoReal> asRealAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asReal);
    }

    /**
     * Converts the subterm to a constructor Application term, if possible.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the converted subterm when the subterm with the given index exists
     * and is a constructor Application term; otherwise, nothing
     */
    public static Optional<IStrategoAppl> asApplAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asAppl);
    }

    /**
     * Converts the subterm to a List term, if possible.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the converted subterm when the subterm with the given index exists
     * and is a List term; otherwise, nothing
     */
    public static Optional<IStrategoList> asListAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asList);
    }

    /**
     * Converts the subterm to a Tuple term, if possible.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the converted subterm when the subterm with the given index exists
     * and is a Tuple term; otherwise, nothing
     */
    public static Optional<IStrategoTuple> asTupleAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asTuple);
    }

    /**
     * Converts the subterm to a Placeholder term, if possible.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the converted subterm when the subterm with the given index exists
     * and is a Placeholder term; otherwise, nothing
     */
    public static Optional<IStrategoPlaceholder> asPlaceholderAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asPlaceholder);
    }


    /// Indexed Subterm Conversions to Java

    /**
     * Returns the given subterm as a Java string.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the subterm as a Java string when the subterm with the given index exists
     * and is a String term; otherwise, nothing
     */
    public static Optional<String> asJavaStringAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asJavaString);
    }

    /**
     * Returns the given subterm as a Java integer.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the subterm as a Java integer when the subterm with the given index exists
     * and is an Int term; otherwise, nothing
     */
    public static Optional<Integer> asJavaIntAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asJavaInt);
    }

    /**
     * Returns the given subterm as a Java real value (double).
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the subterm as a Java real value (double) when the subterm with the given index exists
     * and is a Real term; otherwise, nothing
     */
    public static Optional<Double> asJavaRealAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asJavaReal);
    }

    /**
     * Returns the given subterm as a Java list.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return an option with the subterm as a Java unmodifiable list when the subterm with the given index exists
     * and is a List term; otherwise, nothing
     */
    public static Optional<List<IStrategoTerm>> asJavaListAt(IStrategoTerm term, int index) {
        return tryGetTermAt(term, index).flatMap(TermUtils::asJavaList);
    }


    /// Indexed Subterm Conversions or Exception

    /**
     * Converts the subterm to a String term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static IStrategoString toStringAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.STRING, index);
        return asString(subterm).orElseThrow(() -> newTermCastException(TermType.STRING, term.getType(), index));
    }

    /**
     * Converts the subterm to an Int term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static IStrategoInt toIntAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.INT, index);
        return asInt(subterm).orElseThrow(() -> newTermCastException(TermType.INT, term.getType(), index));
    }

    /**
     * Converts the subterm to a Real term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static IStrategoReal toRealAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.REAL, index);
        return asReal(subterm).orElseThrow(() -> newTermCastException(TermType.REAL, term.getType(), index));
    }

    /**
     * Converts the subterm to a constructor Application term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static IStrategoAppl toApplAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.APPL, index);
        return asAppl(subterm).orElseThrow(() -> newTermCastException(TermType.APPL, term.getType(), index));
    }

    /**
     * Converts the subterm to a List term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static IStrategoList toListAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.LIST, index);
        return asList(subterm).orElseThrow(() -> newTermCastException(TermType.LIST, term.getType(), index));
    }

    /**
     * Converts the subterm to a Tuple term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a Tuple term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static IStrategoTuple toTupleAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.TUPLE, index);
        return asTuple(subterm).orElseThrow(() -> newTermCastException(TermType.TUPLE, term.getType(), index));
    }

    /**
     * Converts the subterm to a Placeholder term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a Placeholder term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static IStrategoPlaceholder toPlaceholderAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.PLACEHOLDER, index);
        return asPlaceholder(subterm).orElseThrow(() -> newTermCastException(TermType.PLACEHOLDER, term.getType(), index));
    }



    /// Indexed Subterm Conversions to Java

    /**
     * Returns the given subterm as a Java string.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the Java string
     * @throws ClassCastException The subterm is not a String term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static String toJavaStringAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.STRING, index);
        return asJavaString(subterm).orElseThrow(() -> newTermCastException(TermType.STRING, term.getType(), index));
    }

    /**
     * Returns the given subterm as a Java integer.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the Java integer
     * @throws ClassCastException The subterm is not an Int term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static int toJavaIntAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.INT, index);
        return asJavaInt(subterm).orElseThrow(() -> newTermCastException(TermType.INT, term.getType(), index));
    }

    /**
     * Returns the given subterm as a Java real value (double).
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the Java real value (double)
     * @throws ClassCastException The subterm is not a Real term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static double toJavaRealAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.REAL, index);
        return asJavaReal(subterm).orElseThrow(() -> newTermCastException(TermType.REAL, term.getType(), index));
    }

    /**
     * Returns the given subterm as a Java list.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the Java unmodifiable list
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     * @throws NullPointerException The term or the subterm is null.
     */
    public static List<IStrategoTerm> toJavaListAt(IStrategoTerm term, int index) {
        if (term == null) throw newTermNullException();
        IStrategoTerm subterm = term.getSubterm(index);
        if (subterm == null) throw newTermNullException(TermType.LIST, index);
        return asJavaList(subterm).orElseThrow(() -> newTermCastException(TermType.LIST, term.getType(), index));
    }

    // Speciality functions

    /**
     * Returns the name of the given term, if it has any.
     *
     * @param term the term whose name to get
     * @return an option with the name of the term; or nothing when the term has no name
     */
    public static Optional<String> tryGetName(IStrategoTerm term) {
        return asAppl(term).map(t -> t.getConstructor().getName());
    }




    /**
     * Determines whether two floating-point numbers are equal within 1e-30.
     * @param a the first number to compare
     * @param b the second number to compare
     * @return {@code true} when they are nearly equal or equal; otherwise, {@code false}
     */
    private static boolean fuzzyEquals(double a, double b) {
        // Inspired by Guava's DoubleMath.fuzzyEquals()
        double EPSILON = 1e-30;
        // @formatter:off
        return Math.copySign(a - b, 1.0) <= EPSILON
                || (a == b)
                || (Double.isNaN(a) && Double.isNaN(b));
        // @formatter:on
    }

    /**
     * Creates a new {@link ClassCastException} for a term conversion.
     *
     * @param expectedType the expected term type
     * @param actualType the actual term type
     * @return the {@link ClassCastException}
     */
    private static ClassCastException newTermCastException(TermType expectedType, TermType actualType) {
        return newTermCastException(expectedType, actualType, -1);
    }

    /**
     * Creates a new {@link ClassCastException} for a term conversion.
     *
     * @param expectedType the expected term type
     * @param actualType the actual term type
     * @param index the zero-based index of the subterm; or -1 when it is not a subterm
     * @return the {@link ClassCastException}
     */
    private static ClassCastException newTermCastException(TermType expectedType, TermType actualType, int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("Expected ");
        sb.append(termTypeToString(expectedType));
        if (index >= 0) { sb.append(" subterm at index ").append(index); }
        else { sb.append(" term"); }
        sb.append(", but got ");
        sb.append(termTypeToString(actualType));
        if (index >= 0) { sb.append(" subterm."); }
        else { sb.append(" term."); }
        return new ClassCastException(sb.toString());
    }

    /**
     * Creates a new {@link NullPointerException} for a term conversion.
     *
     * @return the {@link NullPointerException}
     */
    private static NullPointerException newTermNullException() {
        return new NullPointerException("Expected a term; got null.");
    }

    /**
     * Creates a new {@link NullPointerException} for a term conversion.
     *
     * @param expectedType the expected term type
     * @return the {@link NullPointerException}
     */
    private static NullPointerException newTermNullException(TermType expectedType) {
        return newTermNullException(expectedType, -1);
    }

    /**
     * Creates a new {@link NullPointerException} for a term conversion.
     *
     * @param expectedType the expected term type
     * @param index the zero-based index of the subterm; or -1 when it is not a subterm
     * @return the {@link NullPointerException}
     */
    private static NullPointerException newTermNullException(TermType expectedType, int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("Expected ");
        sb.append(termTypeToString(expectedType));
        if (index >= 0) { sb.append(" subterm at index ").append(index); }
        else { sb.append(" term"); }
        sb.append(", but got null.");
        return new NullPointerException(sb.toString());
    }

    /**
     * Creates an assertion failed message.
     *
     * @param expectedClass the expected class
     * @param expectedType the expected term type
     * @param term the term that failed the assertion
     * @return the assertion message
     */
    private static String getTypeMismatchAssertionMessage(Class<?> expectedClass, TermType expectedType, IStrategoTerm term) {
        return "Expected " +
                termTypeToString(expectedType) +
                " term to implement interface " +
                expectedClass.getSimpleName() +
                ": " +
                term.getClass().getSimpleName();
    }

    /**
     * Converts a term type to a string representation.
     *
     * @param type the type to convert
     * @return the string representation
     */
    public static String termTypeToString(TermType type) {
        switch(type) {
            case APPL: return "a Constructor Application";
            case LIST: return "a List";
            case INT: return "an Int";
            case REAL: return "a Real";
            case STRING: return "a String";
            case CTOR: return "a Constructor";
            case TUPLE: return "a Tuple";
            case REF: return "a Ref";
            case BLOB: return "a Blob";
            case PLACEHOLDER: return "a Placeholder";
            default: return "an unrecognized";
        }
    }

}

