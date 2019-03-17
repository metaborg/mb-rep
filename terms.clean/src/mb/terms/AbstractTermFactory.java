package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A factory for {@link ITerm}s.
 * It has an observer for every String coming through {@link #stringObserver()}.
 * When possible, use the {@code replace*} methods which copy the attachments and annotations of an old term. There are also versions that allow you to provide your own annotations. The {@code new*} methods set no attachments. If you wish to set your own attachments use {@link ITerm#withAttachments(ImmutableClassToInstanceMap)}.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Value.Immutable
public abstract class AbstractTermFactory {
    public static final ImmutableClassToInstanceMap<Object> NO_ATTACHMENTS = ImmutableClassToInstanceMap.builder().build();

    /**
     * This observer is called with every String given to the factory.
     * For example in {@link #newString(String, List)}, but also in {@link #newAppl(String, List)} and {@code replace*} variants.
     * This can be used to keep track of Strings that are in use so unique strings can be generated.
     *
     * @return The observer
     */
    protected abstract @Nullable
    Consumer<String> stringObserver();

    /**
     * Build a TermInt, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The int value to wrap
     * @return The new TermInt wrapping the value with the annotations/attachments from the old term
     */
    public ITermInt replaceInt(ITerm old, int value) {
        return TermInt.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a TermInt, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The int value to wrap
     * @param annotations The annotation for the term
     * @return The new TermInt wrapping the value with the attachments from the old term
     */
    public ITermInt replaceInt(ITerm old, int value, List<ITerm> annotations) {
        return TermInt.of(value, annotations, old.attachments());
    }

    /**
     * Build a TermFloat, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The float value to wrap
     * @return The new TermFloat wrapping the value with the annotations/attachments from the old term
     */
    public ITermFloat replaceFloat(ITerm old, float value) {
        return TermFloat.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a TermFloat, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The int value to wrap
     * @param annotations The annotation for the term
     * @return The new TermFloat wrapping the value with the attachments from the old term
     */
    public ITermFloat replaceFloat(ITerm old, float value, List<ITerm> annotations) {
        return TermFloat.of(value, annotations, old.attachments());
    }

    /**
     * Build a TermString, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The String value to wrap
     * @return The new TermString wrapping the value with the annotations/attachments from the old term
     */
    public ITermString replaceString(ITerm old, String value) {
        Consumer<String> stringObserver = stringObserver();
        if(stringObserver != null) {
            stringObserver.accept(value);
        }
        return TermString.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a TermString, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The String value to wrap
     * @param annotations The annotation for the term
     * @return The new TermString wrapping the value with the attachments from the old term
     */
    public ITermString replaceString(ITerm old, String value, List<ITerm> annotations) {
        Consumer<String> stringObserver = stringObserver();
        if(stringObserver != null) {
            stringObserver.accept(value);
        }
        return TermString.of(value, annotations, old.attachments());
    }

    /**
     * Build a TermList, using the annotations and attachments from the old term.
     *
     * @param old    The old term from which the annotations and attachments are copied.
     * @param values The term values to wrap
     * @return The new TermList wrapping the values with the annotations/attachments from the old term
     */
    public ITermList replaceList(ITerm old, List<ITerm> values) {
        return this.replaceList(old, values, old.annotations());
    }

    /**
     * Build a TermList, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The term values to wrap
     * @param annotations The annotation for the term
     * @return The new TermList wrapping the values with the attachments from the old term
     */
    public ITermList replaceList(ITerm old, List<ITerm> value, List<ITerm> annotations) {
        return TermList.of(value, Collections.singletonList(annotations), Collections.singletonList(old.attachments()));
    }

    /**
     * Build a TermSet, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The set of term values to wrap
     * @return The new TermSet wrapping the values with the annotations/attachments from the old term
     */
    public ITermSet replaceSet(ITerm old, Set.Immutable<ITerm> value) {
        return TermSet.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a TermSet, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The set of term values to wrap
     * @param annotations The annotation for the term
     * @return The new TermSet wrapping the values with the attachments from the old term
     */
    public ITermSet replaceSet(ITerm old, Set.Immutable<ITerm> value, List<ITerm> annotations) {
        return TermSet.of(value, annotations, old.attachments());
    }

    /**
     * Build a TermMap, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The map of term values to wrap
     * @return The new TermMap wrapping the values with the annotations/attachments from the old term
     */
    public ITermMap replaceMap(ITerm old, Map.Immutable<ITerm, ITerm> value) {
        return TermMap.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a TermMap, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The map of term values to wrap
     * @param annotations The annotation for the term
     * @return The new TermMap wrapping the values with the attachments from the old term
     */
    public ITermMap replaceMap(ITerm old, Map.Immutable<ITerm, ITerm> value, List<ITerm> annotations) {
        return TermMap.of(value, annotations, old.attachments());
    }

    /**
     * Build a TermApplication, using the annotations and attachments from the old term.
     *
     * @param old      The old term from which the annotations and attachments are copied.
     * @param cons     The constructor of the application
     * @param children The child terms of the application
     * @return The new TermApplication wrapping the values with the annotations/attachments from the old term
     */
    public ITermApplication replaceAppl(ITerm old, String cons, List<ITerm> children) {
        return this.replaceAppl(old, cons, children, old.annotations());
    }

    /**
     * Build a TermApplication, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param cons        The constructor of the application
     * @param children    The child terms of the application
     * @param annotations The annotation for the term
     * @return The new TermApplication wrapping the values with the attachments from the old term
     */
    public ITermApplication replaceAppl(ITerm old, String cons, List<ITerm> children, List<ITerm> annotations) {
        Consumer<String> stringObserver = stringObserver();
        if(stringObserver != null) {
            stringObserver.accept(cons);
        }
        return TermApplication.of(cons, children, annotations, old.attachments());
    }

    /**
     * Build a new TermInt, without attachments. In a term transformation context {@link #replaceInt(ITerm, int, List)} is recommended
     *
     * @param value       The int value to wrap
     * @param annotations The annotations for the term
     * @return The new TermInt wrapping the value
     */
    public ITermInt newInt(int value, List<ITerm> annotations) {
        return TermInt.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new TermFloat, without attachments. In a term transformation context {@link #replaceFloat(ITerm, float, List)} is recommended
     *
     * @param value       The float value to wrap
     * @param annotations The annotations for the term
     * @return The new TermFloat wrapping the value
     */
    public ITermFloat newFloat(float value, List<ITerm> annotations) {
        return TermFloat.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new TermString, without attachments. In a term transformation context {@link #replaceString(ITerm, String, List)}  is recommended
     *
     * @param value       The String value to wrap
     * @param annotations The annotations for the term
     * @return The new TermString wrapping the value
     */
    public ITermString newString(String value, List<ITerm> annotations) {
        Consumer<String> stringObserver = stringObserver();
        if(stringObserver != null) {
            stringObserver.accept(value);
        }
        return TermString.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new TermList, without attachments or annotations. In a term transformation context {@link #replaceList(ITerm, List)} is recommended
     *
     * @param values The list values to wrap
     * @return The new TermList wrapping the value
     */
    public ITermList newList(List<ITerm> values) {
        return this.newList(values, Collections.emptyList());
    }

    /**
     * Build a new TermList, without attachments. In a term transformation context {@link #replaceList(ITerm, List, List)} is recommended
     *
     * @param value       The list values to wrap
     * @param annotations The annotations for the term
     * @return The new TermList wrapping the value
     */
    public ITermList newList(List<ITerm> value, List<ITerm> annotations) {
        return TermList.of(value, Collections.singletonList(annotations), Collections.singletonList(NO_ATTACHMENTS));
    }

    /**
     * Build a new TermSet, without attachments. In a term transformation context {@link #replaceSet(ITerm, Set.Immutable, List)} is recommended
     *
     * @param value       The set values to wrap
     * @param annotations The annotations for the term
     * @return The new TermSet wrapping the value
     */
    public ITermSet newSet(Set.Immutable<ITerm> value, List<ITerm> annotations) {
        return TermSet.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new TermMap, without attachments. In a term transformation context {@link #replaceMap(ITerm, Map.Immutable, List)} is recommended
     *
     * @param value       The map values to wrap
     * @param annotations The annotations for the term
     * @return The new TermMap wrapping the valueCollections.singletonList
     */
    public ITermMap newMap(Map.Immutable<ITerm, ITerm> value, List<ITerm> annotations) {
        return TermMap.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new TermApplication, without attachments. In a term transformation context {@link #replaceAppl(ITerm, String, List)} is recommended
     *
     * @param cons     The constructor of the application
     * @param children The child terms of the application
     * @return The new TermApplication wrapping the value
     */
    public ITermApplication newAppl(String cons, List<ITerm> children) {
        return this.newAppl(cons, children, Collections.emptyList());
    }

    /**
     * Build a new TermApplication, without attachments. In a term transformation context {@link #replaceAppl(ITerm, String, List, List)}  is recommended
     *
     * @param cons        The constructor of the application
     * @param children    The child terms of the application
     * @param annotations The annotations for the term
     * @return The new TermApplication wrapping the value
     */
    public ITermApplication newAppl(String cons, List<ITerm> children, List<ITerm> annotations) {
        Consumer<String> stringObserver = stringObserver();
        if(stringObserver != null) {
            stringObserver.accept(cons);
        }
        return TermApplication.of(cons, children, annotations, NO_ATTACHMENTS);
    }
}
