package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.spoofax.terms.attachments.ITermAttachment;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link IStrategoReal} interface.
 */
@DisplayName("IStrategoReal")
@SuppressWarnings("unused")
public interface IStrategoRealTests {

    interface Fixture extends IStrategoTermTests.Fixture {

        /**
         * Creates a new instance of {@link IStrategoReal} for testing (with fixed hashCode 0).
         *
         * @param value       the value of the term; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws TestAbortedException when an instance with the given parameters could not be created
         */
        IStrategoReal createIStrategoReal(@Nullable Double value, @Nullable IStrategoList annotations,
                                          @Nullable List<ITermAttachment> attachments);

        @Override
        default IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                  @Nullable IStrategoList annotations,
                                                  @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createIStrategoReal(null, annotations, attachments);
        }

    }


    /**
     * Tests the {@link IStrategoReal#realValue()} method.
     */
    @DisplayName("realValue()")
    interface RealValueTests extends Fixture {

        @Test
        @DisplayName("returns the value of the term")
        default void returnsTheValueOfTheTerm() {
            // Arrange
            double value = 10.2;
            IStrategoReal sut = createIStrategoReal(value, null, null);

            // Act
            double result = sut.realValue();

            // Assert
            assertEquals(value, result);
        }

    }


    /**
     * Tests the {@link IStrategoReal#getType()} method.
     */
    @DisplayName("getType()")
    interface GetTypeTests extends Fixture, IStrategoTermTests.GetTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoReal sut = createIStrategoReal(null, null, null);

            // Act
            TermType result = sut.getType();

            // Assert
            assertEquals(TermType.REAL, result);
        }

    }


    /**
     * Tests the {@link IStrategoReal#getSubtermCount()} method.
     */
    @DisplayName("getSubtermCount()")
    interface GetSubtermCountTests extends Fixture, IStrategoTermTests.GetSubtermCountTests {

        @Test
        @DisplayName("alwaysReturnsZero")
        default void alwaysReturnsZero() {
            // Arrange
            IStrategoTerm sut = createIStrategoReal(null, null, null);

            // Act
            int result = sut.getSubtermCount();

            // Assert
            assertEquals(0, result);
        }

    }


    /**
     * Tests the {@link IStrategoReal#getAllSubterms()} method.
     */
    @DisplayName("getAllSubterms(int)")
    interface GetAllSubtermsTests extends Fixture, IStrategoTermTests.GetAllSubtermsTests {

        @Test
        @DisplayName("always returns empty array")
        default void alwaysReturnsEmptyArray() {
            // Arrange
            IStrategoTerm sut = createIStrategoReal(null, null, null);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(0, result.length);
        }

    }

    /**
     * Tests the {@link IStrategoReal#getSubterms()} method.
     */
    @DisplayName("getSubterms(int)")
    interface GetSubtermsTests extends Fixture, IStrategoTermTests.GetSubtermsTests {

        @Test
        @DisplayName("always returns empty list")
        default void alwaysReturnsEmptyList() {
            // Arrange
            IStrategoTerm sut = createIStrategoReal(null, null, null);

            // Act
            List<IStrategoTerm> result = sut.getSubterms();

            // Assert
            assertEquals(0, result.size());
        }

    }


    /**
     * Tests the {@link IStrategoReal#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends Fixture, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both have the same value, returns true")
        default void whenBothHaveTheSameValue_returnsTrue() {
            // Arrange
            IStrategoReal sut = createIStrategoReal(13.37, null, null);
            IStrategoReal other = createIStrategoReal(13.37, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different value, returns false")
        default void whenOtherHasDifferentValue_returnsFalse() {
            // Arrange
            IStrategoReal sut = createIStrategoReal(4.2, null, null);
            IStrategoReal other = createIStrategoReal(13.37, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

    }


    /**
     * Tests the {@link IStrategoReal#toString(int)} and {@link IStrategoReal#toString()} methods.
     */
    @DisplayName("toString(..)")
    interface ToStringTests extends Fixture, IStrategoTermTests.ToStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() {
            // Arrange
            IStrategoReal sut = createIStrategoReal(4.2, null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("4.2", result);
        }

    }


    /**
     * Tests the {@link IStrategoReal#writeAsString(Appendable, int)} and
     * {@link IStrategoReal#writeAsString(Appendable)}
     * methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends Fixture, IStrategoTermTests.WriteAsStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() throws IOException {
            // Arrange
            StringBuilder sb = new StringBuilder();
            IStrategoReal sut = createIStrategoReal(4.2, null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("4.2", sb.toString());
        }

    }

}
