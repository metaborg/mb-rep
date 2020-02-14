package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoConstructorTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoConstructor} class.
 */
@DisplayName("StrategoConstructor")
public class StrategoConstructorTests extends StrategoTermTests implements IStrategoConstructorTests {

    @Override
    public IStrategoConstructor createStrategoConstructor(@Nullable String name, @Nullable Integer arity, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
        if (annotations != null && !annotations.isEmpty())  throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
        return TermUtil.putAttachments(new StrategoConstructor(
                    name != null ? name : "Dummy",
                    arity != null ? arity : 0
                ), attachments);
    }

    @Override
    public IStrategoTerm createStrategoTerm(@Nullable List<IStrategoTerm> subterms,
                                            @Nullable IStrategoList annotations,
                                            @Nullable List<ITermAttachment> attachments) {
        if (subterms != null && subterms.size() != 0)  throw new TestAbortedException(TEST_INSTANCE_NOT_CREATED);
        return createStrategoConstructor(null, null, annotations, attachments);
    }

    // @formatter:off
    static class GetAllSubtermTests     extends StrategoConstructorTests implements IStrategoConstructorTests.GetAllSubtermTests {}
    static class GetAnnotationsTests    extends StrategoConstructorTests implements IStrategoConstructorTests.GetAnnotationsTests {}
    static class GetArityTests          extends StrategoConstructorTests implements IStrategoConstructorTests.GetArityTests {}
    static class GetAttachmentTests     extends StrategoConstructorTests implements IStrategoConstructorTests.GetAttachmentTests {}
    static class GetNameTests           extends StrategoConstructorTests implements IStrategoConstructorTests.GetNameTests {}
    static class GetSubtermCountTests   extends StrategoConstructorTests implements IStrategoConstructorTests.GetSubtermCountTests {}
    static class GetSubtermTests        extends StrategoConstructorTests implements IStrategoConstructorTests.GetSubtermTests {}
    static class GetTermTypeTests       extends StrategoConstructorTests implements IStrategoConstructorTests.GetTermTypeTests {}
    static class MatchTests             extends StrategoConstructorTests implements IStrategoConstructorTests.MatchTests {}
    static class PutAttachmentTests     extends StrategoConstructorTests implements IStrategoConstructorTests.PutAttachmentTests {}
    static class RemoveAttachmentTests  extends StrategoConstructorTests implements IStrategoConstructorTests.RemoveAttachmentTests {}
    static class ToStringTests          extends StrategoConstructorTests implements IStrategoConstructorTests.ToStringTests {}
    static class WriteAsStringTests     extends StrategoConstructorTests implements IStrategoConstructorTests.WriteAsStringTests {}
    // @formatter:on

}
