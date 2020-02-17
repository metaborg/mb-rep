package org.spoofax.terms;


import org.junit.jupiter.api.DisplayName;
import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.ISimpleTermTests;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link AbstractSimpleTerm} abstract class.
 */
public interface AbstractSimpleTermTests extends ISimpleTermTests {

    /**
     * Creates a new instance of {@link AbstractSimpleTerm} for testing.
     *
     * @param subterms    the subterms of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    AbstractSimpleTerm createAbstractSimpleTerm(@Nullable List<ISimpleTerm> subterms, @Nullable List<ITermAttachment> attachments);


}
