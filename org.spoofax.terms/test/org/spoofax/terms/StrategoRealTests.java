package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoRealTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoReal} class.
 */
@DisplayName("StrategoReal")
public class StrategoRealTests extends StrategoTermTests implements IStrategoRealTests {

    @Override
    public IStrategoReal createStrategoReal(@Nullable Double value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoReal(
                    value != null ? value : 4.2,
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
                ), attachments);
    }

    @Override
    public IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                            @Nullable IStrategoList annotations,
                                            @Nullable List<ITermAttachment> attachments) {
        if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
        return createStrategoReal(null, annotations, attachments);
    }

    // @formatter:off
    static class GetAllSubtermTests     extends StrategoRealTests implements IStrategoRealTests.GetAllSubtermTests {}
    static class GetAnnotationsTests    extends StrategoRealTests implements IStrategoRealTests.GetAnnotationsTests {}
    static class GetAttachmentTests     extends StrategoRealTests implements IStrategoRealTests.GetAttachmentTests {}
    static class GetSubtermCountTests   extends StrategoRealTests implements IStrategoRealTests.GetSubtermCountTests {}
    static class GetSubtermTests        extends StrategoRealTests implements IStrategoRealTests.GetSubtermTests {}
    static class GetTermTypeTests       extends StrategoRealTests implements IStrategoRealTests.GetTermTypeTests {}
    static class RealValueTests         extends StrategoRealTests implements IStrategoRealTests.RealValueTests {}
    static class MatchTests             extends StrategoRealTests implements IStrategoRealTests.MatchTests {}
    static class PutAttachmentTests     extends StrategoRealTests implements IStrategoRealTests.PutAttachmentTests {}
    static class RemoveAttachmentTests  extends StrategoRealTests implements IStrategoRealTests.RemoveAttachmentTests {}
    static class ToStringTests          extends StrategoRealTests implements IStrategoRealTests.ToStringTests {}
    static class WriteAsStringTests     extends StrategoRealTests implements IStrategoRealTests.WriteAsStringTests {}
    // @formatter:on

}
