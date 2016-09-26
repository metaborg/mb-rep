package org.metaborg.unification.persistent;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.metaborg.unification.terms.IntTerm;
import org.metaborg.unification.terms.StringTerm;
import org.metaborg.unification.terms.TermVar;

public class UnificationTests {

    @Test public void testInts() {
        TermUnifier unifier = new TermUnifier();
        unifier.unify(new IntTerm(1), new IntTerm(1));
        try {
            unifier.unify(new IntTerm(1), new IntTerm(42));
            fail();
        } catch(Exception ex) {}
    }

    @Test public void testStrings() {
        TermUnifier unifier = new TermUnifier();
        unifier.unify(new StringTerm("foo"), new StringTerm("foo"));
        try {
            unifier.unify(new StringTerm("foo"), new StringTerm("bar"));
            fail();
        } catch(Exception ex) {}
    }

    @Test public void testVars() {
        TermUnifier unifier = new TermUnifier();
        UnifyResult result0 = unifier.unify(new TermVar("a"), new TermVar("b"));
        UnifyResult result = result0.unifier.unify(new TermVar("a"), new IntTerm(42));
        try {
            result = result.unifier.unify(new TermVar("b"), new IntTerm(1));
            fail();
        } catch(Exception ex) {}
        result = result.unifier.unify(new TermVar("b"), new IntTerm(42));
        result = result0.unifier.unify(new TermVar("b"), new IntTerm(1));
    }

}