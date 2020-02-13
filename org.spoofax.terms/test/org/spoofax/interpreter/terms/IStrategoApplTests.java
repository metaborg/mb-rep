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
import static org.spoofax.TestBase.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link IStrategoAppl} interface.
 */
@DisplayName("IStrategoAppl")
public interface IStrategoApplTests extends IStrategoNamedTests {

    /**
     * Creates a new instance of the {@link IStrategoAppl} for testing.
     *
     * @param constructor the constructor of the term; or {@code null} to use a sensible default
     * @param subterms the subterms of the term; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object; or {@code null} when an instance with the given parameters could not be created
     */
    @Nullable
    IStrategoAppl createStrategoAppl(@Nullable IStrategoConstructor constructor, @Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

    /**
     * Creates a new instance of the {@link IStrategoAppl} for testing.
     *
     * @param constructor the constructor of the term; or {@code null} to use a sensible default
     * @param subterms the subterms of the term; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @return the created object; or {@code null} when an instance with the given parameters could not be created
     */
    @Nullable
    default IStrategoAppl createStrategoAppl(@Nullable IStrategoConstructor constructor, @Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations) {
        return createStrategoAppl(constructor, subterms, annotations, Collections.emptyList());
    }

    /**
     * Creates a new instance of the {@link IStrategoAppl} for testing.
     *
     * @param constructor the constructor of the term; or {@code null} to use a sensible default
     * @param subterms the subterms of the term; or {@code null} to use a sensible default
     * @return the created object; or {@code null} when an instance with the given parameters could not be created
     */
    @Nullable
    default IStrategoAppl createStrategoAppl(@Nullable IStrategoConstructor constructor, @Nullable List<IStrategoTerm> subterms) {
        return createStrategoAppl(constructor, subterms, TermFactory.EMPTY_LIST);
    }

    /**
     * Creates a new instance of the {@link IStrategoAppl} for testing.
     *
     * @return the created object; or {@code null} when an instance with the given parameters could not be created
     */
    @Nullable
    default IStrategoAppl createStrategoAppl() {
        return createStrategoAppl(DummyStrategoConstructor.Dummy0, Collections.emptyList(), TermFactory.EMPTY_LIST);
    }

    @Nullable
    @Override
    default IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations,
                                             @Nullable List<ITermAttachment> attachments) {
        IStrategoConstructor constructor;
        switch (subterms.size()) {
            case 0: constructor = DummyStrategoConstructor.Dummy0; break;
            case 1: constructor = DummyStrategoConstructor.Dummy1; break;
            case 2: constructor = DummyStrategoConstructor.Dummy2; break;
            case 3: constructor = DummyStrategoConstructor.Dummy3; break;
            case 4: constructor = DummyStrategoConstructor.Dummy4; break;
            default: constructor = new DummyStrategoConstructor("Dummy", subterms.size()); break;
        }
        return createStrategoAppl(constructor, subterms, annotations, attachments);
    }

    @Override
    default IStrategoNamed createStrategoNamed() {
        return createStrategoAppl(DummyStrategoConstructor.Dummy0, Collections.emptyList());
    }

    /**
     * Tests the {@link IStrategoAppl#getConstructor()} method.
     */
    @DisplayName("getConstructor()")
    interface GetConstructorTests extends IStrategoApplTests {

        @Test
        @DisplayName("returns the constructor of the term")
        default void returnsTheConstructorOfTheTerm() {
            // Arrange
            IStrategoConstructor constructor = DummyStrategoConstructor.Dummy2;
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoAppl sut = createStrategoAppl(constructor, subterms);

            // Assume
            assumeTrue(sut != null, TEST_INSTANCE_NOT_CREATED);

            // Act
            IStrategoConstructor result = sut.getConstructor();

            // Assert
            assertSame(constructor, result);
        }

    }


    /**
     * Tests the {@link IStrategoAppl#getName()} method.
     */
    @DisplayName("getName()")
    interface GetNameTests extends IStrategoApplTests, IStrategoNamedTests.GetNameTests {

        @Test
        @DisplayName("returns the constructor name as the name")
        default void returnsANonEmptyStringAsTheName() {
            // Arrange
            IStrategoConstructor constructor = DummyStrategoConstructor.Dummy2;
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoAppl sut = createStrategoAppl(constructor, subterms);

            // Assume
            assumeTrue(sut != null, TEST_INSTANCE_NOT_CREATED);

            // Act
            String result = sut.getName();

            // Assert
            assertEquals(constructor.getName(), result);
        }

    }

    /**
     * Tests the {@link IStrategoAppl#getTermType()} method.
     */
    @DisplayName("getTermType()")
    interface GetTermTypeTests extends IStrategoApplTests, IStrategoTermTests.GetTermTypeTests {

        @Test
        @DisplayName("returns the correct term type")
        default void returnsTheCorrectTermType() {
            // Arrange
            IStrategoAppl sut = createStrategoAppl();

            // Act
            int result = sut.getTermType();

            // Assert
            assertEquals(IStrategoTerm.APPL, result);
        }

    }

    /**
     * Tests the {@link IStrategoAppl#match(IStrategoTerm)} method.
     */
    @DisplayName("match(IStrategoTerm)")
    interface MatchTests extends IStrategoApplTests, IStrategoTermTests.MatchTests {

        @Test
        @DisplayName("when both have the same constructor and subterms, returns true")
        default void whenBothHaveTheSameConstructorAndSubterms_returnsTrue() {
            // Arrange
            IStrategoConstructor constructor = DummyStrategoConstructor.Dummy2;
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoAppl sut = createStrategoAppl(constructor, subterms);
            IStrategoAppl other = createStrategoAppl(constructor, subterms);

            // Assume
            assumeTrue(sut != null, TEST_INSTANCE_NOT_CREATED);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("when other has different constructor, returns false")
        default void whenOtherHasDifferentConstructor_returnsFalse() {
            // Arrange
            List<IStrategoTerm> subterms = Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm());
            IStrategoAppl sut = createStrategoAppl(DummyStrategoConstructor.Dummy2, subterms);
            IStrategoAppl other = createStrategoAppl(new DummyStrategoConstructor("Alt", 2), subterms);

            // Assume
            assumeTrue(sut != null && other != null, TEST_INSTANCE_NOT_CREATED);

            // Act
            boolean result = sut.match(other);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("when other has different subterms, returns false")
        default void whenOtherHasDifferentSubterms_returnsFalse() {
            // Arrange
            IStrategoAppl sut = createStrategoAppl(DummyStrategoConstructor.Dummy2, Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm()));
            IStrategoAppl other = createStrategoAppl(DummyStrategoConstructor.Dummy2, Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm()));

            // Assume
            assumeTrue(sut != null && other != null, TEST_INSTANCE_NOT_CREATED);

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
    interface ToStringTests extends IStrategoApplTests, IStrategoTermTests.ToStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() {
            // Arrange
            IStrategoAppl sut = createStrategoAppl(DummyStrategoConstructor.Dummy2, Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm()));

            // Assume
            assumeTrue(sut != null, TEST_INSTANCE_NOT_CREATED);

            // Act
            String result = sut.toString();

            // Assert
            assertEquals("Dummy(<dummy>,<dummy>)", result);
        }
    }


    /**
     * Tests the {@link IStrategoInt#writeAsString(Appendable, int)} and {@link IStrategoInt#writeAsString(Appendable)} methods.
     */
    @DisplayName("writeAsString(..)")
    interface WriteAsStringTests extends IStrategoApplTests, IStrategoTermTests.WriteAsStringTests {

        @Test
        @DisplayName("returns the correct string representation")
        default void returnsTheCorrectStringRepresentation() throws IOException {
            // Arrange
            StringBuilder sb = new StringBuilder();
            IStrategoAppl sut = createStrategoAppl(DummyStrategoConstructor.Dummy2, Arrays.asList(new DummyStrategoTerm(), new DummyStrategoTerm()));

            // Assume
            assumeTrue(sut != null, TEST_INSTANCE_NOT_CREATED);

            // Act
            sut.writeAsString(sb);

            // Assert
            assertEquals("Dummy(<dummy>,<dummy>)", sb.toString());
        }

    }
}
