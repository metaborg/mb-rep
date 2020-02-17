package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.opentest4j.TestAbortedException;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.IStrategoTupleTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * Tests the {@link StrategoTuple} class.
 */
public interface StrategoTupleTests extends IStrategoTupleTests {

    /**
     * Creates a new instance of {@link StrategoTuple} for testing.
     *
     * @param elements the elements of the tuple; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    StrategoTuple createStrategoTuple(@Nullable List<IStrategoTerm> elements,
                                        @Nullable IStrategoList annotations,
                                        @Nullable List<ITermAttachment> attachments);

}
