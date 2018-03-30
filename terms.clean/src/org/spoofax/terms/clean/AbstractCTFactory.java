package org.spoofax.terms.clean;

import com.google.common.collect.ImmutableClassToInstanceMap;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A factory for {@link ICleanTerm}s.
 * It has an observer for every String coming through {@link #stringObserver()}.
 * When possible, use the {@code replace*} methods which copy the attachments and annotations of an old term. There are also versions that allow you to provide your own annotations. The {@code new*} methods set no attachments. If you wish to set your own attachments use {@link ICleanTerm#withAttachments(ImmutableClassToInstanceMap)}.
 */
@SuppressWarnings({"WeakerAccess", "unused", "JavaDoc"})
@Value.Immutable
abstract class AbstractCTFactory {
    public static final ImmutableClassToInstanceMap<ICleanTermAttachment> NO_ATTACHMENTS = ImmutableClassToInstanceMap.<ICleanTermAttachment>builder().build();

    /**
     * This observer is called with every String given to the factory.
     * For example in {@link #newString(String, List<ICleanTerm>)}, but also in {@link #newAppl(String, List<ICleanTerm>)} and {@code replace*} variants.
     * This can be used to keep track of Strings that are in use so unique strings can be generated.
     *
     * @return The observer
     */
    protected abstract @Nullable
    Consumer<String> stringObserver();

    /**
     * Build a CTInt, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The int value to wrap
     * @return The new CTInt wrapping the value with the annotations/attachments from the old term
     */
    public CTInt replaceInt(ICleanTerm old, int value) {
        return CTInt.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a CTInt, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The int value to wrap
     * @param annotations The annotation for the term
     * @return The new CTInt wrapping the value with the attachments from the old term
     */
    public CTInt replaceInt(ICleanTerm old, int value, List<ICleanTerm> annotations) {
        return CTInt.of(value, annotations, old.attachments());
    }

    /**
     * Build a CTFloat, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The float value to wrap
     * @return The new CTFloat wrapping the value with the annotations/attachments from the old term
     */
    public CTFloat replaceFloat(ICleanTerm old, float value) {
        return CTFloat.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a CTFloat, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The int value to wrap
     * @param annotations The annotation for the term
     * @return The new CTFloat wrapping the value with the attachments from the old term
     */
    public CTFloat replaceFloat(ICleanTerm old, float value, List<ICleanTerm> annotations) {
        return CTFloat.of(value, annotations, old.attachments());
    }

    /**
     * Build a CTString, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The String value to wrap
     * @return The new CTString wrapping the value with the annotations/attachments from the old term
     */
    public CTString replaceString(ICleanTerm old, String value) {
        Objects.requireNonNull(stringObserver()).accept(value);
        return CTString.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a CTString, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The String value to wrap
     * @param annotations The annotation for the term
     * @return The new CTString wrapping the value with the attachments from the old term
     */
    public CTString replaceString(ICleanTerm old, String value, List<ICleanTerm> annotations) {
        Objects.requireNonNull(stringObserver()).accept(value);
        return CTString.of(value, annotations, old.attachments());
    }

    /**
     * Build a CTList, using the annotations and attachments from the old term.
     *
     * @param old    The old term from which the annotations and attachments are copied.
     * @param values The term values to wrap
     * @return The new CTList wrapping the values with the annotations/attachments from the old term
     */
    public CTList replaceList(ICleanTerm old, List<ICleanTerm> values) {
        return this.replaceList(old, values, old.annotations());
    }

    /**
     * Build a CTList, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The term values to wrap
     * @param annotations The annotation for the term
     * @return The new CTList wrapping the values with the attachments from the old term
     */
    public CTList replaceList(ICleanTerm old, List<ICleanTerm> value, List<ICleanTerm> annotations) {
        return CTList.of(value, Collections.singletonList(annotations), Collections.singletonList(old.attachments()));
    }

    /**
     * Build a CTSet, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The set of term values to wrap
     * @return The new CTSet wrapping the values with the annotations/attachments from the old term
     */
    public CTSet replaceSet(ICleanTerm old, Set.Immutable<ICleanTerm> value) {
        return CTSet.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a CTSet, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The set of term values to wrap
     * @param annotations The annotation for the term
     * @return The new CTSet wrapping the values with the attachments from the old term
     */
    public CTSet replaceSet(ICleanTerm old, Set.Immutable<ICleanTerm> value, List<ICleanTerm> annotations) {
        return CTSet.of(value, annotations, old.attachments());
    }

    /**
     * Build a CTMap, using the annotations and attachments from the old term.
     *
     * @param old   The old term from which the annotations and attachments are copied.
     * @param value The map of term values to wrap
     * @return The new CTMap wrapping the values with the annotations/attachments from the old term
     */
    public CTMap replaceMap(ICleanTerm old, Map.Immutable<ICleanTerm, ICleanTerm> value) {
        return CTMap.of(value, old.annotations(), old.attachments());
    }

    /**
     * Build a CTMap, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param value       The map of term values to wrap
     * @param annotations The annotation for the term
     * @return The new CTMap wrapping the values with the attachments from the old term
     */
    public CTMap replaceMap(ICleanTerm old, Map.Immutable<ICleanTerm, ICleanTerm> value, List<ICleanTerm> annotations) {
        return CTMap.of(value, annotations, old.attachments());
    }

    /**
     * Build a CTApplication, using the annotations and attachments from the old term.
     *
     * @param old      The old term from which the annotations and attachments are copied.
     * @param cons     The constructor of the application
     * @param children The child terms of the application
     * @return The new CTApplication wrapping the values with the annotations/attachments from the old term
     */
    public CTApplication replaceAppl(ICleanTerm old, String cons, List<ICleanTerm> children) {
        return this.replaceAppl(old, cons, children, old.annotations());
    }

    /**
     * Build a CTApplication, using the attachments from the old term.
     *
     * @param old         The old term from which the attachments are copied.
     * @param cons        The constructor of the application
     * @param children    The child terms of the application
     * @param annotations The annotation for the term
     * @return The new CTApplication wrapping the values with the attachments from the old term
     */
    public CTApplication replaceAppl(ICleanTerm old, String cons, List<ICleanTerm> children, List<ICleanTerm> annotations) {
        Objects.requireNonNull(stringObserver()).accept(cons);
        return CTApplication.of(cons, children, annotations, old.attachments());
    }

    /**
     * Build a new CTInt, without attachments. In a term transformation context {@link #replaceInt(ICleanTerm, int, List<ICleanTerm>)} is recommended
     *
     * @param value       The int value to wrap
     * @param annotations The annotations for the term
     * @return The new CTInt wrapping the value
     */
    public CTInt newInt(int value, List<ICleanTerm> annotations) {
        return CTInt.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new CTFloat, without attachments. In a term transformation context {@link #replaceFloat(ICleanTerm, float, List<ICleanTerm>)} is recommended
     *
     * @param value       The float value to wrap
     * @param annotations The annotations for the term
     * @return The new CTFloat wrapping the value
     */
    public CTFloat newFloat(float value, List<ICleanTerm> annotations) {
        return CTFloat.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new CTString, without attachments. In a term transformation context {@link #replaceString(ICleanTerm, String, List<ICleanTerm>)}  is recommended
     *
     * @param value       The String value to wrap
     * @param annotations The annotations for the term
     * @return The new CTString wrapping the value
     */
    public CTString newString(String value, List<ICleanTerm> annotations) {
        Objects.requireNonNull(stringObserver()).accept(value);
        return CTString.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new CTList, without attachments or annotations. In a term transformation context {@link #replaceList(ICleanTerm, List<ICleanTerm>)} is recommended
     *
     * @param values The list values to wrap
     * @return The new CTList wrapping the value
     */
    public CTList newList(List<ICleanTerm> values) {
        return this.newList(values, Collections.emptyList());
    }

    /**
     * Build a new CTList, without attachments. In a term transformation context {@link #replaceList(ICleanTerm, List<ICleanTerm>, List<ICleanTerm>)} is recommended
     *
     * @param value       The list values to wrap
     * @param annotations The annotations for the term
     * @return The new CTList wrapping the value
     */
    public CTList newList(List<ICleanTerm> value, List<ICleanTerm> annotations) {
        return CTList.of(value, Collections.singletonList(annotations), Collections.singletonList(NO_ATTACHMENTS));
    }

    /**
     * Build a new CTSet, without attachments. In a term transformation context {@link #replaceSet(ICleanTerm, Set.Immutable, List<ICleanTerm>)} is recommended
     *
     * @param value       The set values to wrap
     * @param annotations The annotations for the term
     * @return The new CTSet wrapping the value
     */
    public CTSet newSet(Set.Immutable<ICleanTerm> value, List<ICleanTerm> annotations) {
        return CTSet.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new CTMap, without attachments. In a term transformation context {@link #replaceMap(ICleanTerm, Map.Immutable, List<ICleanTerm>)} is recommended
     *
     * @param value       The map values to wrap
     * @param annotations The annotations for the term
     * @return The new CTMap wrapping the valueCollections.singletonList
     */
    public CTMap newMap(Map.Immutable<ICleanTerm, ICleanTerm> value, List<ICleanTerm> annotations) {
        return CTMap.of(value, annotations, NO_ATTACHMENTS);
    }

    /**
     * Build a new CTApplication, without attachments. In a term transformation context {@link #replaceAppl(ICleanTerm, String, List<ICleanTerm>)} is recommended
     *
     * @param cons     The constructor of the application
     * @param children The child terms of the application
     * @return The new CTApplication wrapping the value
     */
    public CTApplication newAppl(String cons, List<ICleanTerm> children) {
        return this.newAppl(cons, children, Collections.emptyList());
    }

    /**
     * Build a new CTApplication, without attachments. In a term transformation context {@link #replaceAppl(ICleanTerm, String, List<ICleanTerm>, List<ICleanTerm>)}  is recommended
     *
     * @param cons        The constructor of the application
     * @param children    The child terms of the application
     * @param annotations The annotations for the term
     * @return The new CTApplication wrapping the value
     */
    public CTApplication newAppl(String cons, List<ICleanTerm> children, List<ICleanTerm> annotations) {
        Objects.requireNonNull(stringObserver()).accept(cons);
        return CTApplication.of(cons, children, annotations, NO_ATTACHMENTS);
    }
}
