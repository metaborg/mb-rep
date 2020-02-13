package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.TermUtil;
import org.spoofax.TestBase;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoPlaceholder} class.
 */
@DisplayName("StrategoPlaceholder")
public class StrategoPlaceholderTests extends TestBase implements IStrategoPlaceholderTests {

    @Nullable
    @Override
    public IStrategoPlaceholder createStrategoPlaceholder(@Nullable IStrategoTerm template, @Nullable IStrategoList annotations,
                                                          @Nullable List<ITermAttachment> attachments) {
        return TermUtil.putAttachments(new StrategoPlaceholder(getTermBuilder().makeConstructor("<>", 1), template, annotations), attachments);
    }

    // @formatter:off
    static class GetAllSubtermTests     extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.GetAllSubtermTests {}
    static class GetAnnotationsTests    extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.GetAnnotationsTests {}
    static class GetAttachmentTests     extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.GetAttachmentTests {}
    static class GetSubtermCountTests   extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.GetSubtermCountTests {}
    static class GetSubtermTests        extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.GetSubtermTests {}
    static class GetTemplateTests       extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.GetTemplateTests {}
    static class GetTermTypeTests       extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.GetTermTypeTests {}
    static class MatchTests             extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.MatchTests {}
    static class PutAttachmentTests     extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.PutAttachmentTests {}
    static class RemoveAttachmentTests  extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.RemoveAttachmentTests {}
    static class ToStringTests          extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.ToStringTests {}
    static class WriteAsStringTests     extends StrategoPlaceholderTests implements IStrategoPlaceholderTests.WriteAsStringTests {}
    // @formatter:on

    @DisplayName("StrategoPlaceholder/StrategoAppl")
    public static class StrategoApplTests extends org.spoofax.terms.StrategoApplTests {

        @Nullable
        @Override
        public IStrategoAppl createStrategoAppl(IStrategoConstructor constructor, List<IStrategoTerm> subterms,
                                                IStrategoList annotations, List<ITermAttachment> attachments) {
            if (!constructor.getName().equals("<>") || constructor.getArity() != 1 || subterms.size() != 1) return null;
            return TermUtil.putAttachments(new StrategoPlaceholder(constructor, subterms.get(0), annotations), attachments);
        }

    }

}
