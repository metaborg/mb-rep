package mb.terms;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Delegates everything to the given {@link AbstractTermFactory}.
 */
public abstract class AbstractWrappedTermFactory extends AbstractTermFactory {
    private final AbstractTermFactory factory;

    public AbstractWrappedTermFactory(AbstractTermFactory factory) {
        this.factory = factory;
    }

    @Override
    @Nullable
    public Consumer<String> stringObserver() {
        return factory.stringObserver();
    }

    @Override
    public ITermInt replaceInt(ITerm old, int value) {
        return factory.replaceInt(old, value);
    }

    @Override
    public ITermInt replaceInt(ITerm old, int value, List<ITerm> annotations) {
        return factory.replaceInt(old, value, annotations);
    }

    @Override
    public ITermFloat replaceFloat(ITerm old, float value) {
        return factory.replaceFloat(old, value);
    }

    @Override
    public ITermFloat replaceFloat(ITerm old, float value, List<ITerm> annotations) {
        return factory.replaceFloat(old, value, annotations);
    }

    @Override
    public ITermString replaceString(ITerm old, String value) {
        return factory.replaceString(old, value);
    }

    @Override
    public ITermString replaceString(ITerm old, String value, List<ITerm> annotations) {
        return factory.replaceString(old, value, annotations);
    }

    @Override
    public ITermList replaceList(ITerm old, List<ITerm> values) {
        return factory.replaceList(old, values);
    }

    @Override
    public ITermList replaceList(ITerm old, List<ITerm> value, List<ITerm> annotations) {
        return factory.replaceList(old, value, annotations);
    }

    @Override
    public ITermSet replaceSet(ITerm old, Set.Immutable<ITerm> value) {
        return factory.replaceSet(old, value);
    }

    @Override
    public ITermSet replaceSet(ITerm old, Set.Immutable<ITerm> value, List<ITerm> annotations) {
        return factory.replaceSet(old, value, annotations);
    }

    @Override
    public ITermMap replaceMap(ITerm old, Map.Immutable<ITerm, ITerm> value) {
        return factory.replaceMap(old, value);
    }

    @Override
    public ITermMap replaceMap(ITerm old, Map.Immutable<ITerm, ITerm> value, List<ITerm> annotations) {
        return factory.replaceMap(old, value, annotations);
    }

    @Override
    public ITermApplication replaceAppl(ITerm old, String cons, List<ITerm> children) {
        return factory.replaceAppl(old, cons, children);
    }

    @Override
    public ITermApplication replaceAppl(ITerm old, String cons, List<ITerm> children, List<ITerm> annotations) {
        return factory.replaceAppl(old, cons, children, annotations);
    }

    @Override
    public ITermInt newInt(int value, List<ITerm> annotations) {
        return factory.newInt(value, annotations);
    }

    @Override
    public ITermFloat newFloat(float value, List<ITerm> annotations) {
        return factory.newFloat(value, annotations);
    }

    @Override
    public ITermString newString(String value, List<ITerm> annotations) {
        return factory.newString(value, annotations);
    }

    @Override
    public ITermList newList(List<ITerm> values) {
        return factory.newList(values);
    }

    @Override
    public ITermList newList(List<ITerm> value, List<ITerm> annotations) {
        return factory.newList(value, annotations);
    }

    @Override
    public ITermSet newSet(Set.Immutable<ITerm> value, List<ITerm> annotations) {
        return factory.newSet(value, annotations);
    }

    @Override
    public ITermMap newMap(Map.Immutable<ITerm, ITerm> value, List<ITerm> annotations) {
        return factory.newMap(value, annotations);
    }

    @Override
    public ITermApplication newAppl(String cons, List<ITerm> children) {
        return factory.newAppl(cons, children);
    }

    @Override
    public ITermApplication newAppl(String cons, List<ITerm> children, List<ITerm> annotations) {
        return factory.newAppl(cons, children, annotations);
    }
}
