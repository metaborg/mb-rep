package org.spoofax.interpreter.library.index.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.Iterables;

public class IndexSymbolTableTest extends IndexTest {
    public IndexSymbolTableTest() {
    }

    @Before public void setUp() {
        index.reset();
    }

    @Test public void sources() {
        IStrategoAppl def = def("Class", "java", "lang", "String");

        IStrategoTerm source1 = source("a/b/c");
        IStrategoTerm source2 = source("a/b/c", "some", "element");

        // Sources have not been added yet.
        Iterable<IStrategoTerm> all1 = index.getAllSources();
        assertFalse(Iterables.contains(all1, source1));
        assertFalse(Iterables.contains(all1, source2));

        add(def, source1);
        add(def, source2);

        // Sources have been added by calling getSource.
        Iterable<IStrategoTerm> all2 = index.getAllSources();
        assertTrue(Iterables.contains(all2, source1));
        assertTrue(Iterables.contains(all2, source2));
    }

    @Test public void getEntries() {
        IStrategoAppl def = def("Class", "java", "lang", "String");
        IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
        IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");

        assertEquals(0, size(index.get(def)));
        assertEquals(0, size(index.get(type)));
        assertEquals(0, size(index.get(defData)));

        add(def);
        add(type);
        add(defData);

        Iterable<IndexEntry> ret1 = index.get(def);
        Iterable<IndexEntry> ret2 = index.get(type);
        Iterable<IndexEntry> ret3 = index.get(defData);

        assertTrue(matchAll(ret1, def));
        assertTrue(matchAll(ret2, type));
        assertTrue(matchAll(ret3, defData));
        assertFalse(matchAll(ret1, type));
        assertFalse(matchAll(ret1, defData));
        assertFalse(matchAll(ret2, def));
        assertFalse(matchAll(ret2, defData));
        assertFalse(matchAll(ret3, def));
        assertFalse(matchAll(ret3, type));
    }

    @Test public void duplicateAddAndGetEntries() {
        IStrategoAppl def = def("Entity", "CRM", "Person");
        IStrategoAppl read = read("Function", "CRM", "Person", "GetName");
        IStrategoAppl longTerm =
            longTerm(str("Entity"), str("CRM"), str("Person"), "Function", "CRM", "Person", "GetName");

        assertEquals(0, size(index.get(def)));
        assertEquals(0, size(index.get(read)));
        assertEquals(0, size(index.get(longTerm)));

        startCollection();

        collect(def);
        collect(def);
        collect(def);
        collect(read);
        collect(read);
        collect(longTerm);

        stopCollection();

        Iterable<IndexEntry> ret1 = index.get(def);
        Iterable<IndexEntry> ret2 = index.get(read);
        Iterable<IndexEntry> ret3 = index.get(longTerm);

        assertEquals(1, size(ret1));
        assertEquals(1, size(ret2));
        assertEquals(1, size(ret3));

        assertTrue(matchAll(ret1, def));
        assertTrue(matchAll(ret2, read));
        assertTrue(matchAll(ret3, longTerm));
        assertFalse(matchAll(ret1, read));
        assertFalse(matchAll(ret1, longTerm));
        assertFalse(matchAll(ret2, def));
        assertFalse(matchAll(ret2, longTerm));
        assertFalse(matchAll(ret3, def));
        assertFalse(matchAll(ret3, read));

        for(IndexEntry entry : Iterables.toArray(ret2, IndexEntry.class))
            index.add(entry);

        Iterable<IndexEntry> ret4 = index.get(read);

        assertEquals(1, size(ret4));
        assertTrue(matchAll(ret4, read));
        assertFalse(matchAll(ret4, def));
        assertFalse(matchAll(ret4, longTerm));
    }

    @Test public void getChildrenEntries() {
        IStrategoAppl classDef = def("Class", "java", "lang", "String");
        IStrategoAppl methodDef1 = def("Method", "java", "lang", "String", "charAt");
        IStrategoAppl methodDef2 = def("Method", "java", "lang", "String", "getBytes");
        IStrategoAppl fieldDef = def("Field", "java", "lang", "String", "length");

        IStrategoAppl methodsTemplate = def("Method", "java", "lang", "String");
        IStrategoAppl fieldsTemplate = def("Field", "java", "lang", "String");

        assertEquals(0, size(index.get(classDef)));
        assertEquals(0, size(index.get(methodDef1)));
        assertEquals(0, size(index.get(methodDef2)));
        assertEquals(0, size(index.get(fieldDef)));
        assertEquals(0, size(index.getChilds(methodsTemplate)));
        assertEquals(0, size(index.getChilds(fieldsTemplate)));

        startCollection();

        collect(classDef);
        collect(methodDef1);
        collect(methodDef2);
        collect(fieldDef);

        stopCollection();

        Iterable<IndexEntry> ret1 = index.getChilds(methodsTemplate);
        Iterable<IndexEntry> ret2 = index.getChilds(fieldsTemplate);

        assertEquals(2, size(ret1));
        assertEquals(1, size(ret2));

        assertTrue(containsEntry(ret1, methodDef1));
        assertTrue(containsEntry(ret1, methodDef2));
        assertFalse(containsEntry(ret1, fieldDef));
        assertFalse(containsEntry(ret1, classDef));

        assertFalse(containsEntry(ret2, methodDef1));
        assertFalse(containsEntry(ret2, methodDef2));
        assertTrue(containsEntry(ret2, fieldDef));
        assertFalse(containsEntry(ret2, classDef));
    }

    @Test public void getEntriesInSourceAndRemoveSource() {
        IStrategoTerm source1 = source("TestSource", "Source", "1");
        IStrategoTerm source2 = source("TestSource", "Source", "2");

        IStrategoAppl def1 = def("Entity", "CRM", "Person");
        IStrategoAppl read = read("Function", "CRM", "Person", "GetName");

        IStrategoAppl def2 = def("Class", "java", "lang", "String");
        IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");

        assertEquals(0, size(index.get(def1)));
        assertEquals(0, size(index.get(read)));
        assertEquals(0, size(index.get(def2)));
        assertEquals(0, size(index.get(type)));
        assertEquals(0, size(index.getInSource(source1)));
        assertEquals(0, size(index.getInSource(source2)));

        startCollection(source1);
        collect(def1);
        collect(read);
        stopCollection(source1);
        startCollection(source2);
        collect(def2);
        collect(type);
        stopCollection(source2);

        Iterable<IndexEntry> ret1 = index.getInSource(source1);
        Iterable<IndexEntry> ret2 = index.getInSource(source2);

        assertEquals(2, size(ret1));
        assertEquals(2, size(ret2));

        assertTrue(containsEntry(ret1, def1));
        assertTrue(containsEntry(ret1, read));
        assertFalse(containsEntry(ret1, def2));
        assertFalse(containsEntry(ret1, type));

        assertFalse(containsEntry(ret2, def1));
        assertFalse(containsEntry(ret2, read));
        assertTrue(containsEntry(ret2, def2));
        assertTrue(containsEntry(ret2, type));

        for(IndexEntry entry : ret1) {
            assertSame(entry.source, source1);
            assertNotSame(entry.source, source2);
        }
        for(IndexEntry entry : ret2) {
            assertNotSame(entry.source, source1);
            assertSame(entry.source, source2);
        }

        index.clearSource(source1);
        assertEquals(0, size(ret1));
        assertEquals(2, size(ret2));

        index.clearSource(source2);
        assertEquals(0, size(ret2));
    }

    @Test public void getSourcesOf() {
        IStrategoTerm source1 = source("TestSource", "Source", "1");
        IStrategoTerm source2 = source("TestSource", "Source", "2");

        IStrategoAppl def = def("Entity", "CRM", "Person");
        IStrategoAppl read = read("Function", "CRM", "Person", "GetName");
        IStrategoAppl longTerm =
            longTerm(str("Entity"), str("CRM"), str("Person"), "Function", "CRM", "Person", "GetName");
        IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");

        assertEquals(0, size(index.get(def)));
        assertEquals(0, size(index.get(read)));
        assertEquals(0, size(index.get(longTerm)));
        assertEquals(0, size(index.get(defData)));
        assertEquals(0, size(index.getInSource(source1)));
        assertEquals(0, size(index.getInSource(source2)));

        startCollection(source1);
        collect(def);
        collect(def);
        collect(longTerm);
        stopCollection(source1);
        startCollection(source2);
        collect(def);
        collect(read);
        collect(read);
        stopCollection(source2);

        Iterable<IStrategoTerm> ret1 = index.getSourcesOf(def);
        Iterable<IStrategoTerm> ret2 = index.getSourcesOf(read);
        Iterable<IStrategoTerm> ret3 = index.getSourcesOf(longTerm);
        Iterable<IStrategoTerm> ret4 = index.getSourcesOf(defData);

        assertTrue(containsSource(ret1, source1));
        assertTrue(containsSource(ret1, source2));
        assertFalse(containsSource(ret2, source1));
        assertTrue(containsSource(ret2, source2));
        assertTrue(containsSource(ret3, source1));
        assertFalse(containsSource(ret3, source2));
        assertFalse(containsSource(ret4, source1));
        assertFalse(containsSource(ret4, source2));
    }

    @Test public void clear() {
        IStrategoTerm source1 = source("TestSource", "Source", "1");
        IStrategoTerm source2 = source("TestSource", "Source", "2");

        IStrategoAppl readAll = readAll("Str", "Class", "java", "lang");

        assertEquals(0, size(index.getAll()));

        add(readAll, source1);
        add(readAll, source2);

        assertEquals(2, size(index.getAll()));

        index.reset();

        assertEquals(0, size(index.getAll()));
    }
}
