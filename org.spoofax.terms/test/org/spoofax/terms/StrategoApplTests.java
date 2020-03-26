package org.spoofax.terms;

import java.util.List;

import javax.annotation.Nullable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoConstructor;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.ISimpleTermTests;
import org.spoofax.interpreter.terms.IStrategoApplTests;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTermTests;
import org.spoofax.terms.attachments.ITermAttachment;


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
            //noinspection EqualsAndHashcode
            return TestUtils.putAttachments(new StrategoAppl(constructor,
                subterms != null ? subterms.toArray(AbstractTermFactory.EMPTY_TERM_ARRAY) :
                    AbstractTermFactory.EMPTY_TERM_ARRAY, annotations != null ? annotations : TermFactory.EMPTY_LIST) {
                @Override
                public int hashCode() {
                    return 0;
                }
            }, attachments);
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
    @Nested class GetAllSubtermsTests    extends FixtureImpl implements IStrategoTermTests.GetAllSubtermsTests {}
    @Nested class GetSubtermsTests       extends FixtureImpl implements IStrategoTermTests.GetSubtermsTests {}
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoTermTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on

}
