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
 * Tests the {@link IStrategoString} interface.
 */
@DisplayName("IStrategoString")
@SuppressWarnings("unused")
public interface IStrategoStringTests {

    interface Fixture extends IStrategoTermTests.Fixture, IStrategoNamedTests.Fixture {

        /**
         * Creates a new instance of {@link IStrategoString} for testing.
         *
         * @param value       the value of the term; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws TestAbortedException when an instance with the given parameters could not be created
         */
        IStrategoString createIStrategoString(@Nullable String value, @Nullable IStrategoList annotations,
                                              @Nullable List<ITermAttachment> attachments);

        @Override
        default IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                  @Nullable IStrategoList annotations,
                                                  @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createIStrategoString(null, annotations, attachments);
        }

        @Override
        default IStrategoNamed createIStrategoNamed() {
            return createIStrategoString(null, null, null);
        }

    }


    /**
     * Tests the {@link IStrategoString#stringValue()} method.
     */
    @DisplayName("stringValue()")
    interface StringValueTests extends Fixture {

        @Test
        @DisplayName("returns the value of the term")
        default void returnsTheValueOfTheTerm() {
            // Arrange
            String value = "abc";
            IStrategoString sut = createIStrategoString(value, null, null);

            // Act
            String result = sut.stringValue();

            // Assert
            assertEquals(value, result);
        }

    }


    /**
     * Tests the {@link IStrategoString#getName()} method.
     */
    @DisplayName("getName()")
    interface GetNameTests extends Fixture, IStrategoNamedTests.GetNameTests {

        @Test
        @DisplayName("returns the constructor name as the name")
        default void returnsANonEmptyStringAsTheName() {
            // Arrange
            String value = "abc";
            IStrategoString sut = createIStrategoString(value, null, null);

            // Act
            String result = sut.getName();

            // Assert
            assertEquals(value, result);
        }

    }


    /**
     * Tests the {@link IStrategoString#getTermType()} method.
     */
    @DisplayName("getTermType()")
    interface GetTermTypeTests extends Fixture, IStrategoTermTests.GetTermTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoString sut = createIStrategoString(null, null, null);

            // Act
            int result = sut.getTermType();

            // Assert
            assertEquals(IStrategoTerm.STRING, result);
        }

    }


    /**
     * Tests the {@link IStrategoString#getSubtermCount()} method.
     */
    @DisplayName("getSubtermCount()")
    interface GetSubtermCountTests extends Fixture, IStrategoTermTests.GetSubtermCountTests {

        @Test
        @DisplayName("alwaysReturnsZero")
        default void alwaysReturnsZero() {
            // Arrange
            IStrategoTerm sut = createIStrategoString(null, null, null);

            // Act
            int result = sut.getSubtermCount();

            // Assert
            assertEquals(0, result);
        }

    }


    /**
     * Tests the {@link IStrategoString#getAllSubterms()} method.
     */
    @DisplayName("getAllSubterms(int)")
    interface GetAllSubtermTests extends Fixture, IStrategoTermTests.GetAllSubtermTests {

        @Test
        @DisplayName("always returns empty array")
        default void alwaysReturnsEmptyArray() {
            // Arrange
            IStrategoTerm sut = createIStrategoString(null, null, null);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(0, result.length);
        }

    }


    /**
     * Tests the {@link IStrategoString#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends Fixture, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both have the same value, returns true")
        @SuppressWarnings("StringOperationCanBeSimplified")
        default void whenBothHaveTheSameValue_returnsTrue() {
            // Arrange
            IStrategoString sut = createIStrategoString("abc", null, null);
            IStrategoString other = createIStrategoString(new String("abc"), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different value, returns false")
        default void whenOtherHasDifferentValue_returnsFalse() {
            // Arrange
            IStrategoString sut = createIStrategoString("abc", null, null);
            IStrategoString other = createIStrategoString("DEF", null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

    }


    /**
     * Tests the {@link IStrategoString#toString(int)} and {@link IStrategoString#toString()} methods.
     */
    @DisplayName("toString(..)")
    interface ToStringTests extends Fixture, IStrategoTermTests.ToStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() {
            // Arrange
            IStrategoString sut = createIStrategoString("abc", null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("\"abc\"", result);
        }

    }


    /**
     * Tests the {@link IStrategoString#writeAsString(Appendable, int)} and
     * {@link IStrategoString#writeAsString(Appendable)}
     * methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends Fixture, IStrategoTermTests.WriteAsStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() throws IOException {
            // Arrange
            StringBuilder sb = new StringBuilder();
            IStrategoString sut = createIStrategoString("abc", null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("\"abc\"", sb.toString());
        }

    }

}
