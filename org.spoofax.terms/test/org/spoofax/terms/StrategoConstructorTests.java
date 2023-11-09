package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.opentest4j.TestAbortedException;
import org.spoofax.TestUtils;
import org.spoofax.interpreter.terms.ISimpleTermTests;
import org.spoofax.interpreter.terms.IStrategoConstructorTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTermTests;
import org.spoofax.terms.attachments.ITermAttachment;

import jakarta.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoConstructor} class.
 */
@DisplayName("StrategoConstructor")
public class StrategoConstructorTests {

    public interface Fixture extends IStrategoConstructorTests.Fixture {

        @Override
        StrategoConstructor createIStrategoConstructor(@Nullable String name, @Nullable Integer arity,
                                                       @Nullable IStrategoList annotations,
                                                       @Nullable List<ITermAttachment> attachments);

    }


    public static class FixtureImpl extends StrategoTermTests.FixtureImpl implements Fixture {

        @Override
        public StrategoConstructor createIStrategoConstructor(@Nullable String name, @Nullable Integer arity,
                                                              @Nullable IStrategoList annotations,
                                                              @Nullable List<ITermAttachment> attachments) {
            if (annotations != null && !annotations.isEmpty())
                throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return TestUtils.putAttachments(new StrategoConstructor(
                    name != null ? name : "Dummy",
                    arity != null ? arity : 0
            ), attachments);
        }

        @Override
        public StrategoTerm createIStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                                @Nullable IStrategoList annotations,
                                                @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createIStrategoConstructor(null, null, annotations, attachments);
        }

    }


    // @formatter:off
    // IStrategoConstructor
    @Nested class GetAllSubtermsTests    extends FixtureImpl implements IStrategoConstructorTests.GetAllSubtermsTests {}
    @Nested class GetSubtermsTests       extends FixtureImpl implements IStrategoConstructorTests.GetSubtermsTests {}
    @Nested class GetArityTests          extends FixtureImpl implements IStrategoConstructorTests.GetArityTests {}
    @Nested class GetNameTests           extends FixtureImpl implements IStrategoConstructorTests.GetNameTests {}
    @Nested class GetSubtermCountTests   extends FixtureImpl implements IStrategoConstructorTests.GetSubtermCountTests {}
    @Nested class GetTypeTests           extends FixtureImpl implements IStrategoConstructorTests.GetTypeTests {}
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
