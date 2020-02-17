package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;
import static org.spoofax.TestUtils.getTermBuilder;


/**
 * Tests the {@link IStrategoTerm} interface.
 */
@SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef", "unused"})
public interface IStrategoTermTests extends ISimpleTermTests {

    /**
     * Creates a new instance of {@link IStrategoTerm} for testing.
     *
     * @param subterms the subterms of the term; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

    @Override
    default ISimpleTerm createISimpleTerm(@Nullable List<ISimpleTerm> subterms, @Nullable List<ITermAttachment> attachments) {
        try {
            List<IStrategoTerm> newSubterms = subterms != null ? subterms.stream().map(t -> (IStrategoTerm)t).collect(Collectors.toList()) : null;
            return createIStrategoTerm(newSubterms, TermFactory.EMPTY_LIST, attachments);
        } catch (ClassCastException e) {
            throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
        }
    }

    /**
     * Tests the {@link IStrategoTerm#getSubtermCount()} method.
     */
    @DisplayName("getSubtermCount()")
    interface GetSubtermCountTests extends IStrategoTermTests {

        @Test
        @DisplayName("returns the number of subterms")
        default void returnsTheNumberOfSubterms() {
            // Arrange
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(subterms, null, null);

            // Act
            int result = sut.getSubtermCount();

            // Assert
            assertEquals(3, result);
        }

        @Test
        @DisplayName("returns zero when the term has no subterms")
        default void returnsZeroWhenTheTermHasNoSubterms() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(Collections.emptyList(), null, null);

            // Act
            int result = sut.getSubtermCount();

            // Assert
            assertEquals(0, result);
        }

    }

    /**
     * Tests the {@link IStrategoTerm#getSubterm(int)} method.
     */
    @DisplayName("getSubterm(int)")
    interface GetSubtermTests extends IStrategoTermTests {

        @Test
        @DisplayName("returns the subterm at the specified index")
        default void returnsTheSubtermAtTheSpecifiedIndex() {
            // Arrange
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(subterms, null, null);

            // Act
            IStrategoTerm result = sut.getSubterm(1);

            // Assert
            assertSame(subterms.get(1), result);
        }

        @Test
        @DisplayName("throws exception when below bounds")
        default void throwsExceptionWhenBelowBounds() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(null,null, null);

            // Act/Assert
            assertThrows(IndexOutOfBoundsException.class, () -> {
                sut.getSubterm(-1);
            });
        }

        @Test
        @DisplayName("throws exception when above bounds of term without subterms")
        default void throwsExceptionWhenAboveBoundsOfTermWithoutSubterms() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(Collections.emptyList(), null, null);

            // Act/Assert
            assertThrows(IndexOutOfBoundsException.class, () -> {
                sut.getSubterm(1);
            });
        }

        @Test
        @DisplayName("throws exception when above bounds")
        default void throwsExceptionWhenAboveBounds() {
            // Arrange
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(subterms, null, null);

            // Act/Assert
            assertThrows(IndexOutOfBoundsException.class, () -> {
                sut.getSubterm(3);
            });
        }

    }

    /**
     * Tests the {@link IStrategoTerm#getAllSubterms()} method.
     */
    @DisplayName("getAllSubterms(int)")
    interface GetAllSubtermTests extends IStrategoTermTests {

        @Test
        @DisplayName("when it has no subterms, returns empty array")
        default void whenItHasNoSubterms_returnsEmptyArray() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(Collections.emptyList(), null, null);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("when it has subterms, returns array of subterms")
        default void whenItHasSubterms_returnsEmptyArray() {
            // Arrange
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(subterms, null, null);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(subterms, Arrays.asList(result));
        }

        @Test
        @Disabled("FIXME: Modifications to the returned array must not be reflected in the term.")
        @DisplayName("modifications to returned array are not reflected in the term")
        default void modificationsToReturnedArrayAreNotReflectedInTheTerm() {
            // Arrange
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(subterms, null, null);

            // Act
            DummyStrategoTerm replacement = new DummyStrategoTerm();
            sut.getAllSubterms()[1] = replacement;
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(subterms, Arrays.asList(result));
        }

    }


    /**
     * Tests the {@link IStrategoTerm#getTermType()} method.
     */
    @DisplayName("getTermType()")
    interface GetTermTypeTests extends IStrategoTermTests {

        @Test
        @DisplayName("returns a valid term type")
        default void returnsAValidTermType() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(null, null, null);

            // Act
            int result = sut.getTermType();

            // Assert
            assertTrue(
                    result == IStrategoTerm.APPL ||
                    result == IStrategoTerm.LIST ||
                    result == IStrategoTerm.INT ||
                    result == IStrategoTerm.REAL ||
                    result == IStrategoTerm.STRING ||
                    result == IStrategoTerm.CTOR ||
                    result == IStrategoTerm.TUPLE ||
                    result == IStrategoTerm.REF ||
                    result == IStrategoTerm.BLOB ||
                    result == IStrategoTerm.PLACEHOLDER
            );
        }

    }

    /**
     * Tests the {@link IStrategoTerm#getAnnotations()} method.
     */
    @DisplayName("getAnnotations()")
    interface GetAnnotationsTests extends IStrategoTermTests {

        @Test
        @DisplayName("returns the annotation list term that was used to construct it")
        default void returnsAValidTermType() {
            // Arrange
            IStrategoList annotations = getTermBuilder().makeList(
                    new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(Collections.emptyList(), annotations, null);

            // Act
            IStrategoList result = sut.getAnnotations();

            // Assert
            assertSame(annotations, result);
        }

        @Test
        @DisplayName("when there are no annotations, returns an empty list")
        default void whenThereAreNoAnnotations_returnsAnEmptyList() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(null, null, null);

            // Act
            IStrategoList result = sut.getAnnotations();

            // Assert
            assertTrue(result.isEmpty());
        }

    }

    /**
     * Tests the {@link IStrategoTerm#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends IStrategoTermTests {

        @Test
        @DisplayName("when compared to itself (with children), returns true")
        default void whenComparedToItself_withChildren_returnsTrue() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm()), null, null);

            // Act
            boolean result = sut.match(sut);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when compared to itself (without children), returns true")
        default void whenComparedToItself_withoutChildren_returnsTrue() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(Collections.emptyList(), null, null);

            // Act
            boolean result = sut.match(sut);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when compared to an identical copy, returns true")
        default void whenComparedToAnIdenticalCopy_returnsTrue() {
            // Arrange
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(subterms, null, null);
            IStrategoTerm other = createIStrategoTerm(subterms, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when compared to a copy with different subterms, returns false")
        default void whenComparedToACopyWithDifferentSubterms_returnsFalse() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm()), null, null);
            IStrategoTerm other = createIStrategoTerm(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm()), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when compared to a copy with a different number of subterms, returns false")
        default void whenComparedToACopyWithADifferentNumberOfSubterms_returnsFalse() {
            // Arrange
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(),
                    new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(subterms, null, null);
            IStrategoTerm other = createIStrategoTerm(subterms.subList(0, 2), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when compared to a different kind of term, returns false")
        default void whenComparedToADifferentKindOfTerm_returnsFalse() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(),
                    new DummyStrategoTerm()), null, null);
            IStrategoTerm other = new DummyStrategoTerm();

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }


        @Test
        @DisplayName("when compared to identical copy with identical annotations, returns true")
        default void whenComparedToIdenticalCopyWithIdenticalAnnotations_returnsTrue() {
            // Arrange
            IStrategoList annotations = getTermBuilder().makeList(
                    new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(Collections.emptyList(), annotations, null);
            IStrategoTerm other = createIStrategoTerm(Collections.emptyList(), annotations, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when compared to copy with different annotations, returns false")
        default void whenComparedToCopyWithDifferentAnnotations_returnsFalse() {
            // Arrange
            IStrategoList annotations1 = getTermBuilder().makeList(
                    new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoList annotations2 = getTermBuilder().makeList(
                    new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createIStrategoTerm(Collections.emptyList(), annotations1, null);
            IStrategoTerm other = createIStrategoTerm(Collections.emptyList(), annotations2, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

    }


    /**
     * Tests the {@link IStrategoTerm#toString(int)} and {@link IStrategoTerm#toString()} methods.
     */
    @DisplayName("toString(..)")
    interface ToStringTests extends IStrategoTermTests {

        @Test
        @DisplayName("returns a non-empty string")
        default void returnsANonEmptyString() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(null, null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("when depth is zero, returns a non-empty string")
        default void whenDepthIsZero_returnsANonEmptyString() {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(null, null, null);

            // Act
            String result = sut.toString(0);

            // Assert
            assertFalse(result.isEmpty());
        }

    }


    /**
     * Tests the {@link IStrategoTerm#writeAsString(Appendable, int)} and {@link IStrategoTerm#writeAsString(Appendable)} methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends IStrategoTermTests {

        @Test
        @DisplayName("writes a non-empty string")
        default void writesANonEmptyString() throws IOException {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(null, null, null);
            StringBuilder sb = new StringBuilder();

            // Act
            sut.writeAsString(sb);

            // Assert
            assertFalse(sb.toString().isEmpty());
        }

        @Test
        @DisplayName("when depth is zero, writes a non-empty string")
        default void whenDepthIsZero_writesANonEmptyString() throws IOException {
            // Arrange
            IStrategoTerm sut = createIStrategoTerm(null, null, null);
            StringBuilder sb = new StringBuilder();

            // Act
            sut.writeAsString(sb, 0);

            // Assert
            assertFalse(sb.toString().isEmpty());
        }

    }
}
