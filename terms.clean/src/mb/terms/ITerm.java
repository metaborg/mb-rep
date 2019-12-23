package mb.terms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A clean rewrite of the {@code IStrategoTerm} interface.
 * <p>
 * <b>N.B.</b> All implementations of this interface should be fully immutable.
 */
@SuppressWarnings("unused")
public interface ITerm {
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
     * A (immutable) list of annotations on a term
     */
    List<ITerm> annotations();

    /**
     * A map of (immutable) attachments, which are annotations that aren't visible in normal printing and are not
     * considered when comparing terms.
     */
    HashMap<Class<?>, Object> attachments();

    ITerm withAnnotations(Iterable<? extends ITerm> annotations);

    ITerm withAttachments(HashMap<Class<?>, Object> attachments);

    default ITerm withAnnotations(ITerm... annotations) {
        return withAnnotations(new ArrayList<>(Arrays.asList(annotations)));
    }

    @SuppressWarnings("unchecked")
    default <T> T getAttachment(Class<T> attachmentClass) {
        return (T) attachments().get(attachmentClass);
    }

    @SuppressWarnings("unchecked")
    default <T> ITerm withAttachment(T attachment) {
        Class<T> attachmentClass = (Class<T>) attachment.getClass();

        HashMap<Class<?>, Object> m = new HashMap<>(this.attachments());
        m.put(attachmentClass, attachment);
        return this.withAttachments(m);
    }

    @SuppressWarnings("unchecked")
    default <T> void mutatePutAttachment(T attachment) {
        Class<T> attachmentClass = (Class<T>) attachment.getClass();
        this.attachments().put(attachmentClass, attachment);
    }

    default void mutatePutAttachments(Iterable<?> attachments) {
        for(Object attachment : attachments) {
            mutatePutAttachment(attachment);
        }
    }
}
