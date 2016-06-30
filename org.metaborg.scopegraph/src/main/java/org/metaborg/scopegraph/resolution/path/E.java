package org.metaborg.scopegraph.resolution.path;

import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Scope;

import lombok.Value;

@Value
public class E implements Path {

    Scope scope;
    Label label;
    Path tail;

    public E(Scope scope, Label label, Path tail) {
        assert !(tail instanceof R);
        this.scope = scope;
        this.label = label;
        this.tail = tail;
    }

    @Override public <T> T visit(PathVisitor<T> visitor) {
        return visitor.visit(this);
    } 

    @Override
    public String toString() {
        return String.format("E(%s,%s)%s", scope, label, tail);
    }

}
