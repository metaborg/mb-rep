package org.spoofax.interpreter.terms;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.DummyStrategoTermWithHashCode;
import org.spoofax.DummyTermAttachment;
import org.spoofax.DummyTermAttachmentType;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.ITermAttachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.spoofax.TestUtils.getTermBuilder;


/**
 * Tests the {@link IStrategoList} interface.
 */
@DisplayName("IStrategoList")
@SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef", "unused"})
public interface IStrategoListTests {

    interface Fixture extends IStrategoTermTests.Fixture {

        /**
         * Creates a new instance of {@link IStrategoList} for testing.
         *
         * @param elements    the elements of the list; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        default IStrategoList createIStrategoList(@Nullable List<IStrategoTerm> elements,
                                                  @Nullable IStrategoList annotations,
                                                  @Nullable List<ITermAttachment> attachments) {
            if (elements == null || elements.isEmpty()) {
                return createEmptyIStrategoList(annotations, attachments);
            }
            return createConsNilIStrategoList(elements.get(0), createIStrategoList(elements.subList(1,
                    elements.size()), null, null), annotations, attachments);
        }

        /**
         * Creates a new instance of a cons-nil {@link IStrategoList} for testing.
         *
         * @param head        the head of the list; or {@code null} to use a sensible default
         * @param tail        the tail of the list; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        IStrategoList createConsNilIStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail,
                                                 @Nullable IStrategoList annotations,
                                                 @Nullable List<ITermAttachment> attachments);

        /**
         * Creates a new instance of an empty {@link IStrategoList} for testing.
         * <p>
         * This creates an empty list.
         *
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        IStrategoList createEmptyIStrategoList(@Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments);

        @Override
        default IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                  @Nullable IStrategoList annotations,
                                                  @Nullable List<ITermAttachment> attachments) {
            return createIStrategoList(subterms, annotations, attachments);
        }

    }


    /**
     * Tests the {@link IStrategoList#size()} method.
     */
    @DisplayName("size()")
    interface SizeTests extends Fixture {

        @Test
        @DisplayName("when the list is empty, returns zero")
        default void whenTheListIsEmpty_returnsZero() {
            // Arrange
            IStrategoList sut = createEmptyIStrategoList(null, null);

            // Act
            int result = sut.size();

            // Assert
            assertEquals(0, result);
        }

        @Test
        @DisplayName("when the list is not empty, returns the size")
        default void whenTheListIsNotEmpty_returnsTheSize() {
            // Arrange
            List<IStrategoTerm> elements = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoList sut = createIStrategoList(elements, null, null);

            // Act
            int result = sut.size();

            // Assert
            assertEquals(2, result);
        }

    }


    /**
     * Tests the {@link IStrategoList#head()} method.
     */
    @DisplayName("head()")
    interface HeadTests extends Fixture {

        @Test
        @DisplayName("when the list is empty, throws exception")
        default void whenTheListIsEmpty_throwsException() {
            // Arrange
            IStrategoList sut = createEmptyIStrategoList(null, null);

            // Act/Assert
            assertThrows(NoSuchElementException.class, () -> {
                sut.head();
            });
        }

        @Test
        @DisplayName("when the list is not empty, returns the first element")
        default void whenTheListIsNotEmpty_returnsTheFirstElement() {
            // Arrange
            List<IStrategoTerm> elements = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoList sut = createIStrategoList(elements, null, null);

            // Act
            IStrategoTerm result = sut.head();

            // Assert
            assertSame(elements.get(0), result);
        }

    }


    /**
     * Tests the {@link IStrategoList#tail()} method.
     */
    @DisplayName("tail()")
    interface TailTests extends Fixture {

        @Test
        @DisplayName("when the list is empty, throws exception")
        default void whenTheListIsEmpty_throwsException() {
            // Arrange
            IStrategoList sut = createEmptyIStrategoList(null, null);

            // Act/Assert
            assertThrows(IllegalStateException.class, () -> {
                sut.tail();
            });
        }

        @Test
        @DisplayName("when the list is not empty, returns the tail list")
        default void whenTheListIsNotEmpty_returnsTheTailList() {
            // Arrange
            List<IStrategoTerm> elements = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoList sut = createIStrategoList(elements, null, null);

            // Act
            IStrategoList result = sut.tail();

            // Assert
            assertEquals(elements.subList(1, elements.size()), Arrays.asList(result.getAllSubterms()));
        }

        @Test
        @DisplayName("when the list is a singleton list, returns the empty list")
        default void whenTheListIsASingletonList_returnsTheEmptyList() {
            // Arrange
            IStrategoList sut = createIStrategoList(Collections.singletonList(new DummyStrategoTerm()), null, null);

            // Act
            IStrategoList result = sut.tail();

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("when tail has annotations, returns the annotations too")
        default void whenTailHasAnnotations_returnsTheAnnotationsToo() {
            // Arrange
            IStrategoList a0 = getTermBuilder().makeList(new DummyStrategoTerm());
            IStrategoList l0 = createEmptyIStrategoList(a0, null);
            IStrategoList a1 = getTermBuilder().makeList(new DummyStrategoTerm());
            IStrategoList l1 = createConsNilIStrategoList(new DummyStrategoTerm(), l0, a1, null);
            IStrategoList a2 = getTermBuilder().makeList(new DummyStrategoTerm());
            IStrategoList sut = createConsNilIStrategoList(new DummyStrategoTerm(), l1, a2, null);

            // Act
            IStrategoList result1 = sut.tail();
            IStrategoList result0 = result1.tail();

            // Assert
            assertSame(sut.getAnnotations(), a2);
            assertSame(result1.getAnnotations(), a1);
            assertSame(result0.getAnnotations(), a0);
        }

        @Test
        @DisplayName("when tail has attachments, returns the attachments too")
        default void whenTailHasAttachments_returnsTheAttachmentsToo() {
            // Arrange
            DummyTermAttachmentType<DummyTermAttachment> attachmentType = DummyTermAttachment.Type1;
            ITermAttachment a0 = new DummyTermAttachment(attachmentType);
            IStrategoList l0 = createEmptyIStrategoList(TermFactory.EMPTY_LIST, Collections.singletonList(a0));
            ITermAttachment a1 = new DummyTermAttachment(attachmentType);
            IStrategoList l1 = createConsNilIStrategoList(new DummyStrategoTerm(), l0, TermFactory.EMPTY_LIST,
                    Collections.singletonList(a1));
            ITermAttachment a2 = new DummyTermAttachment(attachmentType);
            IStrategoList sut = createConsNilIStrategoList(new DummyStrategoTerm(), l1, TermFactory.EMPTY_LIST,
                    Collections.singletonList(a2));

            // Act
            IStrategoList result1 = sut.tail();
            IStrategoList result0 = result1.tail();

            // Assert
            assertSame(sut.getAttachment(attachmentType), a2);
            assertSame(result1.getAttachment(attachmentType), a1);
            assertSame(result0.getAttachment(attachmentType), a0);
        }

    }


    /**
     * Tests the {@link IStrategoList#isEmpty()} method.
     */
    @DisplayName("isEmpty()")
    interface IsEmptyTests extends Fixture {

        @Test
        @DisplayName("when the list is empty, returns true")
        default void whenTheListIsEmpty_returnsTrue() {
            // Arrange
            IStrategoList sut = createEmptyIStrategoList(null, null);

            // Act
            boolean result = sut.isEmpty();

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when the list is not empty, returns false")
        default void whenTheListIsNotEmpty_returnsFalse() {
            // Arrange
            IStrategoList sut = createIStrategoList(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm()),
                    null, null);

            // Act
            boolean result = sut.isEmpty();

            // Assert
            assertFalse(result);
        }

    }


    /**
     * Tests the {@link IStrategoList#getTermType()} method.
     */
    @DisplayName("getTermType()")
    interface GetTermTypeTests extends Fixture, IStrategoTermTests.GetTermTypeTests {

        @Test
        @DisplayName("empty list, returns the correct term type")
        default void emptyList_returnsTheCorrectTermType() {
            // Arrange
            IStrategoList sut = createEmptyIStrategoList(null, null);

            // Act
            int result = sut.getTermType();

            // Assert
            assertEquals(IStrategoTerm.LIST, result);
        }

        @Test
        @DisplayName("non-empty list, returns the correct term type")
        default void nonEmptyList_returnsTheCorrectTermType() {
            // Arrange
            List<IStrategoTerm> elements = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoList sut = createIStrategoList(elements, null, null);

            // Act
            int result = sut.getTermType();

            // Assert
            assertEquals(IStrategoTerm.LIST, result);
        }

    }


    /**
     * Tests the {@link IStrategoList#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends Fixture, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both are empty lists, returns true")
        default void whenBothAreEmptyLists_returnsTrue() {
            // Arrange
            IStrategoList sut = createIStrategoList(Collections.emptyList(), null, null);
            IStrategoList other = createIStrategoList(Collections.emptyList(), null, null);

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
            IStrategoList sut = createIStrategoList(elements, null, null);
            IStrategoList other = createIStrategoList(elements, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has differing elements, returns false")
        default void whenOtherHasDifferingElements_returnsFalse() {
            // Arrange
            IStrategoList sut = createIStrategoList(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm()),
                    null, null);
            IStrategoList other = createIStrategoList(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm())
                    , null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when other has different number of elements, returns false")
        default void whenOtherHasDifferentNumberOfElements_returnsFalse() {
            // Arrange
            IStrategoList sut = createIStrategoList(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm()),
                    null, null);
            IStrategoList other = createIStrategoList(Arrays.asList(new DummyStrategoTerm()), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when one is empty, returns false")
        default void whenOneIsEmpty_returnsFalse() {
            // Arrange
            IStrategoList sut = createIStrategoList(Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm()),
                    null, null);
            IStrategoList other = createEmptyIStrategoList(null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Disabled("BUG: Annotations on sublists are not currently compared.")
        @Test
        @DisplayName("when one has different annotations on a sublist, returns false")
        default void whenOneHasDifferentAnnotationsOnASublist_returnsFalse() {
            // Arrange
            IStrategoList anno1 = getTermBuilder().makeList(new DummyStrategoTerm());
            IStrategoList anno2 = getTermBuilder().makeList(new DummyStrategoTerm());
            DummyStrategoTerm t0 = new DummyStrategoTerm();
            DummyStrategoTerm t1 = new DummyStrategoTerm();
            IStrategoList sut = createConsNilIStrategoList(t1, createConsNilIStrategoList(t0,
                    createEmptyIStrategoList(null, null), anno1, null), null, null);
            IStrategoList other = createConsNilIStrategoList(t1, createConsNilIStrategoList(t0,
                    createEmptyIStrategoList(null, null), anno2, null), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Disabled("BUG: Annotations on sublists are not currently compared.")
        @Test
        @DisplayName("when one has different annotations on the empty list, returns false")
        default void whenOneHasDifferentAnnotationsOnTheEmptyList_returnsFalse() {
            // Arrange
            IStrategoList anno1 = getTermBuilder().makeList(new DummyStrategoTerm());
            IStrategoList anno2 = getTermBuilder().makeList(new DummyStrategoTerm());
            DummyStrategoTerm t0 = new DummyStrategoTerm();
            DummyStrategoTerm t1 = new DummyStrategoTerm();
            IStrategoList sut = createConsNilIStrategoList(t1, createConsNilIStrategoList(t0,
                    createEmptyIStrategoList(anno1, null), null, null), null, null);
            IStrategoList other = createConsNilIStrategoList(t1, createConsNilIStrategoList(t0,
                    createEmptyIStrategoList(anno2, null), null, null), null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

    }


    /**
     * Tests the {@link IStrategoList#hashCode()} method.
     */
    @DisplayName("hashCode()")
    interface HashCodeTests extends Fixture {

        @Test
        @DisplayName("when the list is empty, returns one")
        default void whenTheListIsEmpty_returnsOne() {
            // Arrange
            IStrategoList sut = createEmptyIStrategoList(null, null);

            // Act
            int result = sut.hashCode();

            // Assert
            assertEquals(1, result);
        }

        @Test
        @DisplayName("when the list is not empty, returns the hashCode")
        default void whenTheListIsNotEmpty_returnsTheHashCode() {
            // Arrange
            List<IStrategoTerm> elements = Arrays.asList(new DummyStrategoTermWithHashCode(0xCAFEBABE), new DummyStrategoTermWithHashCode(0xDEADBEEF));
            IStrategoList sut = createIStrategoList(elements, null, null);

            // Act
            int result = sut.hashCode();

            // Assert
            assertEquals(-454507987, result);
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
            IStrategoList sut = createIStrategoList(Arrays.asList(new DummyStrategoTerm("Dummy1"), new DummyStrategoTerm("Dummy2")),
                    null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("[<Dummy1>,<Dummy2>]", result);
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
            IStrategoList sut = createIStrategoList(Arrays.asList(new DummyStrategoTerm("Dummy1"), new DummyStrategoTerm("Dummy2")),
                    null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("[<Dummy1>,<Dummy2>]", sb.toString());
        }

    }


    /**
     * Tests the {@link ISimpleTerm#putAttachment(ITermAttachment)} method.
     */
    @DisplayName("putAttachment(ITermAttachment)")
    interface PutAttachmentTests extends Fixture {

        @Test
        @DisplayName("when an attachment is put on a tail, renavigating to that tail, it still has the attachment")
        default void whenAnAttachmentPutOnTail_renavigatingToTail_stillHasAttachment() {
            // Arrange
            DummyTermAttachment attachment =
                new DummyTermAttachment(DummyTermAttachment.Type1);
            IStrategoList sut = createIStrategoList(
                Arrays.asList(createIStrategoTerm(null, null, null), createIStrategoTerm(null, null, null)), null,
                null);

            // Act
            sut.tail().putAttachment(attachment);

            // Assert
            assertSame(attachment, sut.tail().getAttachment(DummyTermAttachment.Type1));
        }

    }

}
