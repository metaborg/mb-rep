package org.spoofax.interpreter.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.spoofax.TestBase.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link IStrategoConstructor} interface.
 */
@SuppressWarnings("unused")
@DisplayName("IStrategoConstructor")
public interface IStrategoConstructorTests extends IStrategoTermTests {

    /**
     * Creates a new instance of the {@link IStrategoConstructor} for testing.
     *
     * @param name the constructor name; or {@code null} to use a sensible default
     * @param arity the constructor arity; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    IStrategoConstructor createStrategoConstructor(@Nullable String name, @Nullable Integer arity, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

    @Override
    default IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations,
                                             @Nullable List<ITermAttachment> attachments) {
        if (subterms != null && subterms.size() != 0)  throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
        return createStrategoConstructor(null, null, annotations, attachments);
    }

    /**
     * Tests the {@link IStrategoConstructor#getName()} method.
     */
    @DisplayName("getName()")
    interface GetNameTests extends IStrategoConstructorTests {

        @Test
        @DisplayName("returns the constructor name")
        default void returnsANonEmptyStringAsTheName() {
            // Arrange
            String name = "DummyCons";
            int arity = 2;
            IStrategoConstructor sut = createStrategoConstructor(name, arity, null, null);

            // Act
            String result = sut.getName();

            // Assert
            assertEquals(name, result);
        }

    }

    /**
     * Tests the {@link IStrategoConstructor#getArity()} method.
     */
    @DisplayName("getArity()")
    interface GetArityTests extends IStrategoConstructorTests {

        @Test
        @DisplayName("returns the constructor arity")
        default void returnsANonEmptyStringAsTheName() {
            // Arrange
            String name = "DummyCons";
            int arity = 2;
            IStrategoConstructor sut = createStrategoConstructor(name, arity, null, null);

            // Act
            int result = sut.getArity();

            // Assert
            assertEquals(arity, result);
        }

    }

    /**
     * Tests the {@link IStrategoConstructor#getTermType()} method.
     */
    @DisplayName("getTermType()")
    interface GetTermTypeTests extends IStrategoConstructorTests, IStrategoTermTests.GetTermTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2, null, null);

            // Act
            int result = sut.getTermType();

            // Assert
            assertEquals(IStrategoTerm.CTOR, result);
        }

    }


    /**
     * Tests the {@link IStrategoConstructor#getSubtermCount()} method.
     */
    @DisplayName("getSubtermCount()")
    interface GetSubtermCountTests extends IStrategoConstructorTests, IStrategoTermTests.GetSubtermCountTests {

        @Test
        @DisplayName("alwaysReturnsZero")
        default void alwaysReturnsZero() {
            // Arrange
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2, null, null);

            // Act
            int result = sut.getSubtermCount();

            // Assert
            assertEquals(0, result);
        }

    }

    /**
     * Tests the {@link IStrategoConstructor#getAllSubterms()} method.
     */
    @DisplayName("getAllSubterms(int)")
    interface GetAllSubtermTests extends IStrategoConstructorTests, IStrategoTermTests.GetAllSubtermTests {

        @Test
        @DisplayName("always returns empty array")
        default void alwaysReturnsEmptyArray() {
            // Arrange
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2, null, null);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(0, result.length);
        }

    }


    /**
     * Tests the {@link IStrategoConstructor#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends IStrategoConstructorTests, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both have the same name and arity, returns true")
        default void whenBothHaveTheSameNameAndArity_returnsTrue() {
            // Arrange
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2, null, null);
            IStrategoConstructor other = createStrategoConstructor("Dummy", 2, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different name, returns false")
        default void whenOtherHasDifferentName_returnsFalse() {
            // Arrange
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2, null, null);
            IStrategoConstructor other = createStrategoConstructor("Nutty", 2, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when other has different arity, returns false")
        default void whenOtherHasDifferentArity_returnsFalse() {
            // Arrange
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2, null, null);
            IStrategoConstructor other = createStrategoConstructor("Dummy", 1, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

    }


    /**
     * Tests the {@link IStrategoConstructor#toString(int)} and {@link IStrategoConstructor#toString()} methods.
     */
    @DisplayName("toString(..)")
    interface ToStringTests extends IStrategoConstructorTests, IStrategoTermTests.ToStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() {
            // Arrange
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2, null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("Dummy`2", result);
        }
    }


    /**
     * Tests the {@link IStrategoConstructor#writeAsString(Appendable, int)} and {@link IStrategoConstructor#writeAsString(Appendable)} methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends IStrategoConstructorTests, IStrategoTermTests.WriteAsStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() throws IOException {
            // Arrange
            StringBuilder sb = new StringBuilder();
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2, null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("Dummy`2", sb.toString());
        }

    }
}
