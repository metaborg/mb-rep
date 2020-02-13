package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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


/**
 * Tests the {@link IStrategoTerm} interface.
 */
@DisplayName("IStrategoTerm")
public interface IStrategoTermTests extends ISimpleTermTests {

    /**
     * Gets a term builder.
     *
     * @return the term builder
     */
    IStrategoTermBuilder getTermBuilder();

    /**
     * Creates a new instance of the {@link IStrategoTerm} for testing.
     *
     * @param subterms the subterms of the term
     * @param annotations the annotations of the term
     * @param attachments the attachments of the term
     * @return the created object;
     * or {@code null} when an instance with the given subterms or attachments could not be created
     */
    @Nullable
    IStrategoTerm createStrategoTerm(List<IStrategoTerm> subterms, IStrategoList annotations, List<ITermAttachment> attachments);

    /**
     * Creates a new instance of the {@link IStrategoTerm} for testing.
     *
     * @param subterms the subterms of the term
     * @param annotations the annotations of the term
     * @return the created object;
     * or {@code null} when an instance with the given subterms could not be created
     */
    @Nullable
    default IStrategoTerm createStrategoTerm(List<IStrategoTerm> subterms, IStrategoList annotations) {
        return createStrategoTerm(subterms, annotations, Collections.emptyList());
    }

    /**
     * Creates a new instance of the {@link IStrategoTerm} for testing.
     *
     * @param subterms the subterms of the term
     * @return the created object;
     * or {@code null} when an instance with the given subterms could not be created
     */
    @Nullable default IStrategoTerm createStrategoTerm(List<IStrategoTerm> subterms) {
        return createStrategoTerm(subterms, TermFactory.EMPTY_LIST);
    }

    /**
     * Creates a new instance of the {@link IStrategoTerm} for testing.
     *
     * @return the created object
     */
    default IStrategoTerm createStrategoTerm() {
        @Nullable IStrategoTerm term = createStrategoTerm(Collections.emptyList());
        if (term == null) throw new IllegalStateException();
        return term;
    }

    @Nullable
    @Override
    default ISimpleTerm createSimpleTerm(List<ISimpleTerm> subterms, List<ITermAttachment> attachments) {
        try {
            List<IStrategoTerm> newSubterms = subterms.stream().map(t -> (IStrategoTerm)t).collect(Collectors.toList());
            return createStrategoTerm(newSubterms, TermFactory.EMPTY_LIST, attachments);
        } catch (ClassCastException e) {
            return null;
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
            IStrategoTerm sut = createStrategoTerm(subterms);

            // Assume
            assumeTrue(sut != null, "The implementation does not support multiple subterms.");

            // Act
            int result = sut.getSubtermCount();

            // Assert
            assertEquals(3, result);
        }

        @Test
        @DisplayName("returns zero when the term has no subterms")
        default void returnsZeroWhenTheTermHasNoSubterms() {
            // Arrange
            IStrategoTerm sut = createStrategoTerm(Collections.emptyList());

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms.");

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
            IStrategoTerm sut = createStrategoTerm(subterms);

            // Assume
            assumeTrue(sut != null, "The implementation does not support multiple subterms.");

            // Act
            IStrategoTerm result = sut.getSubterm(1);

            // Assert
            assertSame(subterms.get(1), result);
        }

        @Test
        @DisplayName("throws exception when below bounds")
        default void throwsExceptionWhenBelowBounds() {
            // Arrange
            IStrategoTerm sut = createStrategoTerm();

            // Act/Assert
            assertThrows(IndexOutOfBoundsException.class, () -> {
                sut.getSubterm(-1);
            });
        }

        @Test
        @DisplayName("throws exception when above bounds of term without subterms")
        default void throwsExceptionWhenAboveBoundsOfTermWithoutSubterms() {
            // Arrange
            IStrategoTerm sut = createStrategoTerm(Collections.emptyList());

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms.");

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
            IStrategoTerm sut = createStrategoTerm(subterms);

            // Assume
            assumeTrue(sut != null, "The implementation does not support multiple subterms.");

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
            IStrategoTerm sut = createStrategoTerm(Collections.emptyList());

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms.");

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
            IStrategoTerm sut = createStrategoTerm(subterms);

            // Assume
            assumeTrue(sut != null, "The implementation does not support multiple subterms.");

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(subterms, Arrays.asList(result));
        }

        @Test
        @Disabled("This is not currently implemented.")
        @DisplayName("modifications to returned array are not reflected in the term")
        default void modificationsToReturnedArrayAreNotReflectedInTheTerm() {
            // Arrange
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTerm sut = createStrategoTerm(subterms);

            // Assume
            assumeTrue(sut != null, "The implementation does not support multiple subterms.");

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
            IStrategoTerm sut = createStrategoTerm();

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
            IStrategoTerm sut = createStrategoTerm(Collections.emptyList(), annotations);

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms.");

            // Act
            IStrategoList result = sut.getAnnotations();

            // Assert
            assertSame(annotations, result);
        }

        @Test
        @DisplayName("when there are no annotations, returns an empty list")
        default void whenThereAreNoAnnotations_returnsAnEmptyList() {
            // Arrange
            IStrategoTerm sut = createStrategoTerm();

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
            IStrategoTerm sut = createStrategoTerm(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm()));

            // Assume
            assumeTrue(sut != null, "The implementation does not support multiple subterms.");

            // Act
            boolean result = sut.match(sut);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when compared to itself (without children), returns true")
        default void whenComparedToItself_withoutChildren_returnsTrue() {
            // Arrange
            IStrategoTerm sut = createStrategoTerm(Collections.emptyList());

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms.");

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
            IStrategoTerm sut = createStrategoTerm(subterms);
            IStrategoTerm other = createStrategoTerm(subterms);

            // Assume
            assumeTrue(sut != null && other != null, "The implementation does not support multiple subterms.");

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when compared to a copy with different subterms, returns false")
        default void whenComparedToACopyWithDifferentSubterms_returnsFalse() {
            // Arrange
            IStrategoTerm sut = createStrategoTerm(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm()));
            IStrategoTerm other = createStrategoTerm(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(), new DummyStrategoTerm()));

            // Assume
            assumeTrue(sut != null && other != null, "The implementation does not support multiple subterms.");

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
            IStrategoTerm sut = createStrategoTerm(subterms);
            IStrategoTerm other = createStrategoTerm(subterms.subList(0, 2));

            // Assume
            assumeTrue(sut != null && other != null, "The implementation does not support multiple subterms.");

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when compared to a different kind of term, returns false")
        default void whenComparedToADifferentKindOfTerm_returnsFalse() {
            // Arrange
            IStrategoTerm sut = createStrategoTerm(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(),
                    new DummyStrategoTerm()));
            IStrategoTerm other = new DummyStrategoTerm();

            // Assume
            assumeTrue(sut != null, "The implementation does not support multiple subterms.");

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
            IStrategoTerm sut = createStrategoTerm(Collections.emptyList(), annotations);
            IStrategoTerm other = createStrategoTerm(Collections.emptyList(), annotations);

            // Assume
            assumeTrue(sut != null && other != null, "The implementation does not support zero subterms.");

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
            IStrategoTerm sut = createStrategoTerm(Collections.emptyList(), annotations1);
            IStrategoTerm other = createStrategoTerm(Collections.emptyList(), annotations2);

            // Assume
            assumeTrue(sut != null && other != null, "The implementation does not support zero subterms.");

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
            IStrategoTerm sut = createStrategoTerm();

            // Act
            String result = sut.toString();

            // Assert
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("when depth is zero, returns a non-empty string")
        default void whenDepthIsZero_returnsANonEmptyString() {
            // Arrange
            IStrategoTerm sut = createStrategoTerm();

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
            IStrategoTerm sut = createStrategoTerm();
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
            IStrategoTerm sut = createStrategoTerm();
            StringBuilder sb = new StringBuilder();

            // Act
            sut.writeAsString(sb, 0);

            // Assert
            assertFalse(sb.toString().isEmpty());
        }

    }
}
