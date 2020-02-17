package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoInt} class.
 */
@DisplayName("StrategoInt")
public class StrategoIntTestFixture {

    public static class Fixture extends StrategoTermTestFixture.Fixture {

        public StrategoInt createStrategoInt(@Nullable Integer value, @Nullable IStrategoList annotations,
                                             @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoInt(
                    value != null ? value : 42,
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        public IStrategoInt createIStrategoInt(@Nullable Integer value, @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            return createStrategoInt(value, annotations, attachments);
        }

        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createStrategoInt(null, annotations, attachments);
        }

    }

    // @formatter:off
    @Nested class GetAllSubtermTests     extends Fixture implements IStrategoIntTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends Fixture implements StrategoIntTests.GetAnnotationsTests {}
    @Nested class GetAttachmentTests     extends Fixture implements StrategoIntTests.GetAttachmentTests {}
    @Nested class GetSubtermCountTests   extends Fixture implements IStrategoIntTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends Fixture implements IStrategoIntTests.GetSubtermTests {}
    @Nested class GetTermTypeTests       extends Fixture implements IStrategoIntTests.GetTermTypeTests {}
    @Nested class IntValueTests          extends Fixture implements StrategoIntTests.IntValueTests {}
    @Nested class MatchTests             extends Fixture implements IStrategoIntTests.MatchTests {}
    @Nested class PutAttachmentTests     extends Fixture implements StrategoIntTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends Fixture implements StrategoIntTests.RemoveAttachmentTests {}
    @Nested class ToStringTests          extends Fixture implements IStrategoTermTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends Fixture implements IStrategoTermTests.WriteAsStringTests {}
    // @formatter:on

}
