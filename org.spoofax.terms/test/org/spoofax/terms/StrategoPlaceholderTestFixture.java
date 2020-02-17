package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoPlaceholder;
import org.spoofax.interpreter.terms.IStrategoPlaceholderTests;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.getTermBuilder;


/**
 * Tests the {@link StrategoPlaceholder} class.
 */
@DisplayName("StrategoPlaceholder")
public class StrategoPlaceholderTestFixture {

    public static class Fixture extends StrategoApplTestFixture.Fixture {

        public StrategoPlaceholder createStrategoPlaceholder(@Nullable IStrategoTerm template, @Nullable IStrategoList annotations,
                                                             @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoPlaceholder(
                    getTermBuilder().makeConstructor("<>", 1),
                    template != null ? template : new DummyStrategoTerm(),
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        public IStrategoPlaceholder createIStrategoPlaceholder(@Nullable IStrategoTerm template, @Nullable IStrategoList annotations,
                                                               @Nullable List<ITermAttachment> attachments) {
            return createStrategoPlaceholder(template, annotations, attachments);
        }

    }

    // @formatter:off
    @Nested class GetAllSubtermTests     extends Fixture implements StrategoPlaceholderTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends Fixture implements StrategoPlaceholderTests.GetAnnotationsTests {}
    @Nested class GetAttachmentTests     extends Fixture implements StrategoPlaceholderTests.GetAttachmentTests {}
    @Nested class GetSubtermCountTests   extends Fixture implements StrategoPlaceholderTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends Fixture implements StrategoPlaceholderTests.GetSubtermTests {}
    @Nested class GetTemplateTests       extends Fixture implements StrategoPlaceholderTests.GetTemplateTests {}
    @Nested class GetTermTypeTests       extends Fixture implements StrategoPlaceholderTests.GetTermTypeTests {}
    @Nested class MatchTests             extends Fixture implements StrategoPlaceholderTests.MatchTests {}
    @Nested class PutAttachmentTests     extends Fixture implements StrategoPlaceholderTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends Fixture implements StrategoPlaceholderTests.RemoveAttachmentTests {}
    @Nested class ToStringTests          extends Fixture implements StrategoPlaceholderTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends Fixture implements StrategoPlaceholderTests.WriteAsStringTests {}
    // @formatter:on

}
