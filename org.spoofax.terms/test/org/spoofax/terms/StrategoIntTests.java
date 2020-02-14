package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoIntTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoInt} class.
 */
@DisplayName("StrategoInt")
public class StrategoIntTests extends StrategoTermTests implements IStrategoIntTests {

    @Override
    public IStrategoInt createStrategoInt(@Nullable Integer value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoInt(
                    value != null ? value : 42,
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
                ), attachments);
    }

    @Override
    public IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                            @Nullable IStrategoList annotations,
                                            @Nullable List<ITermAttachment> attachments) {
        if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
        return createStrategoInt(null, annotations, attachments);
    }

    // @formatter:off
    static class GetAllSubtermTests     extends StrategoIntTests implements IStrategoIntTests.GetAllSubtermTests {}
    static class GetAnnotationsTests    extends StrategoIntTests implements IStrategoIntTests.GetAnnotationsTests {}
    static class GetAttachmentTests     extends StrategoIntTests implements IStrategoIntTests.GetAttachmentTests {}
    static class GetSubtermCountTests   extends StrategoIntTests implements IStrategoIntTests.GetSubtermCountTests {}
    static class GetSubtermTests        extends StrategoIntTests implements IStrategoIntTests.GetSubtermTests {}
    static class GetTermTypeTests       extends StrategoIntTests implements IStrategoIntTests.GetTermTypeTests {}
    static class IntValueTests          extends StrategoIntTests implements IStrategoIntTests.IntValueTests {}
    static class MatchTests             extends StrategoIntTests implements IStrategoIntTests.MatchTests {}
    static class PutAttachmentTests     extends StrategoIntTests implements IStrategoIntTests.PutAttachmentTests {}
    static class RemoveAttachmentTests  extends StrategoIntTests implements IStrategoIntTests.RemoveAttachmentTests {}
    static class ToStringTests          extends StrategoIntTests implements IStrategoIntTests.ToStringTests {}
    static class WriteAsStringTests     extends StrategoIntTests implements IStrategoIntTests.WriteAsStringTests {}
    // @formatter:on

}
