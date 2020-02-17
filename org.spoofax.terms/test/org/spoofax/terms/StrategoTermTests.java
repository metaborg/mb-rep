package org.spoofax.terms;

import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.spoofax.TestUtils.tryCastAll;


/**
 * Tests the {@link StrategoTerm} class.
 */
public interface StrategoTermTests extends AbstractSimpleTermTests, IStrategoTermTests {

    /**
     * Creates a new instance of {@link StrategoTerm} for testing.
     *
     * @param subterms the subterms of the term; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);


}
