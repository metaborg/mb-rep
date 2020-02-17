package org.spoofax.terms;

import org.junit.jupiter.api.Nested;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoConstructor} class.
 */
public class StrategoConstructorTests {

    public interface Fixture extends IStrategoConstructorTests.Fixture {

        /**
         * Creates a new instance of {@link StrategoConstructor} for testing.
         *
         * @param name the constructor name; or {@code null} to use a sensible default
         * @param arity the constructor arity; or {@code null} to use a sensible default
         * @param annotations the annotations of the term; or {@code null} to use a sensible default
         * @param attachments the attachments of the term; or {@code null} to use a sensible default
         * @return the created object
         * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
         */
        StrategoConstructor createStrategoConstructor(@Nullable String name, @Nullable Integer arity, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);
    }

    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoConstructor createStrategoConstructor(@Nullable String name, @Nullable Integer arity, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            if (annotations != null && !annotations.isEmpty())
                throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return TermUtil.putAttachments(new StrategoConstructor(
                    name != null ? name : "Dummy",
                    arity != null ? arity : 0
            ), attachments);
        }

        @Override
        public IStrategoConstructor createIStrategoConstructor(@Nullable String name, @Nullable Integer arity, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoConstructor(name, arity, annotations, attachments);
        }

        @Override
        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createStrategoConstructor(null, null, annotations, attachments);
        }

    }


    // @formatter:off
    // IStrategoConstructor
    @Nested class GetAllSubtermTests     extends FixtureImpl implements IStrategoConstructorTests.GetAllSubtermTests {}
    @Nested class GetArityTests          extends FixtureImpl implements IStrategoConstructorTests.GetArityTests {}
    @Nested class GetNameTests           extends FixtureImpl implements IStrategoConstructorTests.GetNameTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoConstructorTests.GetSubtermCountTests {}
    @Nested class GetTermTypeTests       extends FixtureImpl implements IStrategoConstructorTests.GetTermTypeTests {}
    @Nested class MatchTests             extends FixtureImpl implements IStrategoConstructorTests.MatchTests {}
    @Nested class ToStringTests          extends FixtureImpl implements IStrategoConstructorTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends FixtureImpl implements IStrategoConstructorTests.WriteAsStringTests {}

    // IStrategoTerm
    @Nested class GetAnnotationsTests    extends FixtureImpl implements IStrategoTermTests.GetAnnotationsTests {}
    @Nested class GetSubtermTests        extends FixtureImpl implements IStrategoTermTests.GetSubtermTests {}

    // ISimpleTerm
    @Nested class GetAttachmentTests     extends FixtureImpl implements ISimpleTermTests.GetAttachmentTests {}
    @Nested class PutAttachmentTests     extends FixtureImpl implements ISimpleTermTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends FixtureImpl implements ISimpleTermTests.RemoveAttachmentTests {}
    // @formatter:on


}
