package org.spoofax.terms;

import java.util.List;
import javax.annotation.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.ISimpleTermTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoListTests;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTermTests;
import org.spoofax.terms.attachments.ITermAttachment;

import static org.spoofax.terms.util.Assert.assertEquals;


/**
 * Tests the {@link StrategoArrayList} class.
 */
@DisplayName("StrategoArrayList")
public class StrategoArrayListTests {


    public interface Fixture extends IStrategoListTests.Fixture {

        @Override
        StrategoArrayList createIStrategoList(@Nullable List<IStrategoTerm> elements, @Nullable IStrategoList annotations,
            @Nullable List<ITermAttachment> attachments);

        @Override
        StrategoList createConsNilIStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail,
            @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

        @Override
        StrategoArrayList createEmptyIStrategoList(@Nullable IStrategoList annotations,
            @Nullable List<ITermAttachment> attachments);

    }


    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoArrayList createIStrategoList(@Nullable List<IStrategoTerm> elements,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
            if (elements == null || elements.isEmpty()) {
                return createEmptyIStrategoList(annotations, attachments);
            }
            return TestUtils.putAttachments(new StrategoArrayList(elements.toArray(new IStrategoTerm[0]), annotations), attachments);
        }

        @Override
        public StrategoList createConsNilIStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail,
                                                       @Nullable IStrategoList annotations,
                                                       @Nullable List<ITermAttachment> attachments) {
            return TestUtils.putAttachments(new StrategoList(
                    head != null ? head : new DummyStrategoTerm(),
                    tail != null ? tail : createEmptyIStrategoList(null, null),
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public StrategoArrayList createEmptyIStrategoList(@Nullable IStrategoList annotations,
                                                     @Nullable List<ITermAttachment> attachments) {
            return TestUtils.putAttachments(new StrategoArrayList(new IStrategoTerm[0],
                    annotations != null ? annotations : TermFactory.EMPTY_LIST
            ), attachments);
        }

        @Override
        public StrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
            if (subterms == null || subterms.isEmpty()) {
                return createEmptyIStrategoList(annotations, attachments);
            }
            return createIStrategoList(subterms, annotations, attachments);
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
    @Nested class HashCodeTests          extends FixtureImpl implements IStrategoListTests.HashCodeTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoListTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoListTests.WriteAsStringTests {}

    // IStrategoTerm
    @Nested class GetAllSubtermsTests    extends FixtureImpl implements IStrategoTermTests.GetAllSubtermsTests {}
    @Nested class GetSubtermsTests       extends FixtureImpl implements IStrategoTermTests.GetSubtermsTests {}
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoTermTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on


    @Test
    @DisplayName("builder with extra capacity makes correct size list")
    void builderWithExtraCapaxity_makesCorrectSizeList() {
        // Arrange
        IStrategoList.Builder b = StrategoArrayList.arrayListBuilder(10);
        b.add(new DummyStrategoTerm());
        b.add(new DummyStrategoTerm());
        b.add(new DummyStrategoTerm());

        // Act
        IStrategoList l = b.build();

        // Assert
        assertEquals(3, l.size());
    }


    @Test
    @DisplayName("builder with extra capacity makes list with correct size tails")
    void builderWithExtraCapaxity_makesListWithCorrectSizeTails() {
        // Arrange
        IStrategoList.Builder b = StrategoArrayList.arrayListBuilder(10);
        b.add(new DummyStrategoTerm());
        b.add(new DummyStrategoTerm());
        b.add(new DummyStrategoTerm());

        // Act
        IStrategoList l = b.build();

        // Assert
        assertEquals(2, l.tail().size());
        assertEquals(1, l.tail().tail().size());
        assertEquals(0, l.tail().tail().tail().size());
    }
}
