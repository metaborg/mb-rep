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
 * Tests the {@link StrategoInt} class.
 */
@DisplayName("StrategoInt")
public class StrategoIntTests {

    public interface Fixture extends IStrategoIntTests.Fixture {

        @Override
        StrategoInt createIStrategoInt(@Nullable Integer value, @Nullable IStrategoList annotations,
                                       @Nullable List<ITermAttachment> attachments);

    }


    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoInt createIStrategoInt(@Nullable Integer value, @Nullable IStrategoList annotations,
                                              @Nullable List<ITermAttachment> attachments) {
            return TestUtils.putAttachments(new StrategoInt(
                    value != null ? value : 42,
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public StrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createIStrategoInt(null, annotations, attachments);
        }

    }


    // @formatter:off
    // IStrategoInt
    @Nested class GetAllSubtermsTests    extends FixtureImpl implements IStrategoIntTests.GetAllSubtermsTests {}
    @Nested class GetSubtermsTests       extends FixtureImpl implements IStrategoIntTests.GetSubtermsTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoIntTests.GetSubtermCountTests {}
    @Nested class GetTermTypeTests       extends FixtureImpl implements IStrategoIntTests.GetTermTypeTests {}
    @Nested class IntValueTests          extends FixtureImpl implements IStrategoIntTests.IntValueTests {}
    @Nested class MatchTests             extends FixtureImpl implements IStrategoIntTests.MatchTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoIntTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoIntTests.WriteAsStringTests {}

    // IStrategoTerm
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on

}
