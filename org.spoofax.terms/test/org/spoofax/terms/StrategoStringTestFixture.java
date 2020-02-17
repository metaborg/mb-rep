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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoString} class.
 */
@DisplayName("StrategoString")
public class StrategoStringTestFixture {

    public static class Fixture extends StrategoTermTestFixture.Fixture {

        public StrategoString createStrategoString(@Nullable String value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoString(
                    value != null ? value : "abc",
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        public IStrategoString createIStrategoString(@Nullable String value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoString(value, annotations, attachments);
        }

        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createStrategoString(null, annotations, attachments);
        }

    }

    // @formatter:off
    @Nested class GetAllSubtermTests     extends Fixture implements StrategoStringTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends Fixture implements StrategoStringTests.GetAnnotationsTests {}
    @Nested class GetAttachmentTests     extends Fixture implements StrategoStringTests.GetAttachmentTests {}
    @Nested class GetNameTests           extends Fixture implements StrategoStringTests.GetNameTests {}
    @Nested class GetSubtermCountTests   extends Fixture implements StrategoStringTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends Fixture implements StrategoStringTests.GetSubtermTests {}
    @Nested class GetTermTypeTests       extends Fixture implements StrategoStringTests.GetTermTypeTests {}
    @Nested class StringValueTests       extends Fixture implements StrategoStringTests.StringValueTests {}
    @Nested class MatchTests             extends Fixture implements StrategoStringTests.MatchTests {}
    @Nested class PutAttachmentTests     extends Fixture implements StrategoStringTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends Fixture implements StrategoStringTests.RemoveAttachmentTests {}
    @Nested class ToStringTests          extends Fixture implements StrategoStringTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends Fixture implements StrategoStringTests.WriteAsStringTests {}
    // @formatter:on

}
