package org.spoofax;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.terms.attachments.ITermAttachment;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Utility functions for working with terms.
 */
public final class TermUtil {

    /**
     * Puts all given attachments in the given term and returns the resulting term.
     * <p>
     * If there are multiple term attachments of the same type, the later attachments
     * override the earlier ones.
     *
     * @param term        the term to modify
     * @param attachments the attachments to attach; or {@code null}
     * @param <T>         the type of term
     * @return the resulting term
     */
    @Nullable
    public static <T extends ISimpleTerm> T putAttachments(@Nullable T term,
                                                           @Nullable List<ITermAttachment> attachments) {
        if (term == null) return null;
        if (attachments != null && !attachments.isEmpty()) {
            for (ITermAttachment attachment : attachments) {
                term.putAttachment(attachment);
            }
        }
        return term;
    }

}
