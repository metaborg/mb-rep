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
 * Tests the {@link IStrategoConstructor} interface.
 */
@DisplayName("IStrategoConstructor")
@SuppressWarnings("unused")
public interface IStrategoConstructorTests {

    interface Fixture extends IStrategoTermTests.Fixture {

        /**
         * Creates a new instance of {@link IStrategoConstructor} for testing.
         *
         * @param name        the constructor name; or {@code null} to use a sensible default
         * @param arity       the constructor arity; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        IStrategoConstructor createIStrategoConstructor(@Nullable String name, @Nullable Integer arity,
                                                        @Nullable IStrategoList annotations,
                                                        @Nullable List<ITermAttachment> attachments);

        @Override
        default IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                  @Nullable IStrategoList annotations,
                                                  @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createIStrategoConstructor(null, null, annotations, attachments);
        }

    }


    /**
     * Tests the {@link IStrategoConstructor#getName()} method.
     */
    @DisplayName("getName()")
    interface GetNameTests extends Fixture {

        @Test
        @DisplayName("returns the constructor name")
        default void returnsANonEmptyStringAsTheName() {
            // Arrange
            String name = "DummyCons";
            int arity = 2;
            IStrategoConstructor sut = createIStrategoConstructor(name, arity, null, null);

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
    interface GetArityTests extends Fixture {

        @Test
        @DisplayName("returns the constructor arity")
        default void returnsANonEmptyStringAsTheName() {
            // Arrange
            String name = "DummyCons";
            int arity = 2;
            IStrategoConstructor sut = createIStrategoConstructor(name, arity, null, null);

            // Act
            int result = sut.getArity();

            // Assert
            assertEquals(arity, result);
        }

    }


    /**
     * Tests the {@link IStrategoConstructor#getType()} method.
     */
    @DisplayName("getType()")
    interface GetTypeTests extends Fixture, IStrategoTermTests.GetTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoConstructor sut = createIStrategoConstructor("Dummy", 2, null, null);

            // Act
            TermType result = sut.getType();

            // Assert
            assertEquals(TermType.CTOR, result);
        }

    }


    /**
     * Tests the {@link IStrategoConstructor#getSubtermCount()} method.
     */
    @DisplayName("getSubtermCount()")
    interface GetSubtermCountTests extends Fixture, IStrategoTermTests.GetSubtermCountTests {

        @Test
        @DisplayName("alwaysReturnsZero")
        default void alwaysReturnsZero() {
            // Arrange
            IStrategoConstructor sut = createIStrategoConstructor("Dummy", 2, null, null);

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
    interface GetAllSubtermsTests extends Fixture, IStrategoTermTests.GetAllSubtermsTests {

        @Test
        @DisplayName("always returns empty array")
        default void alwaysReturnsEmptyArray() {
            // Arrange
            IStrategoConstructor sut = createIStrategoConstructor("Dummy", 2, null, null);

            // Act
            IStrategoTerm[] result = sut.getAllSubterms();

            // Assert
            assertEquals(0, result.length);
        }

    }

    /**
     * Tests the {@link IStrategoConstructor#getSubterms()} method.
     */
    @DisplayName("getSubterms(int)")
    interface GetSubtermsTests extends Fixture, IStrategoTermTests.GetSubtermsTests {

        @Test
        @DisplayName("always returns empty list")
        default void alwaysReturnsEmptyList() {
            // Arrange
            IStrategoConstructor sut = createIStrategoConstructor("Dummy", 2, null, null);

            // Act
            List<IStrategoTerm> result = sut.getSubterms();

            // Assert
            assertEquals(0, result.size());
        }

    }


    /**
     * Tests the {@link IStrategoConstructor#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends Fixture, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both have the same name and arity, returns true")
        default void whenBothHaveTheSameNameAndArity_returnsTrue() {
            // Arrange
            IStrategoConstructor sut = createIStrategoConstructor("Dummy", 2, null, null);
            IStrategoConstructor other = createIStrategoConstructor("Dummy", 2, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different name, returns false")
        default void whenOtherHasDifferentName_returnsFalse() {
            // Arrange
            IStrategoConstructor sut = createIStrategoConstructor("Dummy", 2, null, null);
            IStrategoConstructor other = createIStrategoConstructor("Nutty", 2, null, null);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when other has different arity, returns false")
        default void whenOtherHasDifferentArity_returnsFalse() {
            // Arrange
            IStrategoConstructor sut = createIStrategoConstructor("Dummy", 2, null, null);
            IStrategoConstructor other = createIStrategoConstructor("Dummy", 1, null, null);

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
    interface ToStringTests extends Fixture, IStrategoTermTests.ToStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() {
            // Arrange
            IStrategoConstructor sut = createIStrategoConstructor("Dummy", 2, null, null);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("Dummy`2", result);
        }

    }


    /**
     * Tests the {@link IStrategoConstructor#writeAsString(Appendable, int)} and {@link
     * IStrategoConstructor#writeAsString(Appendable)} methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends Fixture, IStrategoTermTests.WriteAsStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() throws IOException {
            // Arrange
            StringBuilder sb = new StringBuilder();
            IStrategoConstructor sut = createIStrategoConstructor("Dummy", 2, null, null);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("Dummy`2", sb.toString());
        }

    }

}
