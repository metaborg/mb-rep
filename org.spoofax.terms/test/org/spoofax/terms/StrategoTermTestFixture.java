package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTermTests;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.tryCastAll;


/**
 * Tests the {@link StrategoTerm} class.
 */
@DisplayName("StrategoTerm")
public abstract class StrategoTermTestFixture {

    public static abstract class Fixture extends AbstractSimpleTermTestFixture.Fixture {

        @Override
        public AbstractSimpleTerm createAbstractSimpleTerm(@Nullable List<ISimpleTerm> subterms,
                                                           @Nullable List<ITermAttachment> attachments) {
            return (AbstractSimpleTerm)createIStrategoTerm(tryCastAll(subterms), null, attachments);
        }

        public IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                 @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoTerm(subterms, annotations, attachments);
        }

        public abstract StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                        @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

    }

}
