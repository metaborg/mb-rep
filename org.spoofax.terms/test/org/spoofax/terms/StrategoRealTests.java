package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.opentest4j.TestAbortedException;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoReal} class.
 */
@DisplayName("StrategoReal")
public class StrategoRealTests {

    public interface Fixture extends IStrategoRealTests.Fixture {

        @Override
        StrategoReal createIStrategoReal(@Nullable Double value, @Nullable IStrategoList annotations,
                                         @Nullable List<ITermAttachment> attachments);

    }


    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoReal createIStrategoReal(@Nullable Double value, @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
            //noinspection EqualsAndHashcode
            return TestUtils.putAttachments(new StrategoReal(value != null ? value : 4.2,
                annotations != null ? annotations : TermFactory.EMPTY_LIST) {
                @Override
                public int hashCode() {
                    return 0;
                }
            }, attachments);
        }

        @Override
        public StrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createIStrategoReal(null, annotations, attachments);
        }

    }


    // @formatter:off
    // IStrategoReal
    @Nested class GetAllSubtermsTests    extends FixtureImpl implements IStrategoRealTests.GetAllSubtermsTests {}
    @Nested class GetSubtermsTests       extends FixtureImpl implements IStrategoRealTests.GetSubtermsTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoRealTests.GetSubtermCountTests {}
    @Nested class GetTypeTests           extends FixtureImpl implements IStrategoRealTests.GetTypeTests {}
    @Nested class RealValueTests         extends FixtureImpl implements IStrategoRealTests.RealValueTests {}
    @Nested class MatchTests             extends FixtureImpl implements IStrategoRealTests.MatchTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoRealTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoRealTests.WriteAsStringTests {}

    // IStrategoTerm
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on

}
