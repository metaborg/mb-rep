package org.spoofax;

import org.spoofax.interpreter.terms.IStrategoTermBuilder;
import org.spoofax.terms.TermFactory;


/**
 * Base class for term tests.
 */
public abstract class TestBase {

    public IStrategoTermBuilder getTermBuilder() {
        return new TermFactory();
    }

}
