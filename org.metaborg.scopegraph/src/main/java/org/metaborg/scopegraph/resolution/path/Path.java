package org.metaborg.scopegraph.resolution.path;

public interface Path {

    <T> T visit(PathVisitor<T> visitor);
    
    public interface PathVisitor<T> {
        T visit(R r);
        T visit(E e);
        T visit(N n);
        T visit(D d);
    }

}
