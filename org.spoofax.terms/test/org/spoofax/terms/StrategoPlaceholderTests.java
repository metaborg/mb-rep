package org.spoofax.terms;

import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.getTermBuilder;


/**
 * Tests the {@link StrategoPlaceholder} class.
 */
public class StrategoPlaceholderTests {

    public interface Fixture extends IStrategoPlaceholderTests.Fixture {

        /**
         * Creates a new instance of {@link StrategoPlaceholder} for testing.
         *
         * @param template    the template of the placeholder; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        StrategoPlaceholder createStrategoPlaceholder(@Nullable IStrategoTerm template, @Nullable IStrategoList annotations,
                                                             @Nullable List<ITermAttachment> attachments);
    }

    public static class FixtureImpl extends StrategoApplTests.FixtureImpl implements Fixture {

        @Override
        public StrategoPlaceholder createStrategoPlaceholder(@Nullable IStrategoTerm template, @Nullable IStrategoList annotations,
                                                             @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoPlaceholder(
                    getTermBuilder().makeConstructor("<>", 1),
                    template != null ? template : new DummyStrategoTerm(),
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public IStrategoPlaceholder createIStrategoPlaceholder(@Nullable IStrategoTerm template, @Nullable IStrategoList annotations,
                                                               @Nullable List<ITermAttachment> attachments) {
            return createStrategoPlaceholder(template, annotations, attachments);
        }

    }

    // @formatter:off
    // IStrategoPlaceholder
    @Nested class GetAllSubtermTests     extends FixtureImpl implements IStrategoPlaceholderTests.GetAllSubtermTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoPlaceholderTests.GetSubtermCountTests {}
    @Nested class GetTemplateTests       extends FixtureImpl implements IStrategoPlaceholderTests.GetTemplateTests {}
    @Nested class GetTermTypeTests       extends FixtureImpl implements IStrategoPlaceholderTests.GetTermTypeTests {}
    @Nested class MatchTests             extends FixtureImpl implements IStrategoPlaceholderTests.MatchTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoPlaceholderTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoPlaceholderTests.WriteAsStringTests {}

    // IStrategoTerm
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on

}
