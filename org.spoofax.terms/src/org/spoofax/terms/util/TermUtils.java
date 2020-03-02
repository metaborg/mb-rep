package org.spoofax.terms.util;

import org.spoofax.interpreter.terms.*;

import javax.annotation.Nullable;
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
        return term instanceof IStrategoString;
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
        return term instanceof IStrategoInt;
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
        return term instanceof IStrategoReal;
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
     * Determines whether the given term is a constructor application term.
     *
     * @param term the term to check
     * @return {@code true} when the term is a constructor application term; otherwise, {@code false}
     */
    public static boolean isAppl(IStrategoTerm term) {
        return term instanceof IStrategoAppl;
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
        return term instanceof IStrategoList;
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
     */
    public static IStrategoString toString(IStrategoTerm term) {
        return asString(term).orElseThrow(() -> newTermCastException(IStrategoTerm.STRING, term.getTermType()));
    }

    /**
     * Converts the term to an Int term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a List term.
     */
    public static IStrategoInt toInt(IStrategoTerm term) {
        return asInt(term).orElseThrow(() -> newTermCastException(IStrategoTerm.INT, term.getTermType()));
    }

    /**
     * Converts the term to a Real term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a List term.
     */
    public static IStrategoReal toReal(IStrategoTerm term) {
        return asReal(term).orElseThrow(() -> newTermCastException(IStrategoTerm.REAL, term.getTermType()));
    }

    /**
     * Converts the term to a constructor Application term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a List term.
     */
    public static IStrategoAppl toAppl(IStrategoTerm term) {
        return asAppl(term).orElseThrow(() -> newTermCastException(IStrategoTerm.APPL, term.getTermType()));
    }

    /**
     * Converts the term to a List term.
     *
     * @param term the term
     * @return the converted term
     * @throws ClassCastException The term is not a List term.
     */
    public static IStrategoList toList(IStrategoTerm term) {
        return asList(term).orElseThrow(() -> newTermCastException(IStrategoTerm.LIST, term.getTermType()));
    }



    /// Term Conversions to Java

    /**
     * Returns the given term as a Java string.
     *
     * @param term the term
     * @return the Java string
     * @throws ClassCastException The term is not a String term.
     */
    public static String toJavaString(IStrategoTerm term) {
        return asJavaString(term).orElseThrow(() -> newTermCastException(IStrategoTerm.STRING, term.getTermType()));
    }

    /**
     * Returns the given term as a Java integer.
     *
     * @param term the term
     * @return the Java integer
     * @throws ClassCastException The term is not an Int term.
     */
    public static int toJavaInt(IStrategoTerm term) {
        return asJavaInt(term).orElseThrow(() -> newTermCastException(IStrategoTerm.INT, term.getTermType()));
    }

    /**
     * Returns the given term as a Java real value (double).
     *
     * @param term the term
     * @return the Java real value (double)
     * @throws ClassCastException The term is not a Real term.
     */
    public static double toJavaReal(IStrategoTerm term) {
        return asJavaReal(term).orElseThrow(() -> newTermCastException(IStrategoTerm.REAL, term.getTermType()));
    }

    /**
     * Returns the given term as a Java list.
     *
     * @param term the term
     * @return the Java unmodifiable list
     * @throws ClassCastException The term is not a List term.
     */
    public static List<IStrategoTerm> toJavaList(IStrategoTerm term) {
        return asJavaList(term).orElseThrow(() -> newTermCastException(IStrategoTerm.LIST, term.getTermType()));
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
            if (0 <= index && index < term.getSubtermCount())
                return Optional.of(term.getSubterm(index));
            else
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
     */
    public static IStrategoString toStringAt(IStrategoTerm term, int index) {
        return asString(term.getSubterm(index)).orElseThrow(() -> newTermCastException(IStrategoTerm.STRING, term.getTermType(), index));
    }

    /**
     * Converts the subterm to an Int term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     */
    public static IStrategoInt toIntAt(IStrategoTerm term, int index) {
        return asInt(term.getSubterm(index)).orElseThrow(() -> newTermCastException(IStrategoTerm.INT, term.getTermType(), index));
    }

    /**
     * Converts the subterm to a Real term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     */
    public static IStrategoReal toRealAt(IStrategoTerm term, int index) {
        return asReal(term.getSubterm(index)).orElseThrow(() -> newTermCastException(IStrategoTerm.REAL, term.getTermType(), index));
    }

    /**
     * Converts the subterm to a constructor Application term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     */
    public static IStrategoAppl toApplAt(IStrategoTerm term, int index) {
        return asAppl(term.getSubterm(index)).orElseThrow(() -> newTermCastException(IStrategoTerm.APPL, term.getTermType(), index));
    }

    /**
     * Converts the subterm to a List term.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the converted subterm
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     */
    public static IStrategoList toListAt(IStrategoTerm term, int index) {
        return asList(term.getSubterm(index)).orElseThrow(() -> newTermCastException(IStrategoTerm.LIST, term.getTermType(), index));
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
     */
    public static String toJavaStringAt(IStrategoTerm term, int index) {
        return asJavaString(term.getSubterm(index)).orElseThrow(() -> newTermCastException(IStrategoTerm.STRING, term.getTermType(), index));
    }

    /**
     * Returns the given subterm as a Java integer.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the Java integer
     * @throws ClassCastException The subterm is not an Int term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     */
    public static int toJavaIntAt(IStrategoTerm term, int index) {
        return asJavaInt(term.getSubterm(index)).orElseThrow(() -> newTermCastException(IStrategoTerm.INT, term.getTermType(), index));
    }

    /**
     * Returns the given subterm as a Java real value (double).
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the Java real value (double)
     * @throws ClassCastException The subterm is not a Real term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     */
    public static double toJavaRealAt(IStrategoTerm term, int index) {
        return asJavaReal(term.getSubterm(index)).orElseThrow(() -> newTermCastException(IStrategoTerm.REAL, term.getTermType(), index));
    }

    /**
     * Returns the given subterm as a Java list.
     *
     * @param term the term whose subterm to get
     * @param index the zero-based index of the subterm
     * @return the Java unmodifiable list
     * @throws ClassCastException The subterm is not a List term.
     * @throws IndexOutOfBoundsException The index is is out of bounds.
     */
    public static List<IStrategoTerm> toJavaListAt(IStrategoTerm term, int index) {
        return asJavaList(term.getSubterm(index)).orElseThrow(() -> newTermCastException(IStrategoTerm.LIST, term.getTermType(), index));
    }

    /**
     * Creates a new {@link ClassCastException} for a term conversion.
     *
     * @param expectedType the expected term type
     * @param actualType the actual term type
     * @return the {@link ClassCastException}
     */
    private static ClassCastException newTermCastException(int expectedType, int actualType) {
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
    private static ClassCastException newTermCastException(int expectedType, int actualType, int index) {
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
     * Converts a term type to a string representation.
     *
     * @param type the type to convert
     * @return the string representation
     */
    private static String termTypeToString(int type) {
        switch(type) {
            case IStrategoTerm.APPL: return "a Constructor Application";
            case IStrategoTerm.LIST: return "a List";
            case IStrategoTerm.INT: return "an Int";
            case IStrategoTerm.REAL: return "a Real";
            case IStrategoTerm.STRING: return "a String";
            case IStrategoTerm.CTOR: return "a Constructor";
            case IStrategoTerm.TUPLE: return "a Tuple";
            case IStrategoTerm.REF: return "a Ref";
            case IStrategoTerm.BLOB: return "a Blob";
            case IStrategoTerm.PLACEHOLDER: return "a Placeholder";
            default: return "an unrecognized";
        }
    }

}

