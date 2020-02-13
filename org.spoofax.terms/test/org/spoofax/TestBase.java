package org.spoofax;

import org.spoofax.interpreter.terms.IStrategoTermBuilder;
import org.spoofax.terms.TermFactory;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


/**
 * Base class for term tests.
 */
public abstract class TestBase {

    public static final String TEST_INSTANCE_NOT_CREATED = "A test instance with the given parameters could not be created.";

    public IStrategoTermBuilder getTermBuilder() {
        return new TermFactory();
    }

}
