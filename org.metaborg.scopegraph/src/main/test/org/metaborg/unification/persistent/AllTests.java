package org.metaborg.unification.persistent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.metaborg.unification.persistent.conchon_filliatre.PersistentHashMap;
import org.metaborg.unification.persistent.conchon_filliatre.PersistentHashSet;
import org.metaborg.unification.persistent.conchon_filliatre.SemiPersistentHashMap;
import org.metaborg.unification.persistent.conchon_filliatre.SemiPersistentHashSet;

public class AllTests {

    @Test public void testPresistentMap() {
        PersistentHashMap<Integer,Integer> m1 = new PersistentHashMap<>();
        assertFalse(m1.containsKey(1));
        PersistentHashMap<Integer,Integer> m2 = m1.put(1, 42);
        assertTrue(m2.containsKey(1));
        PersistentHashMap<Integer,Integer> m3 = m2.remove(1);
        assertFalse(m3.containsKey(1));

        assertTrue(m2.containsKey(1));
        assertFalse(m1.containsKey(1));
        assertTrue(m2.containsKey(1));
        assertFalse(m3.containsKey(1));
    }

    @Test public void testSemiPresistentMap() {
        SemiPersistentHashMap<Integer,Integer> m1 = new SemiPersistentHashMap<>();
        assertFalse(m1.containsKey(1));
        SemiPersistentHashMap<Integer,Integer> m2 = m1.put(1, 42);
        assertTrue(m2.containsKey(1));
        SemiPersistentHashMap<Integer,Integer> m3 = m2.remove(1);
        assertFalse(m3.containsKey(1));

        assertTrue(m2.containsKey(1));
        
        try{
            m3.containsKey(1);
            fail();
        } catch(IllegalStateException ex) {}
    }

    @Test public void testPresistentSet() {
        PersistentHashSet<Integer> m1 = new PersistentHashSet<>();
        assertFalse(m1.contains(1));
        PersistentHashSet<Integer> m2 = m1.add(1);
        assertTrue(m2.contains(1));
        PersistentHashSet<Integer> m3 = m2.remove(1);
        assertFalse(m3.contains(1));

        assertTrue(m2.contains(1));
        assertFalse(m1.contains(1));
        assertTrue(m2.contains(1));
        assertFalse(m3.contains(1));
    }

    @Test public void testSemiPresistentSet() {
        SemiPersistentHashSet<Integer> m1 = new SemiPersistentHashSet<>();
        assertFalse(m1.contains(1));
        SemiPersistentHashSet<Integer> m2 = m1.add(1);
        assertTrue(m2.contains(1));
        SemiPersistentHashSet<Integer> m3 = m2.remove(1);
        assertFalse(m3.contains(1));

        assertTrue(m2.contains(1));
        
        try{
            m3.contains(1);
            fail();
        } catch(IllegalStateException ex) {}
    }

}
