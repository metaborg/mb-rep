package org.metaborg.scopegraph.resolution.path;

import java.util.List;
import java.util.NoSuchElementException;

import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Labels;
import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.Scope;
import org.metaborg.scopegraph.resolution.path.Path.PathVisitor;

import com.google.common.collect.Lists;

public class Paths {

    public static Occurrence getDeclaration(final Path path) {
        return path.visit(new PathVisitor<Occurrence>() {
            @Override public Occurrence visit(R r) {
                return r.getTail().visit(this);
            }
            @Override public Occurrence visit(E e) {
                return e.getTail().visit(this);
            }
            @Override public Occurrence visit(N n) {
                return n.getTail().visit(this);
            }
            @Override public Occurrence visit(D d) {
                return d.getDeclaration();
            }
        });
    }
 
    public static Occurrence getReference(final Path path) {
        return path.visit(new PathVisitor<Occurrence>() {
            @Override public Occurrence visit(R r) {
                return r.getReference();
            }
            @Override public Occurrence visit(E e) {
                throw new NoSuchElementException();
            }
            @Override public Occurrence visit(N n) {
                throw new NoSuchElementException();
            }
            @Override public Occurrence visit(D d) {
                throw new NoSuchElementException();
            }
        });
    }
 
    public static Scope getScope(final Path path) {
        return path.visit(new PathVisitor<Scope>() {
            @Override public Scope visit(R r) {
                return r.getTail().visit(this);
            }
            @Override public Scope visit(E e) {
                return e.getScope();
            }
            @Override public Scope visit(N n) {
                return n.getScope();
            }
            @Override public Scope visit(D d) {
                return d.getScope();
            }
        });
    }
 
    public static boolean visits(final Path path, final Scope scope) {
        return path.visit(new PathVisitor<Boolean>() {
            @Override public Boolean visit(R r) {
                return r.getTail().visit(this);
            }
            @Override public Boolean visit(E e) {
                return scope == e.getScope() || e.getTail().visit(this);
            }
            @Override public Boolean visit(N n) {
                return scope == n.getScope() || n.getTail().visit(this);
            }
            @Override public Boolean visit(D d) {
                return scope == d.getScope();
            }
        });
    }
 
    public static boolean imports(final Path path, final Occurrence reference) {
        return path.visit(new PathVisitor<Boolean>() {
            @Override public Boolean visit(R r) {
                return reference == r.getReference() || r.getTail().visit(this);
            }
            @Override public Boolean visit(E e) {
                return e.getTail().visit(this);
            }
            @Override public Boolean visit(N n) {
                return n.getInner().visit(this) || n.getTail().visit(this);
            }
            @Override public Boolean visit(D d) {
                return false;
            }
        });
    }
 
    public static List<Label> labels(final Path path) {
        final List<Label> labels = Lists.newArrayList();
        path.visit(new PathVisitor<Void>() {
            @Override public Void visit(R r) {
                labels.add(Labels.R);
                return r.getTail().visit(this);
            }
            @Override public Void visit(E e) {
                labels.add(e.getLabel());
                return e.getTail().visit(this);
            }
            @Override public Void visit(N n) {
                labels.add(n.getLabel());
                return n.getTail().visit(this);
            }
            @Override public Void visit(D d) {
                labels.add(Labels.D);
                return null;
            }
        });
        return labels;
    }
 
    public static Label label(final Path path) {
        return path.visit(new PathVisitor<Label>() {
            @Override public Label visit(R r) {
                return Labels.R;
            }
            @Override public Label visit(E e) {
                return e.getLabel();
            }
            @Override public Label visit(N n) {
                return n.getLabel();
            }
            @Override public Label visit(D d) {
                return Labels.D;
            }
        });
    }
 
}
