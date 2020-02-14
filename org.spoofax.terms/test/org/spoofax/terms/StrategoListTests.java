package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoList} class.
 */
@DisplayName("StrategoList")
public class StrategoListTests extends StrategoTermTests implements IStrategoListTests {

    @Override
    public IStrategoList createStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail, @Nullable IStrategoList annotations,
                                            @Nullable List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoList(
                    head != null ? head : new DummyStrategoTerm(),
                    tail != null ? tail : new StrategoList(null),
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
                ), attachments);
    }

    @Override
    public IStrategoList createStrategoEmptyList(@Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoList(
            annotations != null ? annotations : TermFactory.EMPTY_LIST
        ), attachments);
    }

    @Override
    public IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                            @Nullable IStrategoList annotations,
                                            @Nullable List<ITermAttachment> attachments) {
        if (subterms == null || subterms.isEmpty()) { return createStrategoEmptyList(annotations, attachments); }
        return createStrategoList(subterms.get(0), createStrategoList(subterms.subList(1, subterms.size()), null, null), annotations, attachments);
    }

    // @formatter:off
    static class GetAllSubtermTests     extends StrategoListTests implements IStrategoListTests.GetAllSubtermTests {}
    static class GetAnnotationsTests    extends StrategoListTests implements IStrategoListTests.GetAnnotationsTests {}
    static class GetAttachmentTests     extends StrategoListTests implements IStrategoListTests.GetAttachmentTests {}
    static class GetSubtermCountTests   extends StrategoListTests implements IStrategoListTests.GetSubtermCountTests {}
    static class GetSubtermTests        extends StrategoListTests implements IStrategoListTests.GetSubtermTests {}
    static class GetTermTypeTests       extends StrategoListTests implements IStrategoListTests.GetTermTypeTests {}
    static class HeadTests              extends StrategoListTests implements IStrategoListTests.HeadTests {}
    static class IsEmptyTests           extends StrategoListTests implements IStrategoListTests.IsEmptyTests {}
    static class MatchTests             extends StrategoListTests implements IStrategoListTests.MatchTests {}
    static class PutAttachmentTests     extends StrategoListTests implements IStrategoListTests.PutAttachmentTests {}
    static class RemoveAttachmentTests  extends StrategoListTests implements IStrategoListTests.RemoveAttachmentTests {}
    static class SizeTests              extends StrategoListTests implements IStrategoListTests.SizeTests {}
    static class TailTests              extends StrategoListTests implements IStrategoListTests.TailTests {}
    static class ToStringTests          extends StrategoListTests implements IStrategoListTests.ToStringTests {}
    static class WriteAsStringTests     extends StrategoListTests implements IStrategoListTests.WriteAsStringTests {}
    // @formatter:on


}
