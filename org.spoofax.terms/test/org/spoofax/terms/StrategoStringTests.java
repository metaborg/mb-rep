package org.spoofax.terms;

import java.util.List;

import javax.annotation.Nullable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.opentest4j.TestAbortedException;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.ISimpleTermTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoStringTests;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTermTests;
import org.spoofax.terms.attachments.ITermAttachment;

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoString} class.
 */
@DisplayName("StrategoString")
public class StrategoStringTests {

    public interface Fixture extends IStrategoStringTests.Fixture {

        @Override
        StrategoString createIStrategoString(@Nullable String value, @Nullable IStrategoList annotations,
                                             @Nullable List<ITermAttachment> attachments);

    }


    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoString createIStrategoString(@Nullable String value, @Nullable IStrategoList annotations,
                                                    @Nullable List<ITermAttachment> attachments) {
            //noinspection EqualsAndHashcode
            return TestUtils.putAttachments(new StrategoString(value != null ? value : "abc",
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
            return createIStrategoString(null, annotations, attachments);
        }

    }


    // @formatter:off
    // IStrategoString
    @Nested class GetAllSubtermsTests    extends FixtureImpl implements IStrategoStringTests.GetAllSubtermsTests {}
    @Nested class GetSubtermsTests       extends FixtureImpl implements IStrategoStringTests.GetSubtermsTests {}
    @Nested class GetNameTests           extends FixtureImpl implements IStrategoStringTests.GetNameTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoStringTests.GetSubtermCountTests {}
    @Nested class GetTypeTests           extends FixtureImpl implements IStrategoStringTests.GetTypeTests {}
    @Nested class StringValueTests       extends FixtureImpl implements IStrategoStringTests.StringValueTests {}
    @Nested class MatchTests             extends FixtureImpl implements IStrategoStringTests.MatchTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoStringTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoStringTests.WriteAsStringTests {}

    // IStrategoTerm
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on

}
