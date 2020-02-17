package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoString} class.
 */
@DisplayName("StrategoString")
public class StrategoStringTests {

    public interface Fixture extends IStrategoStringTests.Fixture {

        /**
         * Creates a new instance of {@link StrategoString} for testing.
         *
         * @param value the value of the term; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws TestAbortedException when an instance with the given parameters could not be created
         */
        StrategoString createStrategoString(@Nullable String value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);
    }

    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoString createStrategoString(@Nullable String value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoString(
                    value != null ? value : "abc",
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public IStrategoString createIStrategoString(@Nullable String value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoString(value, annotations, attachments);
        }

        @Override
        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createStrategoString(null, annotations, attachments);
        }

    }


    // @formatter:off
    // IStrategoString
    @Nested class GetAllSubtermTests     extends FixtureImpl implements IStrategoStringTests.GetAllSubtermTests {}
    @Nested class GetNameTests           extends FixtureImpl implements IStrategoStringTests.GetNameTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoStringTests.GetSubtermCountTests {}
    @Nested class GetTermTypeTests       extends FixtureImpl implements IStrategoStringTests.GetTermTypeTests {}
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
