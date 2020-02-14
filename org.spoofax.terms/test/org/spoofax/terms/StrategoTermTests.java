package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoTerm} class.
 */
@DisplayName("StrategoTerm")
public abstract class StrategoTermTests extends AbstractSimpleTermTests implements IStrategoTermTests {

    @Override
    public AbstractSimpleTerm createAbstractSimpleTerm(@Nullable List<ISimpleTerm> subterms,
                                                       @Nullable List<ITermAttachment> attachments) {
        return (AbstractSimpleTerm)createStrategoTerm(tryCastAll(subterms), null, attachments);
    }

    @Override
    public abstract IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                            @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

}
