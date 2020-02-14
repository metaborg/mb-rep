package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.opentest4j.TestAbortedException;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.IStrategoTupleTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * Tests the {@link StrategoTuple} class.
 */
@DisplayName("StrategoTuple")
public class StrategoTupleTests extends StrategoTermTests implements IStrategoTupleTests {

    @Override
    public IStrategoTuple createStrategoTuple(@Nullable List<IStrategoTerm> elements,
                                              @Nullable IStrategoList annotations,
                                              @Nullable List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoTuple(
                    elements != null ? elements.toArray(new IStrategoTerm[0]) : new IStrategoTerm[] { new DummyStrategoTerm() },
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
                ), attachments);
    }

    @Override
    public IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
        return createStrategoTuple(subterms, annotations, attachments);
    }

    // @formatter:off
    static class GetAllSubtermTests     extends StrategoTupleTests implements IStrategoTupleTests.GetAllSubtermTests {}
    static class GetAnnotationsTests    extends StrategoTupleTests implements IStrategoTupleTests.GetAnnotationsTests {}
    static class GetAttachmentTests     extends StrategoTupleTests implements IStrategoTupleTests.GetAttachmentTests {}
    static class GetSubtermCountTests   extends StrategoTupleTests implements IStrategoTupleTests.GetSubtermCountTests {}
    static class GetSubtermTests        extends StrategoTupleTests implements IStrategoTupleTests.GetSubtermTests {}
    static class GetTermTypeTests       extends StrategoTupleTests implements IStrategoTupleTests.GetTermTypeTests {}
    static class GetTests               extends StrategoTupleTests implements IStrategoTupleTests.GetTests {}
    static class MatchTests             extends StrategoTupleTests implements IStrategoTupleTests.MatchTests {}
    static class PutAttachmentTests     extends StrategoTupleTests implements IStrategoTupleTests.PutAttachmentTests {}
    static class RemoveAttachmentTests  extends StrategoTupleTests implements IStrategoTupleTests.RemoveAttachmentTests {}
    static class SizeTests              extends StrategoTupleTests implements IStrategoTupleTests.SizeTests {}
    static class ToStringTests          extends StrategoTupleTests implements IStrategoTupleTests.ToStringTests {}
    static class WriteAsStringTests     extends StrategoTupleTests implements IStrategoTupleTests.WriteAsStringTests {}
    // @formatter:on

}
