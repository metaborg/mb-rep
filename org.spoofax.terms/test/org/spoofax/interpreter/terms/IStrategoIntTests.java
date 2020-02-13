package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests the {@link IStrategoInt} interface.
 */
@DisplayName("IStrategoInt")
public interface IStrategoIntTests extends IStrategoTermTests {

    /**
     * Creates a new instance of the {@link IStrategoInt} for testing.
     *
     * @param value the value of the term
     * @param annotations the annotations of the term
     * @param attachments the attachments of the term
     * @return the created object
     */
    IStrategoInt createStrategoInt(int value, IStrategoList annotations, List<ITermAttachment> attachments);

    /**
     * Creates a new instance of the {@link IStrategoInt} for testing.
     *
     * @param value the value of the term
     * @param annotations the annotations of the term
     * @return the created object
     */
    default IStrategoInt createStrategoInt(int value, IStrategoList annotations) {
        return createStrategoInt(value, annotations, Collections.emptyList());
    }

    /**
     * Creates a new instance of the {@link IStrategoInt} for testing.
     *
     * @param value the value of the term
     * @return the created object
     */
    default IStrategoInt createStrategoInt(int value) {
        return createStrategoInt(value, TermFactory.EMPTY_LIST);
    }

    @Nullable
    @Override
    default IStrategoTerm createStrategoTerm(List<IStrategoTerm> subterms, IStrategoList annotations,
                                     List<ITermAttachment> attachments) {
        if (subterms != null && subterms.size() != 0) return null;
        return createStrategoInt(42, annotations, attachments);
    }

    /**
     * Tests the {@link IStrategoInt#intValue()} method.
     */
    @DisplayName("intValue()")
    interface IntValueTests extends IStrategoIntTests {

        @Test
        @DisplayName("returns the value of the term")
        default void returnsTheValueOfTheTerm() {
            // Arrange
            int value = 10;
            IStrategoInt sut = createStrategoInt(value);

            // Act
            int result = sut.intValue();

            // Assert
            assertEquals(value, result);
        }

    }

    /**
     * Tests the {@link IStrategoInt#getTermType()} method.
     */
    @DisplayName("getTermType()")
    interface GetTermTypeTests extends IStrategoIntTests, IStrategoTermTests.GetTermTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoInt sut = createStrategoInt(42);

            // Act
            int result = sut.getTermType();

            // Assert
            assertEquals(IStrategoTerm.INT, result);
        }

    }


    /**
     * Tests the {@link IStrategoInt#getSubtermCount()} method.
     */
    @DisplayName("getSubtermCount()")
    interface GetSubtermCountTests extends IStrategoIntTests, IStrategoTermTests.GetSubtermCountTests {

        @Test
        @DisplayName("alwaysReturnsZero")
        default void alwaysReturnsZero() {
            // Arrange
            IStrategoTerm sut = createStrategoInt(42);

            // Act
            int result = sut.getSubtermCount();

            // Assert
            assertEquals(0, result);
        }

    }

    /**
     * Tests the {@link IStrategoInt#getAllSubterms()} method.
     */
    @DisplayName("getAllSubterms(int)")
    interface GetAllSubtermTests extends IStrategoIntTests, IStrategoTermTests.GetAllSubtermTests {

        @Test
        @DisplayName("always returns empty array")
        default void alwaysReturnsEmptyArray() {
            // Arrange
            IStrategoTerm sut = createStrategoInt(42);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(0, result.length);
        }

    }


    /**
     * Tests the {@link IStrategoInt#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends IStrategoIntTests, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both have the same value, returns true")
        default void whenBothHaveTheSameValue_returnsTrue() {
            // Arrange
            IStrategoInt sut = createStrategoInt(42);
            IStrategoInt other = createStrategoInt(42);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different value, returns false")
        default void whenOtherHasDifferentValue_returnsFalse() {
            // Arrange
            IStrategoInt sut = createStrategoInt(42);
            IStrategoInt other = createStrategoInt(1337);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

    }


    /**
     * Tests the {@link IStrategoInt#toString(int)} and {@link IStrategoInt#toString()} methods.
     */
    @DisplayName("toString(..)")
    interface ToStringTests extends IStrategoIntTests, IStrategoTermTests.ToStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() {
            // Arrange
            IStrategoInt sut = createStrategoInt(42);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("42", result);
        }
    }


    /**
     * Tests the {@link IStrategoInt#writeAsString(Appendable, int)} and {@link IStrategoInt#writeAsString(Appendable)} methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends IStrategoIntTests, IStrategoTermTests.WriteAsStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() throws IOException {
            // Arrange
            IStrategoInt sut = createStrategoInt(42);
            StringBuilder sb = new StringBuilder();

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("42", sb.toString());
        }

    }
}
