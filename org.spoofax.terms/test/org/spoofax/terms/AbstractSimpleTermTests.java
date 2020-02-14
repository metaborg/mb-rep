package org.spoofax.terms;


import org.junit.jupiter.api.DisplayName;
import org.spoofax.TestBase;
import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.ISimpleTermTests;
import org.spoofax.terms.AbstractSimpleTerm;
import org.spoofax.terms.StrategoIntTests;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * Tests the {@link AbstractSimpleTerm} abstract class.
 */
@DisplayName("AbstractSimpleTerm")
public abstract class AbstractSimpleTermTests extends TestBase implements ISimpleTermTests {

    /**
     * Creates a new instance of the {@link AbstractSimpleTerm} for testing.
     *
     * @param subterms the subterms of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    public abstract AbstractSimpleTerm createAbstractSimpleTerm(@Nullable List<ISimpleTerm> subterms, @Nullable List<ITermAttachment> attachments);

    @Nullable
    @Override
    public ISimpleTerm createSimpleTerm(@Nullable List<ISimpleTerm> subterms, @Nullable List<ITermAttachment> attachments) {
        return createAbstractSimpleTerm(subterms, attachments);
    }

}
