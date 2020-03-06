package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoConstructor;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoAppl} class.
 */
@DisplayName("StrategoAppl")
public class StrategoApplTests {

    public interface Fixture extends IStrategoApplTests.Fixture {

        @Override
        StrategoAppl createIStrategoAppl(@Nullable IStrategoConstructor constructor,
                                         @Nullable List<IStrategoTerm> subterms,
                                         @Nullable IStrategoList annotations,
                                         @Nullable List<ITermAttachment> attachments);

    }


    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoAppl createIStrategoAppl(@Nullable IStrategoConstructor constructor,
                                                @Nullable List<IStrategoTerm> subterms,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
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
            return TestUtils.putAttachments(new StrategoAppl(
                    constructor,
                    subterms != null ? subterms.toArray(new IStrategoTerm[0]) : new IStrategoTerm[0],
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public StrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
            return createIStrategoAppl(null, subterms, annotations, attachments);
        }

    }


    // @formatter:off
    // IStrategoAppl
    @Nested class GetConstructorTests    extends FixtureImpl implements IStrategoApplTests.GetConstructorTests {}
    @Nested class GetNameTests           extends FixtureImpl implements IStrategoApplTests.GetNameTests {}
    @Nested class GetTermTypeTests       extends FixtureImpl implements IStrategoApplTests.GetTermTypeTests {}
    @Nested class MatchTests             extends FixtureImpl implements IStrategoApplTests.MatchTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoApplTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoApplTests.WriteAsStringTests {}

    // IStrategoTerm
    @Nested class GetAllSubtermTests     extends FixtureImpl implements IStrategoTermTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoTermTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on

}
