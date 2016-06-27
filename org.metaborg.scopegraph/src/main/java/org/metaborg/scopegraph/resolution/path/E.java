package org.metaborg.scopegraph.resolution.path;

import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Scope;

public class E implements Path {

    public final Scope scope;
    public final Label label;
    public final Path tail;

    public E(Scope scope, Label label, Path tail) {
        this.scope = scope;
        this.label = label;
        this.tail = tail;
    }

    @Override public <T> T visit(PathVisitor<T> visitor) {
        return visitor.visit(this);
    } 

    @Override
    public String toString() {
        return String.format("E(%s,%s).%s", scope, label, tail);
    }

}
