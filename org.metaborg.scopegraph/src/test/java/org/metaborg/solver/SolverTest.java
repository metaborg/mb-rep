package org.metaborg.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;
import org.metaborg.regexp.FiniteAlphabet;
import org.metaborg.regexp.IAlphabet;
import org.metaborg.regexp.IRegExp;
import org.metaborg.regexp.IRegExpBuilder;
import org.metaborg.regexp.RegExpBuilder;
import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.TestLabel;
import org.metaborg.solver.constraints.CConj;
import org.metaborg.solver.constraints.CDisj;
import org.metaborg.solver.constraints.CEqual;
import org.metaborg.solver.constraints.CFalse;
import org.metaborg.solver.constraints.CTrue;
import org.metaborg.solver.constraints.IConstraint;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.lazy.LazyTermUnifier;
import org.metaborg.unification.terms.PrimitiveTerm;
import org.metaborg.unification.terms.TermVar;

import com.google.common.collect.ImmutableSet;

public class SolverTest {

    private static ILabel P;
    private static ILabel I;
    private static IRegExp<ILabel> wf;

    @BeforeClass public static void beforeClass() {
        P = TestLabel.of("P");
        I = TestLabel.of("I");
        IAlphabet<ILabel> alphabet = new FiniteAlphabet<>(ImmutableSet.of(P, I));
        IRegExpBuilder<ILabel> b = new RegExpBuilder<>(alphabet);
        wf = b.concat(b.closure(b.symbol(P)), b.closure(b.symbol(I)));
    }

    @Test public void testTrue() {
        IConstraint constraint = CTrue.of();
        Solver solver = new Solver(new LazyTermUnifier(), wf, Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertTrue(solution.getErrors().isEmpty());
    }

    @Test public void testFalse() {
        IConstraint constraint = CFalse.of();
        Solver solver = new Solver(new LazyTermUnifier(), wf, Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertEquals(1, solution.getErrors().size());
    }

    @Test public void testEqual() {
        ITerm v = TermVar.of(null, "a");
        ITerm i = PrimitiveTerm.of(1);
        IConstraint constraint = CEqual.of(v, i);
        Solver solver = new Solver(new LazyTermUnifier(), wf, Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertTrue(solution.getErrors().isEmpty());
        assertEquals(i, solution.getUnifier().find(v).rep());
    }

    @Test public void testConj() {
        IConstraint constraint = CConj.of(CTrue.of(), CFalse.of());
        Solver solver = new Solver(new LazyTermUnifier(), wf, Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertFalse(solution.getErrors().isEmpty());
    }

    @Test public void testDisj() {
        IConstraint constraint = CDisj.of(CTrue.of(), CFalse.of());
        Solver solver = new Solver(new LazyTermUnifier(), wf, Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertTrue(solution.getErrors().isEmpty());
    }

}