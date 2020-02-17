package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;

import static org.spoofax.TestUtils.getTermBuilder;


/**
 * Tests the {@link StrategoPlaceholder} class.
 */
public interface StrategoPlaceholderTests extends IStrategoPlaceholderTests {

    /**
     * Creates a new instance of {@link StrategoPlaceholder} for testing.
     *
     * @param template the template of the placeholder; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    StrategoPlaceholder createStrategoPlaceholder(@Nullable IStrategoTerm template, @Nullable IStrategoList annotations,
                                                    @Nullable List<ITermAttachment> attachments);
}
