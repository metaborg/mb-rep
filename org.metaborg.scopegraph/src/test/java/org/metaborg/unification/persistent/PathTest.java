package org.metaborg.unification.persistent;

import static org.metaborg.scopegraph.path.free.PathConcat.concat;

import org.junit.Test;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.CyclicPathException;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.scopegraph.path.OccurrenceMismatchPathException;
import org.metaborg.scopegraph.path.PathException;
import org.metaborg.scopegraph.path.RecursivePathException;
import org.metaborg.scopegraph.path.ScopeMismatchPathException;
import org.metaborg.scopegraph.path.free.DeclStep;
import org.metaborg.scopegraph.path.free.DirectStep;
import org.metaborg.scopegraph.path.free.NamedStep;
import org.metaborg.scopegraph.path.free.RefStep;

public class PathTest {

    @Test public void testCorrectConcats() throws PathException {
        Occurrence x = new Occurrence(1);
        Scope s1 = new Scope();
        Scope s2 = new Scope();
        RefStep r = new RefStep(x, s1);
        DirectStep ss = new DirectStep(s1, null, s2);
        DeclStep d = new DeclStep(s2, x);
        concat(concat(r, ss), d);
        concat(r, concat(ss, d));
    }

    @Test(expected = ScopeMismatchPathException.class) public void testScopeMismatch() throws PathException {
        Occurrence x = new Occurrence(1);
        Scope s1 = new Scope();
        Scope s2 = new Scope();
        RefStep r = new RefStep(x, s1);
        DeclStep d = new DeclStep(s2, x);
        concat(r, d);
    }

    @Test(expected = OccurrenceMismatchPathException.class) public void testOccurrenceMismatch() throws PathException {
        Occurrence x = new Occurrence(1);
        Occurrence y = new Occurrence(2);
        Scope s = new Scope();
        RefStep r = new RefStep(x, s);
        DeclStep d = new DeclStep(s, y);
        concat(r, d);
    }

    @Test(expected = CyclicPathException.class) public void testCyclic() throws PathException {
        Scope s1 = new Scope();
        Scope s2 = new Scope();
        DirectStep ss1 = new DirectStep(s1, null, s2);
        DirectStep ss2 = new DirectStep(s2, null, s1);
        concat(ss1, ss2);
    }

    @Test(expected = RecursivePathException.class) public void testRecursive() throws PathException {
        Occurrence x = new Occurrence(1);
        Scope is = new Scope();
        RefStep ir = new RefStep(x, is);
        DeclStep id = new DeclStep(is, x);
        IFullPath ip = concat(ir, id);

        Scope s1 = new Scope();
        Scope s2 = new Scope();
        RefStep r = new RefStep(x, s1);
        NamedStep n = new NamedStep(s1, null, ip, s2);

        concat(r, n);
    }

    @SuppressWarnings("serial")
    private class Occurrence implements IOccurrence {

        private final int id;

        public Occurrence(int id) {
            this.id = id;
        }

        @Override public boolean matches(IOccurrence other) {
            return id == ((Occurrence) other).id;
        }

    }

    @SuppressWarnings("serial")
    private class Scope implements IScope {
    }

}