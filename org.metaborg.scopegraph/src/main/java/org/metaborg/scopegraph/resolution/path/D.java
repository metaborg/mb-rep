package org.metaborg.scopegraph.resolution.path;

import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.Scope;

public class D implements Path {

    public final Occurrence declaration;
    public final Scope scope;

    public D(Occurrence declaration, Scope scope) {
        this.declaration = declaration;
        this.scope = scope;
    }

    @Override public <T> T visit(PathVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("D(%s,%s)", scope, declaration);
    }

}