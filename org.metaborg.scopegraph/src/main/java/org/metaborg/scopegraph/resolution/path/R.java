package org.metaborg.scopegraph.resolution.path;

import org.metaborg.scopegraph.Occurrence;

import lombok.Value;

@Value
public class R implements Path {

    Occurrence reference;
    Path tail;

    public R(Occurrence reference, Path tail) {
        assert !(tail instanceof R);
        this.reference = reference;
        this.tail = tail;
    }

    @Override public <T> T visit(PathVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("R(%s)%s", reference, tail);
    }

}
