package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoList} class.
 */
@DisplayName("StrategoList")
public class StrategoListTests {


    public interface Fixture extends IStrategoListTests.Fixture {

        @Override
        StrategoList createIStrategoList(@Nullable List<IStrategoTerm> elements,
                                         @Nullable IStrategoList annotations,
                                         @Nullable List<ITermAttachment> attachments);

        @Override
        StrategoList createConsNilIStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments);

        @Override
        StrategoList createEmptyIStrategoList(@Nullable IStrategoList annotations,
                                              @Nullable List<ITermAttachment> attachments);

    }


    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoList createIStrategoList(@Nullable List<IStrategoTerm> elements,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
            if (elements == null || elements.isEmpty()) {
                return createEmptyIStrategoList(annotations, attachments);
            }
            return createConsNilIStrategoList(elements.get(0), createIStrategoList(elements.subList(1,
                    elements.size()), null, null), annotations, attachments);
        }

        @Override
        public StrategoList createConsNilIStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail,
                                                       @Nullable IStrategoList annotations,
                                                       @Nullable List<ITermAttachment> attachments) {
            return TestUtils.putAttachments(new StrategoList(
                    head != null ? head : new DummyStrategoTerm(),
                    tail != null ? tail : new StrategoList(null),
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public StrategoList createEmptyIStrategoList(@Nullable IStrategoList annotations,
                                                     @Nullable List<ITermAttachment> attachments) {
            return TestUtils.putAttachments(new StrategoList(
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public StrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
            if (subterms == null || subterms.isEmpty()) {
                return createEmptyIStrategoList(annotations, attachments);
            }
            return createConsNilIStrategoList(subterms.get(0), createIStrategoList(subterms.subList(1,
                    subterms.size()), null, null), annotations, attachments);
        }

    }


    // @formatter:off
    // IStrategoList
    @Nested class GetTermTypeTests       extends FixtureImpl implements IStrategoListTests.GetTermTypeTests {}
    @Nested class HeadTests              extends FixtureImpl implements IStrategoListTests.HeadTests {}
    @Nested class IsEmptyTests           extends FixtureImpl implements IStrategoListTests.IsEmptyTests {}
    @Nested class MatchTests             extends FixtureImpl implements IStrategoListTests.MatchTests {}
    @Nested class SizeTests              extends FixtureImpl implements IStrategoListTests.SizeTests {}
    @Nested class TailTests              extends FixtureImpl implements IStrategoListTests.TailTests {}
    @Nested class HashCodeTests          extends FixtureImpl implements IStrategoListTests.HashCodeTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoListTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoListTests.WriteAsStringTests {}

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
