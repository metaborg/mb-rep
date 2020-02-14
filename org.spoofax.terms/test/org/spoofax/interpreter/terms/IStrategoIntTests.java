package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.spoofax.TestBase.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link IStrategoInt} interface.
 */
@SuppressWarnings("unused")
@DisplayName("IStrategoInt")
public interface IStrategoIntTests extends IStrategoTermTests {

    /**
     * Creates a new instance of the {@link IStrategoInt} for testing.
     *
     * @param value the value of the term; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    IStrategoInt createStrategoInt(@Nullable Integer value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

    @Override
    default IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations,
                                             @Nullable List<ITermAttachment> attachments) {
        if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
        return createStrategoInt(null, annotations, attachments);
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
            IStrategoInt sut = createStrategoInt(value, null, null);

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
            IStrategoInt sut = createStrategoInt(null, null, null);

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
            IStrategoTerm sut = createStrategoInt(null, null, null);

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
            IStrategoTerm sut = createStrategoInt(null, null, null);

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
            IStrategoInt sut = createStrategoInt(42, null, null);
            IStrategoInt other = createStrategoInt(42, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different value, returns false")
        default void whenOtherHasDifferentValue_returnsFalse() {
            // Arrange
            IStrategoInt sut = createStrategoInt(42, null, null);
            IStrategoInt other = createStrategoInt(1337, null, null);

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
            IStrategoInt sut = createStrategoInt(42, null, null);

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
            StringBuilder sb = new StringBuilder();
            IStrategoInt sut = createStrategoInt(42, null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("42", sb.toString());
        }

    }
}
