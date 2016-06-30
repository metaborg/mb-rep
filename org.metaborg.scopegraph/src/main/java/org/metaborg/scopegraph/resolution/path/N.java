package org.metaborg.scopegraph.resolution.path;

import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Scope;

import lombok.Value;

@Value
public class N implements Path {

    Scope scope;
    Label label;
    Path inner;
    Path tail;

    public N(Scope scope, Label label, Path inner, Path tail) {
        assert inner instanceof R;
        assert !(tail instanceof R);
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
        return String.format("N(%s,%s,[ %s ])%s", scope, label, inner, tail);
    }

}