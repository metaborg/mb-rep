package org.metaborg.unification.persistent;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.IDeclPath;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.scopegraph.resolution.BackwardResolvingScopeGraph;

public class BackwardResolvingScopeGraphTest {

    @Test public void testAddDecl() {
        BackwardResolvingScopeGraph g = new BackwardResolvingScopeGraph();
        final IScope s1 = new Scope();
        final IOccurrence d1 = new Occurrence(1);
        g = g.addDecl(d1, s1);
        assertInEnv(g.reachables(s1), d1);
    }

    @Test public void testAddRef() {
        BackwardResolvingScopeGraph g = new BackwardResolvingScopeGraph();
        final IScope s1 = new Scope();
        g = g.addRef(new Occurrence(1), s1);
    }

    @Test public void testAddLink() {
        BackwardResolvingScopeGraph g = new BackwardResolvingScopeGraph();
        final IScope s1 = new Scope();
        final IScope s2 = new Scope();
        g = g.addLink(s1, new Label(), s2);
    }

    @Test public void testAddCycle() {
        BackwardResolvingScopeGraph g = new BackwardResolvingScopeGraph();
        final IOccurrence r1 = new Occurrence(1);
        final IOccurrence d1 = new Occurrence(1);
        final IScope s1 = new Scope();
        final IOccurrence r2 = new Occurrence(2);
        final IOccurrence d2 = new Occurrence(2);
        final IScope s2 = new Scope();
        g = g.addRef(r1, s1);
        g = g.addDecl(d2, s1);
        g = g.addRef(r2, s2);
        g = g.addDecl(d1, s2);
        g = g.addLink(s1, new Label(), s2);
        g = g.addLink(s2, new Label(), s1);
        assertInRes(g.reachables(r1), d1);
        assertInRes(g.reachables(r2), d2);
    }

    @Test public void testDirectResolution() {
        BackwardResolvingScopeGraph g = new BackwardResolvingScopeGraph();
        final IScope s1 = new Scope();
        final IOccurrence r1 = new Occurrence(1);
        final IOccurrence d1 = new Occurrence(1);
        g = g.addDecl(d1, s1).addRef(r1, s1);
        assertInRes(g.reachables(r1), d1);
        g = g.addRef(r1, s1).addDecl(d1, s1);
        assertInRes(g.reachables(r1), d1);
    }

    @Test public void testImportResolution() {
        BackwardResolvingScopeGraph g = new BackwardResolvingScopeGraph();

        final IScope s1 = new Scope();
        final IOccurrence r1 = new Occurrence(1);
        final IOccurrence d1 = new Occurrence(1);

        final ILabel l = new Label();
        final IOccurrence r2 = new Occurrence(2);
        final IScope s2 = new Scope();
        final IScope s3 = new Scope();
        final IOccurrence d2 = new Occurrence(2);

        g = g.addRef(r1, s1);
        g = g.addDecl(d1, s1);

        g = g.addRef(r2, s2);
        g = g.addImport(r1, l, s2);

        g = g.addExport(d1, l, s3);
        g = g.addDecl(d2, s3);

        assertInRes(g.reachables(r2), d2);
    }

    private static void assertInEnv(Iterable<IDeclPath> paths, IOccurrence declaration) {
        for (IDeclPath path : paths) {
            if (path.declaration().equals(declaration)) {
                return;
            }
        }
        fail();
    }

    private static void assertInRes(Iterable<IFullPath> paths, IOccurrence declaration) {
        for (IFullPath path : paths) {
            if (path.declaration().equals(declaration)) {
                return;
            }
        }
        fail();
    }

    @SuppressWarnings("serial")
    private static class Occurrence implements IOccurrence {

        private final int id;

        public Occurrence(int id) {
            this.id = id;
        }

        @Override public boolean matches(IOccurrence other) {
            return id == ((Occurrence) other).id;
        }

        @Override public String toString() {
            return "Occurrence(" + id + ")@" + System.identityHashCode(this);
        }

    }

    @SuppressWarnings("serial")
    private static class Scope implements IScope {

        @Override public String toString() {
            return "Scope@" + System.identityHashCode(this);
        }

    }

    @SuppressWarnings("serial")
    private static class Label implements ILabel {

        @Override public String toString() {
            return "Label@" + System.identityHashCode(this);
        }

    }

}