package org.metaborg.unification.persistent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.metaborg.unification.ITermUnifier;
import org.metaborg.unification.IUnifyResult;
import org.metaborg.unification.eager.EagerTermUnifier;
import org.metaborg.unification.lazy.LazyTermUnifier;
import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.IntTerm;
import org.metaborg.unification.terms.StringTerm;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermVar;

import com.google.common.collect.Iterables;

public class UnificationTests {

    @Test public void testLazy() {
        ITermUnifier unifier = new LazyTermUnifier();
        testInt(unifier);
        testString(unifier);
        testVar(unifier);
        testAppl(unifier);
        testOp(unifier);
    }

    @Test public void testEager() {
        ITermUnifier unifier = new EagerTermUnifier();
        testInt(unifier);
        testString(unifier);
        testVar(unifier);
        testAppl(unifier);
        testOp(unifier);
    }

    private void testInt(ITermUnifier unifier) {
        IUnifyResult result = unifier.unify(new IntTerm(1), new IntTerm(1));
        result = result.unifier().unify(new IntTerm(1), new IntTerm(42));
        assertEquals(1, Iterables.size(result.conflicts()));
    }

    private void testString(ITermUnifier unifier) {
        unifier.unify(new StringTerm("foo"), new StringTerm("foo"));
        IUnifyResult result = unifier.unify(new StringTerm("foo"), new StringTerm("bar"));
        assertEquals(1, Iterables.size(result.conflicts()));
    }

    private void testVar(ITermUnifier unifier) {
        IUnifyResult result0 = unifier.unify(new TermVar("a"), new TermVar("b"));
        IUnifyResult result = result0.unifier().unify(new TermVar("a"), new IntTerm(42));
        IUnifyResult result1 = result.unifier().unify(new TermVar("b"), new IntTerm(1));
        assertEquals(1, Iterables.size(result1.conflicts()));
        result = result.unifier().unify(new TermVar("b"), new IntTerm(42));
        result = result0.unifier().unify(new TermVar("b"), new IntTerm(1));
    }

    private void testAppl(ITermUnifier unifier) {
        IUnifyResult result = unifier.unify(new ApplTerm("f"), new ApplTerm("g"));
        assertEquals(1, Iterables.size(result.conflicts()));
        result = unifier.unify(new ApplTerm("f", new IntTerm(42)), new ApplTerm("f"));
        assertEquals(1, Iterables.size(result.conflicts()));
        unifier.unify(new ApplTerm("f", new TermVar("x"), new TermVar("x")),
                new ApplTerm("f", new TermVar("y"), new IntTerm(42)));
    }

    private void testOp(ITermUnifier unifier) {
        IUnifyResult result = unifier.unify(new ApplTerm("f"), new TermOp("lub"));
        assertNull(result);
    }

}