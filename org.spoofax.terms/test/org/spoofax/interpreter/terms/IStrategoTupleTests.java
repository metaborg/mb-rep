package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests the {@link IStrategoTuple} interface.
 */
@DisplayName("IStrategoTuple")
@SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef", "unused"})
public interface IStrategoTupleTests {

    interface Fixture extends IStrategoTermTests.Fixture {

        /**
         * Creates a new instance of {@link IStrategoTuple} for testing.
         *
         * @param elements    the elements of the tuple; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        IStrategoTuple createIStrategoTuple(@Nullable List<IStrategoTerm> elements,
                                            @Nullable IStrategoList annotations,
                                            @Nullable List<ITermAttachment> attachments);

        @Override
        default IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                  @Nullable IStrategoList annotations,
                                                  @Nullable List<ITermAttachment> attachments) {
            return createIStrategoTuple(subterms, annotations, attachments);
        }

    }


    /**
     * Tests the {@link IStrategoTuple#size()} method.
     */
    @DisplayName("size()")
    interface SizeTests extends Fixture {

        @Test
        @DisplayName("when the tuple is empty, returns zero")
        default void whenTheTupleIsEmpty_returnsZero() {
            // Arrange
            IStrategoTuple sut = createIStrategoTuple(Collections.emptyList(), null, null);

            // Act
            int result = sut.size();

            // Assert
            assertEquals(0, result);
        }

        @Test
        @DisplayName("when the tuple is not empty, returns the size")
        default void whenTheTupleIsNotEmpty_returnsTheSize() {
            // Arrange
            List<IStrategoTerm> elements = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTuple sut = createIStrategoTuple(elements, null, null);

            // Act
            int result = sut.size();

            // Assert
            assertEquals(2, result);
        }

    }


    /**
     * Tests the {@link IStrategoTuple#get(int)} method.
     */
    @DisplayName("get(int)")
    interface GetTests extends Fixture {

        @Test
        @DisplayName("returns the subterm at the specified index")
        default void returnsTheSubtermAtTheSpecifiedIndex() {
            // Arrange
            List<IStrategoTerm> elements = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(),
                    new DummyStrategoTerm());
            IStrategoTuple sut = createIStrategoTuple(elements, null, null);

            // Act
            IStrategoTerm result = sut.get(1);

            // Assert
            assertSame(elements.get(1), result);
        }

        @Test
        @DisplayName("throws exception when below bounds")
        default void throwsExceptionWhenBelowBounds() {
            // Arrange
            IStrategoTuple sut = createIStrategoTuple(null, null, null);

            // Act/Assert
            assertThrows(IndexOutOfBoundsException.class, () -> {
                sut.get(-1);
            });
        }

        @Test
        @DisplayName("throws exception when above bounds of term without subterms")
        default void throwsExceptionWhenAboveBoundsOfTermWithoutSubterms() {
            // Arrange
            IStrategoTuple sut = createIStrategoTuple(Collections.emptyList(), null, null);

            // Act/Assert
            assertThrows(IndexOutOfBoundsException.class, () -> {
                sut.get(1);
            });
        }

        @Test
        @DisplayName("throws exception when above bounds")
        default void throwsExceptionWhenAboveBounds() {
            // Arrange
            List<IStrategoTerm> elements = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm(),
                    new DummyStrategoTerm());
            IStrategoTuple sut = createIStrategoTuple(elements, null, null);

            // Act/Assert
            assertThrows(IndexOutOfBoundsException.class, () -> {
                sut.get(3);
            });
        }

    }


    /**
     * Tests the {@link IStrategoTuple#getTermType()} method.
     */
    @DisplayName("getTermType()")
    interface GetTermTypeTests extends Fixture, IStrategoTermTests.GetTermTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoTuple sut = createIStrategoTuple(null, null, null);

            // Act
            int result = sut.getTermType();

            // Assert
            assertEquals(IStrategoTerm.TUPLE, result);
        }

    }


    /**
     * Tests the {@link IStrategoTuple#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends Fixture, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both are empty tuples, returns true")
        default void whenBothAreEmptyLists_returnsTrue() {
            // Arrange
            IStrategoTuple sut = createIStrategoTuple(Collections.emptyList(), null, null);
            IStrategoTuple other = createIStrategoTuple(Collections.emptyList(), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when both have the same elements, returns true")
        default void whenBothHaveTheSameElements_returnsTrue() {
            // Arrange
            List<IStrategoTerm> elements = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoTuple sut = createIStrategoTuple(elements, null, null);
            IStrategoTuple other = createIStrategoTuple(elements, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has differing elements, returns false")
        default void whenOtherHasDifferingElements_returnsFalse() {
            // Arrange
            IStrategoTuple sut = createIStrategoTuple(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm())
                    , null, null);
            IStrategoTuple other = createIStrategoTuple(Arrays.asList(new DummyStrategoTerm(),
                    new DummyStrategoTerm()), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when other has different number of elements, returns false")
        default void whenOtherHasDifferentNumberOfElements_returnsFalse() {
            // Arrange
            IStrategoTuple sut = createIStrategoTuple(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm())
                    , null, null);
            IStrategoTuple other = createIStrategoTuple(Arrays.asList(new DummyStrategoTerm()), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when one is empty, returns false")
        default void whenOneIsEmpty_returnsFalse() {
            // Arrange
            IStrategoTuple sut = createIStrategoTuple(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm())
                    , null, null);
            IStrategoTuple other = createIStrategoTuple(Collections.emptyList(), null, null);

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
            IStrategoTuple sut = createIStrategoTuple(Arrays.asList(new DummyStrategoTerm("Dummy1"), new DummyStrategoTerm("Dummy2"))
                    , null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("(<Dummy1>,<Dummy2>)", result);
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
            IStrategoTuple sut = createIStrategoTuple(Arrays.asList(new DummyStrategoTerm("Dummy1"), new DummyStrategoTerm("Dummy2"))
                    , null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("(<Dummy1>,<Dummy2>)", sb.toString());
        }

    }

}
