package org.metaborg.scopegraph.path.backward;

import java.util.Iterator;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.IDeclStep;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.PathException;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import com.google.common.collect.Iterators;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class DeclPathV implements IDeclStep {

    @Override public abstract IScope sourceScope();

    @Override public abstract IOccurrence declaration();

    @Value.Lazy @Override public int size() {
        return 1;
    }

    @Value.Lazy @Override public PSet<IScope> scopes() {
        return HashTreePSet.singleton(sourceScope());
    }

    @Value.Lazy @Override public PSet<IOccurrence> references() {
        return HashTreePSet.empty();
    }

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit(this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators.<IStep> singletonIterator(this);
    }

    @Override public String toString() {
        return sourceScope() + " . D(" + declaration() + ")";
    }

}