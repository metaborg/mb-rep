package org.metaborg.scopegraph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.metaborg.regexp.FiniteAlphabet;
import org.metaborg.regexp.IAlphabet;
import org.metaborg.regexp.IRegExp;
import org.metaborg.regexp.IRegExpBuilder;
import org.metaborg.regexp.RegExpBuilder;
import org.metaborg.scopegraph.resolution.BackwardResolvingScopeGraph;
import org.metaborg.scopegraph.wf.IWFDeclPath;
import org.metaborg.scopegraph.wf.IWFFullPath;

import com.google.common.collect.ImmutableSet;

public class BackwardResolvingScopeGraphTest {

    private ILabel P;
    private ILabel I;
    private BackwardResolvingScopeGraph g;

    @Before public void setUp() {
        P = TestLabel.of("P");
        I = TestLabel.of("I");
        IAlphabet<ILabel> alphabet = new FiniteAlphabet<>(ImmutableSet.of(P, I));
        IRegExpBuilder<ILabel> b = new RegExpBuilder<>(alphabet);
        IRegExp<ILabel> wf = b.concat(b.closure(b.symbol(P)), b.closure(b.symbol(I)));
        g = new BackwardResolvingScopeGraph(wf);
    }

    @Test public void testAddDecl() {
        final IScope s1 = TestScope.of(1);
        final IOccurrence d1 = TestOccurrence.of("x", 1);
        g = g.addDecl(d1, s1);
        assertTrue(inEnv(g.reachables(s1), d1));
    }

    @Test public void testAddRef() {
        final IScope s1 = TestScope.of(1);
        g = g.addRef(TestOccurrence.of("x", 1), s1);
    }

    @Test public void testAddLink() {
        final IScope s1 = TestScope.of(1);
        final IScope s2 = TestScope.of(2);
        g = g.addLink(s1, P, s2);
    }

    @Test public void testAddCycle() {
        final IOccurrence r1 = TestOccurrence.of("x", 1);
        final IOccurrence d1 = TestOccurrence.of("x", 2);
        final IScope s1 = TestScope.of(1);
        final IOccurrence r2 = TestOccurrence.of("y", 3);
        final IOccurrence d2 = TestOccurrence.of("y", 4);
        final IScope s2 = TestScope.of(2);
        g = g.addRef(r1, s1);
        g = g.addDecl(d2, s1);
        g = g.addRef(r2, s2);
        g = g.addDecl(d1, s2);
        g = g.addLink(s1, P, s2);
        g = g.addLink(s2, P, s1);
        assertTrue(inRes(g.reachables(r1), d1));
        assertTrue(inRes(g.reachables(r2), d2));
    }

    @Test public void testDirectResolution() {
        final IScope s1 = TestScope.of(1);
        final IOccurrence r1 = TestOccurrence.of("x", 1);
        final IOccurrence d1 = TestOccurrence.of("x", 2);
        BackwardResolvingScopeGraph g1 = g.addDecl(d1, s1).addRef(r1, s1);
        assertTrue(inRes(g1.reachables(r1), d1));
        BackwardResolvingScopeGraph g2 = g.addRef(r1, s1).addDecl(d1, s1);
        assertTrue(inRes(g2.reachables(r1), d1));
    }

    @Test public void testParentResolution() {
        final IScope s1 = TestScope.of(1);
        final IScope s2 = TestScope.of(2);
        final IOccurrence r1 = TestOccurrence.of("x", 1);
        final IOccurrence d1 = TestOccurrence.of("x", 2);
        g = g.addDecl(d1, s1).addLink(s2, P, s1).addRef(r1, s2);
        assertTrue(inRes(g.reachables(r1), d1));
    }

    @Test public void testImportResolution() {
        final IScope s1 = TestScope.of(1);
        final IOccurrence r1 = TestOccurrence.of("x", 1);
        final IOccurrence d1 = TestOccurrence.of("x", 2);

        final IOccurrence r2 = TestOccurrence.of("y", 3);
        final IScope s2 = TestScope.of(2);
        final IScope s3 = TestScope.of(3);
        final IOccurrence d2 = TestOccurrence.of("y", 4);

        g = g.addRef(r1, s1);
        g = g.addDecl(d1, s1);

        g = g.addRef(r2, s2);
        g = g.addImport(r1, I, s2);

        g = g.addExport(d1, I, s3);
        g = g.addDecl(d2, s3);

        assertTrue(inRes(g.reachables(r2), d2));
    }

    @Test public void testNoParentAfterImport() {
        final IScope s1 = TestScope.of(1);
        final IOccurrence r1 = TestOccurrence.of("x", 1);
        final IOccurrence d1 = TestOccurrence.of("x", 2);

        final IOccurrence r2 = TestOccurrence.of("y", 3);
        final IScope s2 = TestScope.of(2);
        final IScope s3 = TestScope.of(3);
        final IOccurrence d2 = TestOccurrence.of("y", 4);

        final IScope s4 = TestScope.of(4);

        g = g.addRef(r1, s1);
        g = g.addDecl(d1, s1);

        g = g.addRef(r2, s2);
        g = g.addImport(r1, I, s2);

        g = g.addExport(d1, I, s3);
        g = g.addLink(s3, P, s4);
        g = g.addDecl(d2, s4);

        assertTrue(inRes(g.reachables(r1), d1));
        assertFalse(inRes(g.reachables(r2), d2));
    }

    private static boolean inEnv(Iterable<IWFDeclPath> paths, IOccurrence declaration) {
        for (IWFDeclPath path : paths) {
            if (path.path().declaration().equals(declaration)) {
                return true;
            }
        }
        return false;
    }

    private static boolean inRes(Iterable<IWFFullPath> paths, IOccurrence declaration) {
        for (IWFFullPath path : paths) {
            if (path.path().declaration().equals(declaration)) {
                return true;
            }
        }
        return false;
    }

}