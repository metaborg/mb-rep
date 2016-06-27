package org.metaborg.scopegraph.resolution.path;

import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.Scope;
import org.metaborg.scopegraph.resolution.path.Path.PathVisitor;

public class Paths {

    public static Occurrence getDeclaration(final Path path) {
        return path.visit(new PathVisitor<Occurrence>() {
            @Override public Occurrence visit(R r) {
                return r.tail.visit(this);
            }
            @Override public Occurrence visit(E e) {
                return e.tail.visit(this);
            }
            @Override public Occurrence visit(N n) {
                return n.tail.visit(this);
            }
            @Override public Occurrence visit(D d) {
                return d.declaration;
            }
        });
    }
    
    public static Scope getScope(final Path path) {
        return path.visit(new PathVisitor<Scope>() {
            @Override public Scope visit(R r) {
                return r.tail.visit(this);
            }
            @Override public Scope visit(E e) {
                return e.scope;
            }
            @Override public Scope visit(N n) {
                return n.scope;
            }
            @Override public Scope visit(D d) {
                return d.scope;
            }
        });
    }
    
    public static boolean visits(final Path path, final Scope scope) {
        return path.visit(new PathVisitor<Boolean>() {
            @Override public Boolean visit(R r) {
                return r.tail.visit(this);
            }
            @Override public Boolean visit(E e) {
                return scope == e.scope || e.tail.visit(this);
            }
            @Override public Boolean visit(N n) {
                return scope == n.scope || n.tail.visit(this);
            }
            @Override public Boolean visit(D d) {
                return scope == d.scope;
            }
        });
    }
    
    public static boolean imports(final Path path, final Occurrence reference) {
        return path.visit(new PathVisitor<Boolean>() {
            @Override public Boolean visit(R r) {
                return reference == r.reference || r.tail.visit(this);
            }
            @Override public Boolean visit(E e) {
                return e.tail.visit(this);
            }
            @Override public Boolean visit(N n) {
                return n.inner.visit(this) || n.tail.visit(this);
            }
            @Override public Boolean visit(D d) {
                return false;
            }
        });
    }
    
}
