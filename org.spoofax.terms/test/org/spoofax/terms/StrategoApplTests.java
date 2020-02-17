package org.spoofax.terms;

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
public class StrategoApplTests {

    public interface Fixture extends IStrategoApplTests.Fixture {

        /**
         * Creates a new instance of {@link StrategoAppl} for testing.
         *
         * @param constructor the constructor of the term; or {@code null} to use a sensible default
         * @param subterms the subterms of the term; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        StrategoAppl createStrategoAppl(@Nullable IStrategoConstructor constructor, @Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);
    }

    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
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

        @Override
        public IStrategoAppl createIStrategoAppl(@Nullable IStrategoConstructor constructor, @Nullable List<IStrategoTerm> subterms,
                                                 @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoAppl(constructor, subterms, annotations, attachments);
        }

        @Override
        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            return createStrategoAppl(null, subterms, annotations, attachments);
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
