package org.spoofax.interpreter.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


/**
 * Tests the {@link IStrategoNamed} interface.
 */
@SuppressWarnings("unused")
public interface IStrategoNamedTests extends IStrategoTermTests {

    /**
     * Creates a new instance of {@link IStrategoNamed} for testing.
     *
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    IStrategoNamed createIStrategoNamed();

    /**
     * Tests the {@link IStrategoNamed#getName()} method.
     */
    @DisplayName("getName()")
    interface GetNameTests extends IStrategoNamedTests {

        @Test
        @DisplayName("returns a non-empty string as the name")
        default void returnsANonEmptyStringAsTheName() {
            // Arrange
            IStrategoNamed sut = createIStrategoNamed();

            // Act
            String result = sut.getName();

            // Assert
            assertFalse(result.isEmpty());
        }

    }
}
