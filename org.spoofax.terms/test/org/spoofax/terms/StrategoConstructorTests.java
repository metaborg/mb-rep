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

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoConstructor} class.
 */
public interface StrategoConstructorTests extends IStrategoConstructorTests {

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
