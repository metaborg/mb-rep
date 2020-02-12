package org.spoofax.interpreter.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


/**
 * Tests the {@link ISimpleTerm} interface.
 */
@DisplayName("ISimpleTerm")
public interface ISimpleTermTests {

    /**
     * Creates a new instance of the {@link ISimpleTerm} for testing.
     *
     * @param subterms the subterms of the term
     * @param attachments the attachments of the term
     * @return the created object;
     * or {@code null} when an instance with the given subterms or attachments could not be created
     */
    @Nullable ISimpleTerm createSimpleTerm(List<ISimpleTerm> subterms, List<ITermAttachment> attachments);

    /**
     * Creates a new instance of the {@link ISimpleTerm} for testing.
     *
     * @param subterms the subterms of the term
     * @return the created object;
     * or {@code null} when an instance with the given subterms or attachments could not be created
     */
    @Nullable default ISimpleTerm createSimpleTerm(List<ISimpleTerm> subterms) {
        return createSimpleTerm(subterms, Collections.emptyList());
    }

    /**
     * Tests the {@link ISimpleTerm#getSubtermCount()} method.
     */
    @DisplayName("getSubtermCount()")
    interface GetSubtermCountTests extends ISimpleTermTests {

        @Test
        @DisplayName("returns the number of subterms")
        default void returnsTheNumberOfSubterms() {
            // Arrange
            List<ISimpleTerm> subterms = Arrays.asList(new DummySimpleTerm(), new DummySimpleTerm(), new DummySimpleTerm());
            ISimpleTerm sut = createSimpleTerm(subterms);

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
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList());

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms.");

            // Act
            int result = sut.getSubtermCount();

            // Assert
            assertEquals(0, result);
        }

    }

    /**
     * Tests the {@link ISimpleTerm#getSubterm(int)} method.
     */
    @DisplayName("getSubterm(int)")
    interface GetSubtermTests extends ISimpleTermTests {

        @Test
        @DisplayName("returns the subterm at the specified index")
        default void returnsTheSubtermAtTheSpecifiedIndex() {
            // Arrange
            List<ISimpleTerm> subterms = Arrays.asList(new DummySimpleTerm(), new DummySimpleTerm(), new DummySimpleTerm());
            ISimpleTerm sut = createSimpleTerm(subterms);

            // Assume
            assumeTrue(sut != null, "The implementation does not support multiple subterms.");

            // Act
            ISimpleTerm result = sut.getSubterm(1);

            // Assert
            assertSame(subterms.get(1), result);
        }

        @Test
        @DisplayName("throws exception when below bounds")
        default void throwsExceptionWhenBelowBounds() {
            // Arrange
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList());

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms.");

            // Act/Assert
            assertThrows(IndexOutOfBoundsException.class, () -> {
                sut.getSubterm(-1);
            });
        }

        @Test
        @DisplayName("throws exception when above bounds of term without subterms")
        default void throwsExceptionWhenAboveBoundsOfTermWithoutSubterms() {
            // Arrange
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList());

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
            List<ISimpleTerm> subterms = Arrays.asList(new DummySimpleTerm(), new DummySimpleTerm(), new DummySimpleTerm());
            ISimpleTerm sut = createSimpleTerm(subterms);

            // Assume
            assumeTrue(sut != null, "The implementation does not support multiple subterms.");

            // Act/Assert
            assertThrows(IndexOutOfBoundsException.class, () -> {
                sut.getSubterm(3);
            });
        }

    }

    /**
     * Tests the {@link ISimpleTerm#getAttachment(TermAttachmentType)} method.
     */
    @DisplayName("getAttachment(TermAttachmentType)")
    interface GetAttachmentTests extends ISimpleTermTests {

        @Test
        @DisplayName("when getting the first attachment, returns null when the term has no attachments")
        default void whenGettingTheFirstAttachment_returnsNullWhenTheTermHasNoAttachments() {
            // Arrange
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList(), Collections.emptyList());

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms.");

            // Act
            ITermAttachment result = sut.getAttachment(null);

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("when getting the first attachment, returns the first attachment")
        default void whenGettingTheFirstAttachment_returnsTheFirstAttachment() {
            // Arrange
            List<ITermAttachment> attachments = Arrays.asList(
                    new DummyTermAttachment(DummyTermAttachment.Type1),
                    new DummyTermAttachment(DummyTermAttachment.Type2),
                    new DummyTermAttachment(DummyTermAttachment.Type3)
            );
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList(), attachments);

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms or multiple attachments.");

            // Act
            ITermAttachment result = sut.getAttachment(null);

            // Assert
            assertSame(attachments.get(0), result);
        }

        @Test
        @DisplayName("when getting a specific type of attachment, returns the attachment")
        default void whenGettingASpecificTypeOfAttachment_returnsTheAttachment() {
            // Arrange
            List<ITermAttachment> attachments = Arrays.asList(
                    new DummyTermAttachment(DummyTermAttachment.Type1),
                    new DummyTermAttachment(DummyTermAttachment.Type2),
                    new DummyTermAttachment(DummyTermAttachment.Type3)
            );
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList(), attachments);

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms or multiple attachments.");

            // Act
            ITermAttachment result = sut.getAttachment(DummyTermAttachment.Type2);

            // Assert
            assertSame(attachments.get(1), result);
        }

        @Test
        @DisplayName("when getting a specific type of attachment, returns null when it is not found")
        default void whenGettingASpecificTypeOfAttachment_returnsNullWhenItIsNotFound() {
            // Arrange
            List<ITermAttachment> attachments = Arrays.asList(
                    new DummyTermAttachment(DummyTermAttachment.Type1),
                    new DummyTermAttachment(DummyTermAttachment.Type2),
                    new DummyTermAttachment(DummyTermAttachment.Type3)
            );
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList(), attachments);

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms or multiple attachments.");

            // Act
            ITermAttachment result = sut.getAttachment(DummyTermAttachment.Type4);

            // Assert
            assertNull(result);
        }
    }


    /**
     * Tests the {@link ISimpleTerm#putAttachment(ITermAttachment)} method.
     */
    @DisplayName("putAttachment(ITermAttachment)")
    interface PutAttachmentTests extends ISimpleTermTests {

        @Test
        @DisplayName("when an attachment of the type is present, replaces it")
        default void whenAnAttachmentOfTheTypeIsPresent_replacesIt() {
            // Arrange
            List<ITermAttachment> attachments = Arrays.asList(
                    new DummyTermAttachment(DummyTermAttachment.Type1),
                    new DummyTermAttachment(DummyTermAttachment.Type2),
                    new DummyTermAttachment(DummyTermAttachment.Type3)
            );
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList(), attachments);
            DummyTermAttachment replacement = new DummyTermAttachment(DummyTermAttachment.Type2);

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms or multiple attachments.");

            // Act
            sut.putAttachment(replacement);

            // Assert
            assertSame(replacement, sut.getAttachment(DummyTermAttachment.Type2));
            assertNotSame(attachments.get(2), sut.getAttachment(DummyTermAttachment.Type2));
        }

        @Test
        @DisplayName("when an attachment of the type is not present, adds it")
        default void whenAnAttachmentOfTheTypeIsNotPresent_addsIt() {
            // Arrange
            List<ITermAttachment> attachments = Arrays.asList(
                    new DummyTermAttachment(DummyTermAttachment.Type1),
                    new DummyTermAttachment(DummyTermAttachment.Type2),
                    new DummyTermAttachment(DummyTermAttachment.Type3)
            );
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList(), attachments);
            DummyTermAttachment addition = new DummyTermAttachment(DummyTermAttachment.Type4);

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms or multiple attachments.");

            // Act
            sut.putAttachment(addition);

            // Assert
            assertSame(addition, sut.getAttachment(DummyTermAttachment.Type4));
        }

    }


    /**
     * Tests the {@link ISimpleTerm#removeAttachment(TermAttachmentType)} method.
     */
    @DisplayName("removeAttachment(TermAttachmentType)")
    interface RemoveAttachmentTests extends ISimpleTermTests {

        @Test
        @DisplayName("when an attachment of the type is present, removes and returns it")
        default void whenAnAttachmentOfTheTypeIsPresent_removesAndReturnsIt() {
            // Arrange
            List<ITermAttachment> attachments = Arrays.asList(
                    new DummyTermAttachment(DummyTermAttachment.Type1),
                    new DummyTermAttachment(DummyTermAttachment.Type2),
                    new DummyTermAttachment(DummyTermAttachment.Type3)
            );
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList(), attachments);

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms or multiple attachments.");

            // Act
            ITermAttachment removed = sut.removeAttachment(DummyTermAttachment.Type2);

            // Assert
            assertSame(attachments.get(1), removed);
        }

        @Test
        @DisplayName("when an attachment of the type is not present, returns null")
        default void whenAnAttachmentOfTheTypeIsNotPresent_returnsNull() {
            // Arrange
            List<ITermAttachment> attachments = Arrays.asList(
                    new DummyTermAttachment(DummyTermAttachment.Type1),
                    new DummyTermAttachment(DummyTermAttachment.Type2),
                    new DummyTermAttachment(DummyTermAttachment.Type3)
            );
            ISimpleTerm sut = createSimpleTerm(Collections.emptyList(), attachments);

            // Assume
            assumeTrue(sut != null, "The implementation does not support zero subterms or multiple attachments.");

            // Act
            ITermAttachment removed = sut.removeAttachment(DummyTermAttachment.Type4);

            // Assert
            assertNull(removed);
        }

    }

}
