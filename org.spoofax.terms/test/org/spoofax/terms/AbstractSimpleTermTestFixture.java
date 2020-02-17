package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

@DisplayName("AbstractSimpleTerm")
public abstract class AbstractSimpleTermTestFixture {

    public static abstract class Fixture {

        public abstract AbstractSimpleTerm createAbstractSimpleTerm(@Nullable List<ISimpleTerm> subterms, @Nullable List<ITermAttachment> attachments);

        @Nullable
        public ISimpleTerm createISimpleTerm(@Nullable List<ISimpleTerm> subterms, @Nullable List<ITermAttachment> attachments) {
            return createAbstractSimpleTerm(subterms, attachments);
        }

    }
}
