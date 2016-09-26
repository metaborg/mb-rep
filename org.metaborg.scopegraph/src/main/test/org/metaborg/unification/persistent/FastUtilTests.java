package org.metaborg.unification.persistent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.metaborg.unification.persistent.fastutil.PersistentObject2ObjectMap;
import org.metaborg.unification.persistent.fastutil.PersistentObject2ObjectOpenHashMap;
import org.metaborg.unification.persistent.fastutil.PersistentObjectOpenHashSet;
import org.metaborg.unification.persistent.fastutil.PersistentObjectSet;

public class FastUtilTests {

    @Test public void testPresistentMap() {
        PersistentObject2ObjectMap<Integer, Integer> m1 = new PersistentObject2ObjectOpenHashMap<>();
        assertFalse(m1.containsKey(1));
        PersistentObject2ObjectMap<Integer, Integer> m2 = m1.put(1, 42);
        assertTrue(m2.containsKey(1));
        PersistentObject2ObjectMap<Integer, Integer> m3 = m2.remove(1);
        assertFalse(m3.containsKey(1));

        assertTrue(m2.containsKey(1));
        assertFalse(m1.containsKey(1));
        assertTrue(m2.containsKey(1));
        assertFalse(m3.containsKey(1));
    }

    @Test public void testPresistentSet() {
        PersistentObjectSet<Integer> m1 = new PersistentObjectOpenHashSet<>();
        assertFalse(m1.contains(1));
        PersistentObjectSet<Integer> m2 = m1.add(1);
        assertTrue(m2.contains(1));
        PersistentObjectSet<Integer> m3 = m2.remove(1);
        assertFalse(m3.contains(1));

        assertTrue(m2.contains(1));
        assertFalse(m1.contains(1));
        assertTrue(m2.contains(1));
        assertFalse(m3.contains(1));
    }

}