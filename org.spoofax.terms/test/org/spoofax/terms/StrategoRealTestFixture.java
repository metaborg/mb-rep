package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoRealTests;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoReal} class.
 */
@DisplayName("StrategoReal")
public class StrategoRealTestFixture {

    public static class Fixture extends StrategoTermTestFixture.Fixture {

        public StrategoReal createStrategoReal(@Nullable Double value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoReal(
                    value != null ? value : 4.2,
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        public IStrategoReal createIStrategoReal(@Nullable Double value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoReal(value, annotations, attachments);
        }

        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createStrategoReal(null, annotations, attachments);
        }

    }

    // @formatter:off
    @Nested class GetAllSubtermTests     extends Fixture implements StrategoRealTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends Fixture implements StrategoRealTests.GetAnnotationsTests {}
    @Nested class GetAttachmentTests     extends Fixture implements StrategoRealTests.GetAttachmentTests {}
    @Nested class GetSubtermCountTests   extends Fixture implements StrategoRealTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends Fixture implements StrategoRealTests.GetSubtermTests {}
    @Nested class GetTermTypeTests       extends Fixture implements StrategoRealTests.GetTermTypeTests {}
    @Nested class RealValueTests         extends Fixture implements StrategoRealTests.RealValueTests {}
    @Nested class MatchTests             extends Fixture implements StrategoRealTests.MatchTests {}
    @Nested class PutAttachmentTests     extends Fixture implements StrategoRealTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends Fixture implements StrategoRealTests.RemoveAttachmentTests {}
    @Nested class ToStringTests          extends Fixture implements StrategoRealTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends Fixture implements StrategoRealTests.WriteAsStringTests {}
    // @formatter:on

}
