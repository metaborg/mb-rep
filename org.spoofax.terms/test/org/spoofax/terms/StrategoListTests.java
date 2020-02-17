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
 * Tests the {@link StrategoList} class.
 */
@DisplayName("StrategoList")
public class StrategoListTests {


    public interface Fixture extends IStrategoListTests.Fixture {

        /**
         * Creates a new instance of {@link StrategoList} for testing.
         *
         * @param elements    the elements of the list; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        StrategoList createStrategoList(@Nullable List<IStrategoTerm> elements,
                                               @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

        /**
         * Creates a new instance of a cons-nil {@link StrategoList} for testing.
         *
         * @param head        the head of the list; or {@code null} to use a sensible default
         * @param tail        the tail of the list; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        StrategoList createConsNilStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail, @Nullable IStrategoList annotations,
                                                      @Nullable List<ITermAttachment> attachments);

        /**
         * Creates a new instance of an empty {@link StrategoList} for testing.
         * <p>
         * This creates an empty list.
         *
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        StrategoList createEmptyStrategoList(@Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);
    }

    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoList createStrategoList(@Nullable List<IStrategoTerm> elements,
                                               @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            if (elements == null || elements.isEmpty()) {
                return createEmptyStrategoList(annotations, attachments);
            }
            return createConsNilStrategoList(elements.get(0), createStrategoList(elements.subList(1, elements.size()), null, null), annotations, attachments);
        }

        @Override
        public StrategoList createConsNilStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail, @Nullable IStrategoList annotations,
                                                      @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoList(
                    head != null ? head : new DummyStrategoTerm(),
                    tail != null ? tail : new StrategoList(null),
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public StrategoList createEmptyStrategoList(@Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return TermUtil.putAttachments(new StrategoList(
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public IStrategoList createIStrategoList(@Nullable List<IStrategoTerm> elements,
                                                 @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoList(elements, annotations, attachments);
        }

        @Override
        public IStrategoList createConsNilIStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail, @Nullable IStrategoList annotations,
                                                        @Nullable List<ITermAttachment> attachments) {
            return createConsNilStrategoList(head, tail, annotations, attachments);
        }

        @Override
        public IStrategoList createEmptyIStrategoList(@Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createEmptyStrategoList(annotations, attachments);
        }

        @Override
        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            if (subterms == null || subterms.isEmpty()) {
                return createEmptyStrategoList(annotations, attachments);
            }
            return createConsNilStrategoList(subterms.get(0), createIStrategoList(subterms.subList(1, subterms.size()), null, null), annotations, attachments);
        }

    }


    // @formatter:off
    // IStrategoList
    @Nested class GetTermTypeTests       extends FixtureImpl implements IStrategoListTests.GetTermTypeTests {}
    @Nested class HeadTests              extends FixtureImpl implements IStrategoListTests.HeadTests {}
    @Nested class IsEmptyTests           extends FixtureImpl implements IStrategoListTests.IsEmptyTests {}
    @Nested class MatchTests             extends FixtureImpl implements IStrategoListTests.MatchTests {}
    @Nested class SizeTests              extends FixtureImpl implements IStrategoListTests.SizeTests {}
    @Nested class TailTests              extends FixtureImpl implements IStrategoListTests.TailTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoListTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoListTests.WriteAsStringTests {}

    // IStrategoTerm
    @Nested class GetAllSubtermTests     extends FixtureImpl implements IStrategoTermTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoTermTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on

}
