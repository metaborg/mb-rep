package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link IStrategoInt} interface.
 */
@DisplayName("IStrategoInt")
@SuppressWarnings("unused")
public interface IStrategoIntTests {

    interface Fixture extends IStrategoTermTests.Fixture {

        /**
         * Creates a new instance of {@link IStrategoInt} for testing (with fixed hashCode 0).
         *
         * @param value       the value of the term; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        IStrategoInt createIStrategoInt(@Nullable Integer value, @Nullable IStrategoList annotations,
                                        @Nullable List<ITermAttachment> attachments);

        @Override
        default IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                  @Nullable IStrategoList annotations,
                                                  @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createIStrategoInt(null, annotations, attachments);
        }

    }


    /**
     * Tests the {@link IStrategoInt#intValue()} method.
     */
    @DisplayName("intValue()")
    interface IntValueTests extends Fixture {

        @Test
        @DisplayName("returns the value of the term")
        default void returnsTheValueOfTheTerm() {
            // Arrange
            int value = 10;
            IStrategoInt sut = createIStrategoInt(value, null, null);

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
    interface GetTermTypeTests extends Fixture, IStrategoTermTests.GetTermTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoInt sut = createIStrategoInt(null, null, null);

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
    interface GetSubtermCountTests extends Fixture, IStrategoTermTests.GetSubtermCountTests {

        @Test
        @DisplayName("alwaysReturnsZero")
        default void alwaysReturnsZero() {
            // Arrange
            IStrategoTerm sut = createIStrategoInt(null, null, null);

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
    interface GetAllSubtermsTests extends Fixture, IStrategoTermTests.GetAllSubtermsTests {

        @Test
        @DisplayName("always returns empty array")
        default void alwaysReturnsEmptyArray() {
            // Arrange
            IStrategoTerm sut = createIStrategoInt(null, null, null);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(0, result.length);
        }

    }

    /**
     * Tests the {@link IStrategoInt#getSubterms()} method.
     */
    @DisplayName("getSubterms(int)")
    interface GetSubtermsTests extends Fixture, IStrategoTermTests.GetSubtermsTests {

        @Test
        @DisplayName("always returns empty list")
        default void alwaysReturnsEmptyList() {
            // Arrange
            IStrategoTerm sut = createIStrategoInt(null, null, null);

            // Act
            List<IStrategoTerm> result = sut.getSubterms();

            // Assert
            assertEquals(0, result.size());
        }

    }


    /**
     * Tests the {@link IStrategoInt#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends Fixture, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both have the same value, returns true")
        default void whenBothHaveTheSameValue_returnsTrue() {
            // Arrange
            IStrategoInt sut = createIStrategoInt(42, null, null);
            IStrategoInt other = createIStrategoInt(42, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different value, returns false")
        default void whenOtherHasDifferentValue_returnsFalse() {
            // Arrange
            IStrategoInt sut = createIStrategoInt(42, null, null);
            IStrategoInt other = createIStrategoInt(1337, null, null);

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
    interface ToStringTests extends Fixture, IStrategoTermTests.ToStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() {
            // Arrange
            IStrategoInt sut = createIStrategoInt(42, null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("42", result);
        }

    }


    /**
     * Tests the {@link IStrategoInt#writeAsString(Appendable, int)} and {@link IStrategoInt#writeAsString(Appendable)}
     * methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends Fixture, IStrategoTermTests.WriteAsStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() throws IOException {
            // Arrange
            StringBuilder sb = new StringBuilder();
            IStrategoInt sut = createIStrategoInt(42, null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("42", sb.toString());
        }

    }

}
