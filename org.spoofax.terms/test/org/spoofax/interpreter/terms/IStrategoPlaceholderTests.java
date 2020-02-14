package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.spoofax.TestBase.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link IStrategoPlaceholder} interface.
 */
@SuppressWarnings("unused")
@DisplayName("IStrategoPlaceholder")
public interface IStrategoPlaceholderTests extends IStrategoTermTests {

    /**
     * Creates a new instance of the {@link IStrategoPlaceholder} for testing.
     *
     * @param template the template of the placeholder; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    IStrategoPlaceholder createStrategoPlaceholder(IStrategoTerm template, IStrategoList annotations, List<ITermAttachment> attachments);

    @Override
    default IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations,
                                             @Nullable List<ITermAttachment> attachments) {
        if (subterms == null || subterms.size() != 1)  throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
        return createStrategoPlaceholder(subterms.get(0), annotations, attachments);
    }

    /**
     * Tests the {@link IStrategoPlaceholder#getTemplate()} method.
     */
    @DisplayName("getTemplate()")
    interface GetTemplateTests extends IStrategoPlaceholderTests {

        @Test
        @DisplayName("returns the template of the term")
        default void returnsTheTemplateOfTheTerm() {
            // Arrange
            IStrategoTerm template = new DummyStrategoTerm();
            IStrategoPlaceholder sut = createStrategoPlaceholder(template, null, null);

            // Act
            IStrategoTerm result = sut.getTemplate();

            // Assert
            assertSame(template, result);
        }

    }

    /**
     * Tests the {@link IStrategoPlaceholder#getTermType()} method.
     */
    @DisplayName("getTermType()")
    interface GetTermTypeTests extends IStrategoPlaceholderTests, IStrategoTermTests.GetTermTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoPlaceholder sut = createStrategoPlaceholder(null, null, null);

            // Act
            int result = sut.getTermType();

            // Assert
            assertEquals(IStrategoTerm.PLACEHOLDER, result);
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#getSubtermCount()} method.
     */
    @DisplayName("getSubtermCount()")
    interface GetSubtermCountTests extends IStrategoPlaceholderTests, IStrategoTermTests.GetSubtermCountTests {

        @Test
        @DisplayName("alwaysReturnsOne")
        default void alwaysReturnsOne() {
            // Arrange
            IStrategoTerm sut = createStrategoPlaceholder(null, null, null);

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
    interface GetAllSubtermTests extends IStrategoPlaceholderTests, IStrategoTermTests.GetAllSubtermTests {

        @Test
        @DisplayName("always returns one element array")
        default void alwaysReturnsOneElementArray() {
            // Arrange
            IStrategoTerm sut = createStrategoPlaceholder(null, null, null);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(1, result.length);
        }

    }


    /**
     * Tests the {@link IStrategoPlaceholder#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends IStrategoPlaceholderTests, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both have the same template, returns true")
        default void whenBothHaveTheSameTemplate_returnsTrue() {
            // Arrange
            DummyStrategoTerm template = new DummyStrategoTerm();
            IStrategoPlaceholder sut = createStrategoPlaceholder(template, null, null);
            IStrategoPlaceholder other = createStrategoPlaceholder(template, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different value, returns false")
        default void whenOtherHasDifferentValue_returnsFalse() {
            // Arrange
            IStrategoPlaceholder sut = createStrategoPlaceholder(new DummyStrategoTerm(), null, null);
            IStrategoPlaceholder other = createStrategoPlaceholder(new DummyStrategoTerm(), null, null);

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
    interface ToStringTests extends IStrategoPlaceholderTests, IStrategoTermTests.ToStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() {
            // Arrange
            IStrategoPlaceholder sut = createStrategoPlaceholder(getTermBuilder().makeString("template"), null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("<\"template\">", result);
        }
    }


    /**
     * Tests the {@link IStrategoPlaceholder#writeAsString(Appendable, int)} and {@link IStrategoPlaceholder#writeAsString(Appendable)} methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends IStrategoPlaceholderTests, IStrategoTermTests.WriteAsStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() throws IOException {
            // Arrange
            StringBuilder sb = new StringBuilder();
            IStrategoPlaceholder sut = createStrategoPlaceholder(getTermBuilder().makeString("template"), null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("<\"template\">", sb.toString());
        }

    }
}
