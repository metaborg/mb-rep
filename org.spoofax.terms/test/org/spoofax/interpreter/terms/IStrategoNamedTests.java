package org.spoofax.interpreter.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the {@link IStrategoNamed} interface.
 */
@DisplayName("IStrategoNamed")
public interface IStrategoNamedTests extends IStrategoTermTests {

    /**
     * Creates a new instance of the {@link IStrategoNamed} for testing.
     *
     * @return the created object
     */
    IStrategoNamed createStrategoNamed();

    /**
     * Tests the {@link IStrategoNamed#getName()} method.
     */
    @DisplayName("getName()")
    interface GetNameTests extends IStrategoNamedTests {

        @Test
        @DisplayName("returns a non-empty string as the name")
        default void returnsANonEmptyStringAsTheName() {
            // Arrange
            IStrategoNamed sut = createStrategoNamed();

            // Act
            String result = sut.getName();

            // Assert
            assertFalse(result.isEmpty());
        }

    }
}
