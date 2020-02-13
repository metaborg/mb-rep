package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.TermUtil;
import org.spoofax.TestBase;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoList} class.
 */
@DisplayName("StrategoList")
public class StrategoListTests extends TestBase implements IStrategoListTests {

    @Nullable
    @Override
    public IStrategoList createStrategoList(IStrategoTerm head, IStrategoList tail, IStrategoList annotations,
                                            List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoList(head, tail, annotations), attachments);
    }

    @Override
    public IStrategoList createStrategoEmptyList(IStrategoList annotations, List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoList(annotations), attachments);
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
