package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoConstructorTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoConstructor} class.
 */
@DisplayName("StrategoConstructor")
public class StrategoConstructorTestFixture {

    public static class Fixture extends StrategoTermTestFixture.Fixture {

        public StrategoConstructor createStrategoConstructor(@Nullable String name, @Nullable Integer arity, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            if (annotations != null && !annotations.isEmpty())
                throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return TermUtil.putAttachments(new StrategoConstructor(
                    name != null ? name : "Dummy",
                    arity != null ? arity : 0
            ), attachments);
        }


        public IStrategoConstructor createIStrategoConstructor(@Nullable String name, @Nullable Integer arity, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
            return createStrategoConstructor(name, arity, annotations, attachments);
        }

        public StrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                               @Nullable IStrategoList annotations,
                                               @Nullable List<ITermAttachment> attachments) {
            if (subterms != null && subterms.size() != 0) throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
            return createStrategoConstructor(null, null, annotations, attachments);
        }

    }

    // @formatter:off
    @Nested class GetAllSubtermTests     extends Fixture implements StrategoConstructorTests.GetAllSubtermTests {}
    @Nested class GetAnnotationsTests    extends Fixture implements StrategoConstructorTests.GetAnnotationsTests {}
    @Nested class GetArityTests          extends Fixture implements StrategoConstructorTests.GetArityTests {}
    @Nested class GetAttachmentTests     extends Fixture implements StrategoConstructorTests.GetAttachmentTests {}
    @Nested class GetNameTests           extends Fixture implements StrategoConstructorTests.GetNameTests {}
    @Nested class GetSubtermCountTests   extends Fixture implements StrategoConstructorTests.GetSubtermCountTests {}
    @Nested class GetSubtermTests        extends Fixture implements StrategoConstructorTests.GetSubtermTests {}
    @Nested class GetTermTypeTests       extends Fixture implements StrategoConstructorTests.GetTermTypeTests {}
    @Nested class MatchTests             extends Fixture implements StrategoConstructorTests.MatchTests {}
    @Nested class PutAttachmentTests     extends Fixture implements StrategoConstructorTests.PutAttachmentTests {}
    @Nested class RemoveAttachmentTests  extends Fixture implements StrategoConstructorTests.RemoveAttachmentTests {}
    @Nested class ToStringTests          extends Fixture implements StrategoConstructorTests.ToStringTests {}
    @Nested class WriteAsStringTests     extends Fixture implements StrategoConstructorTests.WriteAsStringTests {}
    // @formatter:on

}
