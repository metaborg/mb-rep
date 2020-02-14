package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoStringTests;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoString} class.
 */
@DisplayName("StrategoString")
public class StrategoStringTests extends StrategoTermTests implements IStrategoStringTests {

    @Override
    public IStrategoString createStrategoString(@Nullable String value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoString(
                    value != null ? value : "abc",
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
                ), attachments);
    }

    @Override
    public IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                            @Nullable IStrategoList annotations,
                                            @Nullable List<ITermAttachment> attachments) {
        if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
        return createStrategoString(null, annotations, attachments);
    }

    // @formatter:off
    @Nested static class GetAllSubtermTests     extends StrategoStringTests implements IStrategoStringTests.GetAllSubtermTests {}
    static class GetAnnotationsTests    extends StrategoStringTests implements IStrategoStringTests.GetAnnotationsTests {}
    static class GetAttachmentTests     extends StrategoStringTests implements IStrategoStringTests.GetAttachmentTests {}
    static class GetNameTests           extends StrategoStringTests implements IStrategoStringTests.GetNameTests {}
    static class GetSubtermCountTests   extends StrategoStringTests implements IStrategoStringTests.GetSubtermCountTests {}
    static class GetSubtermTests        extends StrategoStringTests implements IStrategoStringTests.GetSubtermTests {}
    static class GetTermTypeTests       extends StrategoStringTests implements IStrategoStringTests.GetTermTypeTests {}
    static class StringValueTests       extends StrategoStringTests implements IStrategoStringTests.StringValueTests {}
    static class MatchTests             extends StrategoStringTests implements IStrategoStringTests.MatchTests {}
    static class PutAttachmentTests     extends StrategoStringTests implements IStrategoStringTests.PutAttachmentTests {}
    static class RemoveAttachmentTests  extends StrategoStringTests implements IStrategoStringTests.RemoveAttachmentTests {}
    static class ToStringTests          extends StrategoStringTests implements IStrategoStringTests.ToStringTests {}
    static class WriteAsStringTests     extends StrategoStringTests implements IStrategoStringTests.WriteAsStringTests {}
    // @formatter:on

}
