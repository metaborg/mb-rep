package org.metaborg.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;
import org.metaborg.solver.constraints.CConj;
import org.metaborg.solver.constraints.CDisj;
import org.metaborg.solver.constraints.CEqual;
import org.metaborg.solver.constraints.CFalse;
import org.metaborg.solver.constraints.CTrue;
import org.metaborg.solver.constraints.IConstraint;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.lazy.LazyTermUnifier;
import org.metaborg.unification.terms.IntTerm;
import org.metaborg.unification.terms.TermVar;

public class SolverTest {

    @Test public void testTrue() {
        IConstraint constraint = CTrue.of();
        Solver solver = new Solver(new LazyTermUnifier(), Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertTrue(solution.getErrors().isEmpty());
    }

    @Test public void testFalse() {
        IConstraint constraint = CFalse.of();
        Solver solver = new Solver(new LazyTermUnifier(), Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertEquals(1, solution.getErrors().size());
    }

    @Test public void testEqual() {
        ITerm v = TermVar.of(null, "a");
        ITerm i = IntTerm.of(1);
        IConstraint constraint = CEqual.of(v, i);
        Solver solver = new Solver(new LazyTermUnifier(), Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertTrue(solution.getErrors().isEmpty());
        assertEquals(i, solution.getUnifier().find(v).rep());
    }

    @Test public void testConj() {
        IConstraint constraint = CConj.of(CTrue.of(), CFalse.of());
        Solver solver = new Solver(new LazyTermUnifier(), Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertFalse(solution.getErrors().isEmpty());
    }

    @Test public void testDisj() {
        IConstraint constraint = CDisj.of(CTrue.of(), CFalse.of());
        Solver solver = new Solver(new LazyTermUnifier(), Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertTrue(solution.getErrors().isEmpty());
    }

}