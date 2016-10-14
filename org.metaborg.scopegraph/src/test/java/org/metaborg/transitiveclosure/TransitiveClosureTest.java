package org.metaborg.transitiveclosure;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransitiveClosureTest {

    @Test public void testTransitive() throws SymmetryException {
        TransitiveClosure<Integer> tc = new TransitiveClosure<>();
        tc = tc.add(1, 2);
        tc = tc.add(2, 3);
        assertTrue(tc.contains(1, 3));
    }

    @Test(expected = SymmetryException.class) public void testCycle1() throws SymmetryException {
        TransitiveClosure<Integer> tc = new TransitiveClosure<>();
        tc = tc.add(1, 2);
        tc = tc.add(2, 1);
    }

    @Test(expected = SymmetryException.class) public void testCycle2() throws SymmetryException {
        TransitiveClosure<Integer> tc = new TransitiveClosure<>();
        tc = tc.add(1, 2);
        tc = tc.add(2, 3);
        tc = tc.add(3, 1);
    }

}