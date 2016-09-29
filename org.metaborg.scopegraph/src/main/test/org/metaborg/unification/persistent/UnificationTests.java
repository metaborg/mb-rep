package org.metaborg.unification.persistent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.IntTerm;
import org.metaborg.unification.terms.OpTerm;
import org.metaborg.unification.terms.StringTerm;
import org.metaborg.unification.terms.TermVar;

import com.google.common.collect.Iterables;

public class UnificationTests {

    @Test public void testInt() {
        PersistentTermUnifier unifier = new PersistentTermUnifier();
        UnifyResult result = unifier.unify(new IntTerm(1), new IntTerm(1));
        result = result.unifier.unify(new IntTerm(1), new IntTerm(42));
        assertEquals(1, Iterables.size(result.conflicts));
    }

    @Test public void testString() {
        PersistentTermUnifier unifier = new PersistentTermUnifier();
        unifier.unify(new StringTerm("foo"), new StringTerm("foo"));
        UnifyResult result = unifier.unify(new StringTerm("foo"), new StringTerm("bar"));
        assertEquals(1, Iterables.size(result.conflicts));
    }

    @Test public void testVar() {
        PersistentTermUnifier unifier = new PersistentTermUnifier();
        UnifyResult result0 = unifier.unify(new TermVar("a"), new TermVar("b"));
        UnifyResult result = result0.unifier.unify(new TermVar("a"), new IntTerm(42));
        UnifyResult result1 = result.unifier.unify(new TermVar("b"), new IntTerm(1));
        assertEquals(1, Iterables.size(result1.conflicts));
        result = result.unifier.unify(new TermVar("b"), new IntTerm(42));
        result = result0.unifier.unify(new TermVar("b"), new IntTerm(1));
    }

    @Test public void testAppl() {
        PersistentTermUnifier unifier = new PersistentTermUnifier();
        UnifyResult result = unifier.unify(new ApplTerm("f"), new ApplTerm("g"));
        assertEquals(1, Iterables.size(result.conflicts));
        result = unifier.unify(new ApplTerm("f", new IntTerm(42)), new ApplTerm("f"));
        assertEquals(1, Iterables.size(result.conflicts));
        unifier.unify(new ApplTerm("f", new TermVar("x"), new TermVar("x")),
                new ApplTerm("f", new TermVar("y"), new IntTerm(42)));
    }

    @Test public void testOp() {
        PersistentTermUnifier unifier = new PersistentTermUnifier();
        UnifyResult result = unifier.unify(new ApplTerm("f"), new OpTerm("lub"));
        assertNull(result);
    }

}