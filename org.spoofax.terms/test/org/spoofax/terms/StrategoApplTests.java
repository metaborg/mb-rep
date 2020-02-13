package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoAppl} class.
 */
@DisplayName("StrategoAppl")
public class StrategoApplTests implements IStrategoApplTests {

    @Override
    public IStrategoTermBuilder getTermBuilder() {
        return new TermFactory();
    }
    @Nullable
    @Override
    public IStrategoAppl createStrategoAppl(IStrategoConstructor constructor, List<IStrategoTerm> subterms,
                                            IStrategoList annotations, List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoAppl(constructor, subterms.toArray(new IStrategoTerm[0]), annotations), attachments);
    }

    // @formatter:off
    static class GetAllSubtermTests     extends StrategoApplTests implements IStrategoApplTests.GetAllSubtermTests {}
    static class GetAnnotationsTests    extends StrategoApplTests implements IStrategoApplTests.GetAnnotationsTests {}
    static class GetAttachmentTests     extends StrategoApplTests implements IStrategoApplTests.GetAttachmentTests {}
    static class GetConstructorTests    extends StrategoApplTests implements IStrategoApplTests.GetConstructorTests {}
    static class GetNameTests           extends StrategoApplTests implements IStrategoApplTests.GetNameTests {}
    static class GetSubtermCountTests   extends StrategoApplTests implements IStrategoApplTests.GetSubtermCountTests {}
    static class GetSubtermTests        extends StrategoApplTests implements IStrategoApplTests.GetSubtermTests {}
    static class GetTermTypeTests       extends StrategoApplTests implements IStrategoApplTests.GetTermTypeTests {}
    static class MatchTests             extends StrategoApplTests implements IStrategoApplTests.MatchTests {}
    static class PutAttachmentTests     extends StrategoApplTests implements IStrategoApplTests.PutAttachmentTests {}
    static class RemoveAttachmentTests  extends StrategoApplTests implements IStrategoApplTests.RemoveAttachmentTests {}
    static class ToStringTests          extends StrategoApplTests implements IStrategoApplTests.ToStringTests {}
    static class WriteAsStringTests     extends StrategoApplTests implements IStrategoApplTests.WriteAsStringTests {}
    // @formatter:on


}
