package org.metaborg.scopegraph.resolution.path;

import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Scope;

public class N implements Path {

    public final Scope scope;
    public final Label label;
    public final R inner;
    public final Path tail;

    public N(Scope scope, Label label, R inner, Path tail) {
        this.scope = scope;
        this.label = label;
        this.inner = inner;
        this.tail = tail;
    }

    @Override
    public <T> T visit(PathVisitor<T> visitor) {
        return visitor.visit(this);
    } 

    @Override
    public String toString() {
        return String.format("N(%s,%s,%s).%s", scope, label, inner, tail);
    }

}