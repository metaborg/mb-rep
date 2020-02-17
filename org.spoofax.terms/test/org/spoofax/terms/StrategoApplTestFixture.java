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
@DisplayName("StrategoAppl")
public class StrategoApplTestFixture {

    public static class Fixture extends StrategoTermTestFixture.Fixture {

        public StrategoAppl createStrategoAppl(@Nullable IStrategoConstructor constructor, @Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            if (constructor == null) {
                switch (subterms != null ? subterms.size() : 0) {
                    case 0:
                        constructor = DummyStrategoConstructor.Dummy0;
                        break;
                    case 1:
                        constructor = DummyStrategoConstructor.Dummy1;
                        break;
                    case 2:
                        constructor = DummyStrategoConstructor.Dummy2;
                        break;
                    case 3:
                        constructor = DummyStrategoConstructor.Dummy3;
                        break;
                    case 4:
                        constructor = DummyStrategoConstructor.Dummy4;
                        break;
                    default:
                        constructor = new DummyStrategoConstructor("Dummy", subterms.size());
                        break;
                }
            }
            return TermUtil.putAttachments(new StrategoAppl(
                    constructor,
                    subterms != null ? subterms.toArray(new IStrategoTerm[0]) : new IStrategoTerm[0],
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        public IStrategoAppl createIStrategoAppl(@Nullable IStrategoConstructor constructor, @Nullable List<IStrategoTerm> subterms,
                                                 @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoAppl(constructor, subterms, annotations, attachments);
        }


        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            return createStrategoAppl(null, subterms, annotations, attachments);
        }

    }

    // @formatter:off
    @Nested class GetAllSubtermTests     extends Fixture implements StrategoApplTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends Fixture implements StrategoApplTests.GetAnnotationsTests {}
    @Nested class GetAttachmentTests     extends Fixture implements StrategoApplTests.GetAttachmentTests {}
    @Nested class GetConstructorTests    extends Fixture implements StrategoApplTests.GetConstructorTests {}
    @Nested class GetNameTests           extends Fixture implements StrategoApplTests.GetNameTests {}
    @Nested class GetSubtermCountTests   extends Fixture implements StrategoApplTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends Fixture implements StrategoApplTests.GetSubtermTests {}
    @Nested class GetTermTypeTests       extends Fixture implements StrategoApplTests.GetTermTypeTests {}
    @Nested class MatchTests             extends Fixture implements StrategoApplTests.MatchTests {}
    @Nested class PutAttachmentTests     extends Fixture implements StrategoApplTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends Fixture implements StrategoApplTests.RemoveAttachmentTests {}
    @Nested class ToStringTests          extends Fixture implements StrategoApplTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends Fixture implements StrategoApplTests.WriteAsStringTests {}
    // @formatter:on


}
