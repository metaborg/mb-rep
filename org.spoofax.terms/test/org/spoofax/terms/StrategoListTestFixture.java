package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoListTests;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoList} class.
 */
@DisplayName("StrategoList")
public class StrategoListTestFixture {

    public static class Fixture extends StrategoTermTestFixture.Fixture {

        public StrategoList createStrategoList(@Nullable List<IStrategoTerm> elements,
                                               @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            if (elements == null || elements.isEmpty()) {
                return createEmptyStrategoList(annotations, attachments);
            }
            return createConsNilStrategoList(elements.get(0), createStrategoList(elements.subList(1, elements.size()), null, null), annotations, attachments);
        }

        public StrategoList createConsNilStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail, @Nullable IStrategoList annotations,
                                                      @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoList(
                    head != null ? head : new DummyStrategoTerm(),
                    tail != null ? tail : new StrategoList(null),
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        public StrategoList createEmptyStrategoList(@Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoList(
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        public IStrategoList createIStrategoList(@Nullable List<IStrategoTerm> elements,
                                                 @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoList(elements, annotations, attachments);
        }

        public IStrategoList createConsNilIStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail, @Nullable IStrategoList annotations,
                                                        @Nullable List<ITermAttachment> attachments) {
            return createConsNilStrategoList(head, tail, annotations, attachments);
        }

        public IStrategoList createEmptyIStrategoList(@Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createEmptyStrategoList(annotations, attachments);
        }

        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            if (subterms == null || subterms.isEmpty()) {
                return createEmptyStrategoList(annotations, attachments);
            }
            return createConsNilStrategoList(subterms.get(0), createIStrategoList(subterms.subList(1, subterms.size()), null, null), annotations, attachments);
        }

    }

    // @formatter:off
    @Nested class GetAllSubtermTests     extends Fixture implements StrategoListTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends Fixture implements StrategoListTests.GetAnnotationsTests {}
    @Nested class GetAttachmentTests     extends Fixture implements StrategoListTests.GetAttachmentTests {}
    @Nested class GetSubtermCountTests   extends Fixture implements IStrategoListTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends Fixture implements IStrategoListTests.GetSubtermTests {}
    @Nested class GetTermTypeTests       extends Fixture implements StrategoListTests.GetTermTypeTests {}
    @Nested class HeadTests              extends Fixture implements IStrategoListTests.HeadTests {}
    @Nested class IsEmptyTests           extends Fixture implements IStrategoListTests.IsEmptyTests {}
    @Nested class MatchTests             extends Fixture implements StrategoListTests.MatchTests {}
    @Nested class PutAttachmentTests     extends Fixture implements StrategoListTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends Fixture implements StrategoListTests.RemoveAttachmentTests {}
    @Nested class SizeTests              extends Fixture implements IStrategoListTests.SizeTests {}
    @Nested class TailTests              extends Fixture implements IStrategoListTests.TailTests {}
    @Nested class ToStringTests          extends Fixture implements StrategoListTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends Fixture implements StrategoListTests.WriteAsStringTests {}
    // @formatter:on


}
