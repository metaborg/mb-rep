package org.metaborg.scopegraph.path.backward;

import java.util.Iterator;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.CyclicPathException;
import org.metaborg.scopegraph.path.IDeclPath;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.scopegraph.path.INamedStep;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.PathException;
import org.metaborg.util.iterators.Iterators2;
import org.pcollections.PSet;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class NamedPathV implements INamedStep, IDeclPath {

    @Value.Check public void check() {
        if (tail().scopes().contains(sourceScope())) {
            throw new CyclicPathException();
        }
    }

    @Override public abstract IScope sourceScope();

    @Override public abstract ILabel label();

    @Override public abstract IFullPath path();

    protected abstract IDeclPath tail();

    @Value.Lazy @Override public IScope targetScope() {
        return tail().sourceScope();
    }

    @Value.Lazy @Override public int size() {
        return 1 + tail().size();
    }

    @Value.Lazy @Override public PSet<IScope> scopes() {
        return tail().scopes().plus(sourceScope());
    }

    @Value.Lazy @Override public PSet<IOccurrence> references() {
        return tail().references().plusAll(path().references());
    }

    @Value.Lazy @Override public IOccurrence declaration() {
        return tail().declaration();
    }

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit((IDeclPath) this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators2.cons(this, tail().iterator());
    }

    @Override public String toString() {
        return sourceScope() + " . N(" + label() + ", " + path() + ") . " + tail();
    }

}
