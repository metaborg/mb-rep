package org.metaborg.unification.persistent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.metaborg.fastutil.persistent.Object2ObjectPMap;
import org.metaborg.fastutil.persistent.Object2ObjectOpenHashPMap;
import org.metaborg.fastutil.persistent.ObjectOpenHashPSet;
import org.metaborg.fastutil.persistent.ObjectPSet;

public class FastUtilTests {

    @Test public void testPresistentMap() {
        Object2ObjectPMap<Integer,Integer> m1 = new Object2ObjectOpenHashPMap<>();
        assertFalse(m1.containsKey(1));
        Object2ObjectPMap<Integer,Integer> m2 = m1.put(1, 42);
        assertTrue(m2.containsKey(1));
        Object2ObjectPMap<Integer,Integer> m3 = m2.remove(1);
        assertFalse(m3.containsKey(1));

        assertTrue(m2.containsKey(1));
        assertFalse(m1.containsKey(1));
        assertTrue(m2.containsKey(1));
        assertFalse(m3.containsKey(1));
    }

    @Test public void testPresistentSet() {
        ObjectPSet<Integer> m1 = new ObjectOpenHashPSet<>();
        assertFalse(m1.contains(1));
        ObjectPSet<Integer> m2 = m1.add(1);
        assertTrue(m2.contains(1));
        ObjectPSet<Integer> m3 = m2.remove(1);
        assertFalse(m3.contains(1));

        assertTrue(m2.contains(1));
        assertFalse(m1.contains(1));
        assertTrue(m2.contains(1));
        assertFalse(m3.contains(1));
    }

}