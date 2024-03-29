package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;
import static org.spoofax.TestUtils.getTermBuilder;


/**
 * Tests the {@link IStrategoPlaceholder} interface.
 */
@DisplayName("IStrategoPlaceholder")
@SuppressWarnings("unused")
public interface IStrategoPlaceholderTests {

    interface Fixture extends IStrategoTermTests.Fixture {

        /**
         * Creates a new instance of {@link IStrategoPlaceholder} for testing (with fixed hashCode 0).
         *
         * @param template    the template of the placeholder; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        IStrategoPlaceholder createIStrategoPlaceholder(IStrategoTerm template, IStrategoList annotations,
                                                        List<ITermAttachment> attachments);

        @Override
        default IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                  @Nullable IStrategoList annotations,
                                                  @Nullable List<ITermAttachment> attachments) {
            if (subterms == null || subterms.size() != 1) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createIStrategoPlaceholder(subterms.get(0), annotations, attachments);
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#getTemplate()} method.
     */
    @DisplayName("getTemplate()")
    interface GetTemplateTests extends Fixture {

        @Test
        @DisplayName("returns the template of the term")
        default void returnsTheTemplateOfTheTerm() {
            // Arrange
            IStrategoTerm template = new DummyStrategoTerm();
            IStrategoPlaceholder sut = createIStrategoPlaceholder(template, null, null);

            // Act
            IStrategoTerm result = sut.getTemplate();

            // Assert
            assertSame(template, result);
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#getType()} method.
     */
    @DisplayName("getType()")
    interface GetTypeTests extends Fixture, IStrategoTermTests.GetTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoPlaceholder sut = createIStrategoPlaceholder(null, null, null);

            // Act
            TermType result = sut.getType();

            // Assert
            assertEquals(TermType.PLACEHOLDER, result);
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#getSubtermCount()} method.
     */
    @DisplayName("getSubtermCount()")
    interface GetSubtermCountTests extends Fixture, IStrategoTermTests.GetSubtermCountTests {

        @Test
        @DisplayName("alwaysReturnsOne")
        default void alwaysReturnsOne() {
            // Arrange
            IStrategoTerm sut = createIStrategoPlaceholder(null, null, null);

            // Act
            int result = sut.getSubtermCount();

            // Assert
            assertEquals(1, result);
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#getAllSubterms()} method.
     */
    @DisplayName("getAllSubterms(int)")
    interface GetAllSubtermsTests extends Fixture, IStrategoTermTests.GetAllSubtermsTests {

        @Test
        @DisplayName("always returns one element array")
        default void alwaysReturnsOneElementArray() {
            // Arrange
            IStrategoTerm sut = createIStrategoPlaceholder(null, null, null);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(1, result.length);
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#getSubterms()} method.
     */
    @DisplayName("getSubterms(int)")
    interface GetSubtermsTests extends Fixture, IStrategoTermTests.GetSubtermsTests {

        @Test
        @DisplayName("always returns one element list")
        default void alwaysReturnsOneElementList() {
            // Arrange
            IStrategoTerm sut = createIStrategoPlaceholder(null, null, null);

            // Act
            List<IStrategoTerm> result = sut.getSubterms();

            // Assert
            assertEquals(1, result.size());
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends Fixture, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both have the same template, returns true")
        default void whenBothHaveTheSameTemplate_returnsTrue() {
            // Arrange
            DummyStrategoTerm template = new DummyStrategoTerm();
            IStrategoPlaceholder sut = createIStrategoPlaceholder(template, null, null);
            IStrategoPlaceholder other = createIStrategoPlaceholder(template, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different value, returns false")
        default void whenOtherHasDifferentValue_returnsFalse() {
            // Arrange
            IStrategoPlaceholder sut = createIStrategoPlaceholder(new DummyStrategoTerm(), null, null);
            IStrategoPlaceholder other = createIStrategoPlaceholder(new DummyStrategoTerm(), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#toString(int)} and {@link IStrategoPlaceholder#toString()} methods.
     */
    @DisplayName("toString(..)")
    interface ToStringTests extends Fixture, IStrategoTermTests.ToStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() {
            // Arrange
            IStrategoPlaceholder sut = createIStrategoPlaceholder(getTermBuilder().makeString("template"), null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("<\"template\">", result);
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#writeAsString(Appendable, int)} and {@link
     * IStrategoPlaceholder#writeAsString(Appendable)} methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends Fixture, IStrategoTermTests.WriteAsStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() throws IOException {
            // Arrange
            StringBuilder sb = new StringBuilder();
            IStrategoPlaceholder sut = createIStrategoPlaceholder(getTermBuilder().makeString("template"), null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("<\"template\">", sb.toString());
        }

    }

}
