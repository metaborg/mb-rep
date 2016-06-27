package org.metaborg.scopegraph.resolution.path;

import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.Scope;

public class R implements Path {

    public final Occurrence reference;
    public final Path tail;

    public R(Occurrence reference, Path tail) {
        this.reference = reference;
        this.tail = tail;
    }

    @Override public <T> T visit(PathVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("R(%s).%s", reference, tail);
    }

}
