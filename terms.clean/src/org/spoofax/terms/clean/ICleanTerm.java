package org.spoofax.terms.clean;

import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A clean rewrite of the {@code IStrategoTerm} interface.
 * <p>
 * <b>N.B.</b> All implementations of this interface should be immutable.
 */
@SuppressWarnings("unused")
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
    List<ICleanTerm> annotations();

    /**
     * Some attachments, which are annotations that aren't visible in normal printing and are not considered when comparing terms.
     */
    ImmutableClassToInstanceMap<ICleanTermAttachment> attachments();

    ICleanTerm withAnnotations(Iterable<? extends ICleanTerm> annotations);

    ICleanTerm withAttachments(ImmutableClassToInstanceMap<ICleanTermAttachment> attachments);

    default ICleanTerm withAnnotations(ICleanTerm... annotations) {
        return withAnnotations(new ArrayList<>(Arrays.asList(annotations)));
    }
}
