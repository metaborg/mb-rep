package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    ImmutableClassToInstanceMap<Object> attachments();

    ITerm withAnnotations(Iterable<? extends ITerm> annotations);

    ITerm withAttachments(ImmutableClassToInstanceMap<Object> attachments);

    default ITerm withAnnotations(ITerm... annotations) {
        return withAnnotations(new ArrayList<>(Arrays.asList(annotations)));
    }

    default <T> T getAttachment(Class<T> attachmentClass) {
        return attachments().getInstance(attachmentClass);
    }

    @SuppressWarnings("unchecked")
    default <T> ITerm withAttachment(T attachment) {
        Class<T> attachmentClass = (Class<T>) attachment.getClass();

        ImmutableClassToInstanceMap.Builder<Object> b = ImmutableClassToInstanceMap.builder();
        ImmutableClassToInstanceMap<Object> m = this.attachments();
        if(m.containsKey(attachmentClass)) {
            for(Map.Entry<Class<?>, Object> e : m.entrySet()) {
                if(!e.getKey().equals(attachmentClass)) {
                    Class<Object> key = (Class<Object>) e.getKey();
                    b.put(key, e.getValue());
                }
            }
        } else {
            b.putAll(m);
        }
        b.put(attachmentClass, attachment);
        return this.withAttachments(b.build());
    }
}
