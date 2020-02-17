package org.spoofax.terms;

import org.junit.jupiter.api.DisplayName;
import org.spoofax.DummyStrategoTerm;
import org.spoofax.TermUtil;
import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Tests the {@link StrategoList} class.
 */
public interface StrategoListTests extends StrategoTermTests {

    /**
     * Creates a new instance of {@link StrategoList} for testing.
     *
     * @param elements the elements of the list; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    default StrategoList createStrategoList(@Nullable List<IStrategoTerm> elements,
                                              @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments) {
        if (elements == null || elements.isEmpty()) { return createEmptyStrategoList(annotations, attachments); }
        return createConsNilStrategoList(elements.get(0), createStrategoList(elements.subList(1, elements.size()), null, null), annotations, attachments);
    }

    /**
     * Creates a new instance of a cons-nil {@link StrategoList} for testing.
     *
     * @param head the head of the list; or {@code null} to use a sensible default
     * @param tail the tail of the list; or {@code null} to use a sensible default
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    StrategoList createConsNilStrategoList(@Nullable IStrategoTerm head, @Nullable IStrategoList tail,
                                             @Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

    /**
     * Creates a new instance of an empty {@link StrategoList} for testing.
     *
     * This creates an empty list.
     *
     * @param annotations the annotations of the term; or {@code null} to use a sensible default
     * @param attachments the attachments of the term; or {@code null} to use a sensible default
     * @return the created object
     * @throws org.opentest4j.TestAbortedException when an instance with the given parameters could not be created
     */
    StrategoList createEmptyStrategoList(@Nullable IStrategoList annotations, @Nullable List<ITermAttachment> attachments);

}
