package org.metaborg.unification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.metaborg.unification.eager.EagerTermUnifier;
import org.metaborg.unification.lazy.LazyTermUnifier;
import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.PrimitiveTerm;
import org.metaborg.unification.terms.StringTerm;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermVar;

import com.google.common.collect.Iterables;

public class UnificationTest {

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
        IUnifyResult result = unifier.unify(PrimitiveTerm.of(1), PrimitiveTerm.of(1));
        result = result.unifier().unify(PrimitiveTerm.of(1), PrimitiveTerm.of(42));
        assertEquals(1, Iterables.size(result.conflicts()));
    }

    private void testString(ITermUnifier unifier) {
        unifier.unify(StringTerm.of("foo"), StringTerm.of("foo"));
        IUnifyResult result = unifier.unify(StringTerm.of("foo"), StringTerm.of("bar"));
        assertEquals(1, Iterables.size(result.conflicts()));
    }

    private void testVar(ITermUnifier unifier) {
        IUnifyResult result0 = unifier.unify(TermVar.of(null, "a"), TermVar.of(null, "b"));
        IUnifyResult result = result0.unifier().unify(TermVar.of(null, "a"), PrimitiveTerm.of(42));
        IUnifyResult result1 = result.unifier().unify(TermVar.of(null, "b"), PrimitiveTerm.of(1));
        assertEquals(1, Iterables.size(result1.conflicts()));
        result = result.unifier().unify(TermVar.of(null, "b"), PrimitiveTerm.of(42));
        result = result0.unifier().unify(TermVar.of(null, "b"), PrimitiveTerm.of(1));
    }

    private void testAppl(ITermUnifier unifier) {
        IUnifyResult result = unifier.unify(ApplTerm.of("f"), ApplTerm.of("g"));
        assertEquals(1, Iterables.size(result.conflicts()));
        result = unifier.unify(ApplTerm.of("f", PrimitiveTerm.of(42)), ApplTerm.of("f"));
        assertEquals(1, Iterables.size(result.conflicts()));
        unifier.unify(ApplTerm.of("f", TermVar.of(null, "x"), TermVar.of(null, "x")),
                ApplTerm.of("f", TermVar.of(null, "y"), PrimitiveTerm.of(42)));
    }

    private void testOp(ITermUnifier unifier) {
        IUnifyResult result = unifier.unify(ApplTerm.of("f"), TermOp.of("lub"));
        assertNull(result);
    }

}