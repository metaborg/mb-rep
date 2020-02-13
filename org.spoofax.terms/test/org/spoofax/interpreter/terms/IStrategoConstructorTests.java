package org.spoofax.interpreter.terms;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


/**
 * Tests the {@link IStrategoConstructor} interface.
 */
@DisplayName("IStrategoConstructor")
public interface IStrategoConstructorTests extends IStrategoTermTests {

    /**
     * Creates a new instance of the {@link IStrategoConstructor} for testing.
     *
     * @param name the constructor name
     * @param arity the constructor arity
     * @param annotations the annotations of the term
     * @param attachments the attachments of the term
     * @return the created object
     */
    IStrategoConstructor createStrategoConstructor(String name, int arity, IStrategoList annotations, List<ITermAttachment> attachments);

    /**
     * Creates a new instance of the {@link IStrategoTerm} for testing.
     *
     * @param name the constructor name
     * @param arity the constructor arity
     * @param annotations the annotations of the term
     * @return the created object
     */
    default IStrategoConstructor createStrategoConstructor(String name, int arity, IStrategoList annotations) {
        return createStrategoConstructor(name, arity, annotations, Collections.emptyList());
    }

    /**
     * Creates a new instance of the {@link IStrategoTerm} for testing.
     *
     * @param name the constructor name
     * @param arity the constructor arity
     * @return the created object
     */
    default IStrategoConstructor createStrategoConstructor(String name, int arity) {
        return createStrategoConstructor(name, arity, TermFactory.EMPTY_LIST);
    }

    @Nullable
    @Override
    default IStrategoTerm createStrategoTerm(List<IStrategoTerm> subterms, IStrategoList annotations,
                                             List<ITermAttachment> attachments) {
        if (subterms != null && subterms.size() != 0) return null;
        return createStrategoConstructor("Dummy", 0, annotations, attachments);
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
            IStrategoConstructor sut = createStrategoConstructor(name, arity);

            // Assume
            assumeTrue(sut != null, "The implementation does not support the given name and/or arity.");

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
            IStrategoConstructor sut = createStrategoConstructor(name, arity);

            // Assume
            assumeTrue(sut != null, "The implementation does not support the given name and/or arity.");

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
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2);

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
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2);;

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
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2);

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
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2);
            IStrategoConstructor other = createStrategoConstructor("Dummy", 2);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different name, returns false")
        default void whenOtherHasDifferentName_returnsFalse() {
            // Arrange
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2);
            IStrategoConstructor other = createStrategoConstructor("Nutty", 2);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when other has different arity, returns false")
        default void whenOtherHasDifferentArity_returnsFalse() {
            // Arrange
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2);
            IStrategoConstructor other = createStrategoConstructor("Dummy", 1);

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
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2);

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
            IStrategoConstructor sut = createStrategoConstructor("Dummy", 2);
            StringBuilder sb = new StringBuilder();

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("Dummy`2", sb.toString());
        }

    }
}
