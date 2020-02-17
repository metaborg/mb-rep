package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.IStrategoTupleTests;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoTuple} class.
 */
@DisplayName("StrategoTuple")
public class StrategoTupleTestFixture {

    public static class Fixture extends StrategoTermTestFixture.Fixture {

        public StrategoTuple createStrategoTuple(@Nullable List<IStrategoTerm> elements,
                                                 @Nullable IStrategoList annotations,
                                                 @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoTuple(
                    elements != null ? elements.toArray(new IStrategoTerm[0]) : new IStrategoTerm[]{new DummyStrategoTerm()},
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        public IStrategoTuple createIStrategoTuple(@Nullable List<IStrategoTerm> elements,
                                                   @Nullable IStrategoList annotations,
                                                   @Nullable List<ITermAttachment> attachments) {
            return createStrategoTuple(elements, annotations, attachments);
        }

        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoTuple(subterms, annotations, attachments);
        }

    }

    // @formatter:off
    @Nested class GetAllSubtermTests     extends Fixture implements StrategoTupleTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends Fixture implements StrategoTupleTests.GetAnnotationsTests {}
    @Nested class GetAttachmentTests     extends Fixture implements StrategoTupleTests.GetAttachmentTests {}
    @Nested class GetSubtermCountTests   extends Fixture implements StrategoTupleTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends Fixture implements StrategoTupleTests.GetSubtermTests {}
    @Nested class GetTermTypeTests       extends Fixture implements StrategoTupleTests.GetTermTypeTests {}
    @Nested class GetTests               extends Fixture implements StrategoTupleTests.GetTests {}
    @Nested class MatchTests             extends Fixture implements StrategoTupleTests.MatchTests {}
    @Nested class PutAttachmentTests     extends Fixture implements StrategoTupleTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends Fixture implements StrategoTupleTests.RemoveAttachmentTests {}
    @Nested class SizeTests              extends Fixture implements StrategoTupleTests.SizeTests {}
    @Nested class ToStringTests          extends Fixture implements StrategoTupleTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends Fixture implements StrategoTupleTests.WriteAsStringTests {}
    // @formatter:on

}
