package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTermTests;
import org.spoofax.terms.attachments.ITermAttachment;

import jakarta.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.tryCastAll;


/**
 * Tests the {@link StrategoTerm} class.
 */
@DisplayName("StrategoTerm")
public abstract class StrategoTermTests {

    public interface Fixture extends IStrategoTermTests.Fixture {

        @Override
        StrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                         @Nullable IStrategoList annotations,
                                         @Nullable List<ITermAttachment> attachments);

    }


    public static abstract class FixtureImpl extends AbstractSimpleTermTests.FixtureImpl implements Fixture {

        @Override
        public abstract StrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                         @Nullable IStrategoList annotations,
                                                         @Nullable List<ITermAttachment> attachments);

        @Override
        public AbstractSimpleTerm createAbstractSimpleTerm(@Nullable List<ISimpleTerm> subterms,
                                                           @Nullable List<ITermAttachment> attachments) {
            return createIStrategoTerm(tryCastAll(subterms), null, attachments);
        }

    }

}
