package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoConstructor;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoAppl} class.
 */
public interface StrategoApplTests extends IStrategoApplTests {

    /**
     * Creates a new instance of {@link StrategoAppl} for testing.
     *
     * @param constructor the constructor of the term; or {@code null} to use a sensible default
     * @param subterms the subterms of the term; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    StrategoAppl createStrategoAppl(@Nullable IStrategoConstructor constructor, @Nullable List<IStrategoTerm> subterms,
                                      @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

}
