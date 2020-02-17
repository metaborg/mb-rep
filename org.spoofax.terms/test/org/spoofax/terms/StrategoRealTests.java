package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.opentest4j.TestAbortedException;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoRealTests;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.TEST_INSTANCE_NOT_CREATED;


/**
 * Tests the {@link StrategoReal} class.
 */
public interface StrategoRealTests extends IStrategoRealTests {

    /**
     * Creates a new instance of {@link StrategoReal} for testing.
     *
     * @param value the value of the term; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws TestAbortedException when an instance with the given parameters could not be created
     */
    StrategoReal createStrategoReal(@Nullable Double value, @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);
}
