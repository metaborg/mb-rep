package org.spoofax.interpreter.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;


/**
 * Tests the {@link IStrategoNamed} interface.
 */
@DisplayName("IStrategoNamed")
@SuppressWarnings("unused")
public interface IStrategoNamedTests {

    interface Fixture extends IStrategoTermTests.Fixture {

        /**
         * Creates a new instance of {@link IStrategoNamed} for testing.
         *
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        IStrategoNamed createIStrategoNamed();

    }


    /**
     * Tests the {@link IStrategoNamed#getName()} method.
     */
    @DisplayName("getName()")
    interface GetNameTests extends Fixture {

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
