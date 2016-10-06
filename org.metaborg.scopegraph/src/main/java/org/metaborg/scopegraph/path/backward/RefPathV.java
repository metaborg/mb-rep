package org.metaborg.scopegraph.path.backward;

import java.util.Iterator;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.IDeclPath;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IRefStep;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.OccurrenceMismatchPathException;
import org.metaborg.scopegraph.path.PathException;
import org.metaborg.scopegraph.path.RecursivePathException;
import org.metaborg.util.iterators.Iterators2;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class RefPathV implements IRefStep, IFullPath {

    @Value.Check public void check() {
        if (!reference().matches(tail().declaration())) {
            throw new OccurrenceMismatchPathException();
        }
        if (tail().references().contains(reference())) {
            throw new RecursivePathException();
        }
    }

    @Override public abstract IOccurrence reference();

    protected abstract IDeclPath tail();

    @Value.Lazy @Override public IScope targetScope() {
        return tail().sourceScope();
    }

    @Value.Lazy @Override public int size() {
        return 1 + tail().size();
    }

    @Value.Lazy @Override public PSet<IScope> scopes() {
        return tail().scopes();
    }

    @Value.Lazy @Override public PSet<IOccurrence> references() {
        return HashTreePSet.singleton(reference());
    }

    @Value.Lazy @Override public IOccurrence declaration() {
        return tail().declaration();
    }

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit((IFullPath) this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators2.cons(this, tail().iterator());
    }

    @Override public String toString() {
        return "R(" + reference() + ") . " + tail();
    }

}