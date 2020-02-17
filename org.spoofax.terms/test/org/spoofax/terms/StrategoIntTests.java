package org.spoofax.terms;

import org.opentest4j.TestAbortedException;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoIntTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoInt} class.
 */
public interface StrategoIntTests extends StrategoTermTests, IStrategoIntTests {

    /**
     * Creates a new instance of {@link StrategoInt} for testing.
     *
     * @param value the value of the term; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    StrategoInt createStrategoInt(@Nullable Integer value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

}
