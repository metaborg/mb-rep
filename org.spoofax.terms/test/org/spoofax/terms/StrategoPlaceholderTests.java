package org.spoofax.terms;

import java.util.List;

import javax.annotation.Nullable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.ISimpleTermTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoPlaceholderTests;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTermTests;
import org.spoofax.terms.attachments.ITermAttachment;

import static org.spoofax.TestUtils.getTermBuilder;


/**
 * Tests the {@link StrategoPlaceholder} class.
 */
@DisplayName("StrategoPlaceholder")
public class StrategoPlaceholderTests {

    public interface Fixture extends IStrategoPlaceholderTests.Fixture {

        @Override
        StrategoPlaceholder createIStrategoPlaceholder(@Nullable IStrategoTerm template,
                                                       @Nullable IStrategoList annotations,
                                                       @Nullable List<ITermAttachment> attachments);

    }


    public static class FixtureImpl extends StrategoApplTests.FixtureImpl implements Fixture {

        @Override
        public StrategoPlaceholder createIStrategoPlaceholder(@Nullable IStrategoTerm template,
                                                              @Nullable IStrategoList annotations,
                                                              @Nullable List<ITermAttachment> attachments) {
            //noinspection EqualsAndHashcode
            return TestUtils.putAttachments(new StrategoPlaceholder(getTermBuilder().makeConstructor("<>", 1),
                template != null ? template : new DummyStrategoTerm(),
                annotations != null ? annotations : TermFactory.EMPTY_LIST) {
                @Override
                public int hashCode() {
                    return 0;
                }
            }, attachments);
        }

    }


    // @formatter:off
    // IStrategoPlaceholder
    @Nested class GetAllSubtermsTests    extends FixtureImpl implements IStrategoPlaceholderTests.GetAllSubtermsTests {}
    @Nested class GetSubtermsTests       extends FixtureImpl implements IStrategoPlaceholderTests.GetSubtermsTests {}
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
