package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoTuple} class.
 */
@DisplayName("StrategoTuple")
public class StrategoTupleTests {

    public interface Fixture extends IStrategoTupleTests.Fixture {

        /**
         * Creates a new instance of {@link StrategoTuple} for testing.
         *
         * @param elements the elements of the tuple; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        StrategoTuple createStrategoTuple(@Nullable List<IStrategoTerm> elements,
                                                 @Nullable IStrategoList annotations,
                                                 @Nullable List<ITermAttachment> attachments);
    }

    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoTuple createStrategoTuple(@Nullable List<IStrategoTerm> elements,
                                                 @Nullable IStrategoList annotations,
                                                 @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoTuple(
                    elements != null ? elements.toArray(new IStrategoTerm[0]) : new IStrategoTerm[]{new DummyStrategoTerm()},
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public IStrategoTuple createIStrategoTuple(@Nullable List<IStrategoTerm> elements,
                                                   @Nullable IStrategoList annotations,
                                                   @Nullable List<ITermAttachment> attachments) {
            return createStrategoTuple(elements, annotations, attachments);
        }

        @Override
        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoTuple(subterms, annotations, attachments);
        }

    }

    // @formatter:off
    // IStrategoTuple
    @Nested class GetTermTypeTests       extends FixtureImpl implements IStrategoTupleTests.GetTermTypeTests {}
    @Nested class GetTests               extends FixtureImpl implements IStrategoTupleTests.GetTests {}
    @Nested class MatchTests             extends FixtureImpl implements IStrategoTupleTests.MatchTests {}
    @Nested class SizeTests              extends FixtureImpl implements IStrategoTupleTests.SizeTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoTupleTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoTupleTests.WriteAsStringTests {}

    // IStrategoTerm
    @Nested class GetAllSubtermTests     extends FixtureImpl implements IStrategoTermTests.GetAllSubtermTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoTermTests.GetSubtermCountTests {}
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on


}
