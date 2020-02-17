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
public class StrategoTermTests {

    public interface Fixture extends IStrategoTermTests.Fixture {

        /**
         * Creates a new instance of {@link StrategoTerm} for testing.
         *
         * @param subterms    the subterms of the term; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                        @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

    }


    public static abstract class FixtureImpl extends AbstractSimpleTermTests.FixtureImpl implements Fixture {

        @Override
        public abstract StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                        @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

        @Override
        public AbstractSimpleTerm createAbstractSimpleTerm(@Nullable List<ISimpleTerm> subterms,
                                                           @Nullable List<ITermAttachment> attachments) {
            return (AbstractSimpleTerm)createIStrategoTerm(tryCastAll(subterms), null, attachments);
        }

        @Override
        public IStrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                 @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoTerm(subterms, annotations, attachments);
        }

    }

}
