package org.metaborg.unification.persistent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;
import org.metaborg.solver.ISolution;
import org.metaborg.solver.Solver;
import org.metaborg.solver.constraints.CConj;
import org.metaborg.solver.constraints.CDisj;
import org.metaborg.solver.constraints.CEqual;
import org.metaborg.solver.constraints.CFalse;
import org.metaborg.solver.constraints.CTrue;
import org.metaborg.solver.constraints.IConstraint;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.unification.terms.IntTerm;
import org.metaborg.unification.terms.TermVar;

import com.google.common.collect.Lists;

public class SolverTests {

    @Test public void testTrue() {
        IConstraint constraint = new CTrue();
        Solver solver = new Solver(Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertTrue(solution.getErrors().isEmpty());
    }

    @Test public void testFalse() {
        IConstraint constraint = new CFalse();
        Solver solver = new Solver(Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertEquals(1, solution.getErrors().size());
    }

    @Test public void testEqual() {
        ITerm v = new TermVar("a");
        ITerm i = new IntTerm(1);
        IConstraint constraint = new CEqual(v, i);
        Solver solver = new Solver(Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertTrue(solution.getErrors().isEmpty());
        assertEquals(i, solution.getUnifier().find(v).rep);
    }

    @Test public void testConj() {
        IConstraint constraint = new CConj(Lists.newArrayList(new CTrue(), new CFalse()));
        Solver solver = new Solver(Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertFalse(solution.getErrors().isEmpty());
    }

    @Test public void testDisj() {
        IConstraint constraint = new CDisj(Lists.newArrayList(new CTrue(), new CFalse()));
        Solver solver = new Solver(Collections.singleton(constraint));
        ISolution solution = solver.solve();
        assertNotNull(solution);
        assertTrue(solution.getErrors().isEmpty());
    }

}