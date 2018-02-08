package org.spoofax.terms.clean;

import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * A clean rewrite of the {@code IStrategoTerm} interface.
 *
 * <b>N.B.</b> All implementations of this interface should be immutable.
 */
public interface ICleanTerm {
    /**
     * Use this enum for fast dispatch (this is faster than an if-else chain of instanceof checks).
     */
    enum TermKind {
        Application,
        List,
        Int,
        Float,
        String,
        Set,
        Map,
        Extension
    }

    /**
     * @see TermKind
     */
    TermKind getTermKind();

    /**
     * A list of annotations on a term
     */
    ICleanTerm[] annotations();

    /**
     * Some attachments, which are annotations that aren't visible in normal printing and are not considered when comparing terms.
     */
    ImmutableClassToInstanceMap<ICleanTermAttachment> attachments();

    ICleanTerm withAnnotations(ICleanTerm... elements);
    ICleanTerm withAttachments(ImmutableClassToInstanceMap<ICleanTermAttachment> value);
}
