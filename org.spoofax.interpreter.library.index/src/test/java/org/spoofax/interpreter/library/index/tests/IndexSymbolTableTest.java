package org.spoofax.interpreter.library.index.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.spoofax.interpreter.library.index.IIndexEntryIterable;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.library.index.IndexPartition;
import org.spoofax.interpreter.library.index.IndexPartitionDescriptor;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

@RunWith(value = Parameterized.class)
public class IndexSymbolTableTest extends IndexTest {
    private boolean startTransaction;

    public IndexSymbolTableTest(boolean startTransaction) {
        this.startTransaction = startTransaction;
    }

    private void startTransaction() {
        if(startTransaction)
            doStartTransaction();
    }

    private void endTransaction() {
        if(startTransaction)
            doEndTransaction();
    }

    @Before
    public void setUp() {
        index.clearAll();
    }

    @Test
    public void files() {
        IStrategoTerm fileTerm1 = file("a/b/c");
        IStrategoTerm fileTerm2 = file("a/b/c", "some", "element");

        startTransaction();

        IndexPartitionDescriptor file1 = setupIndex(fileTerm1);
        IndexPartitionDescriptor file2 = setupIndex(fileTerm2);

        IndexPartitionDescriptor ret1 = index.getPartitionDescriptor(fileTerm1);
        IndexPartitionDescriptor ret2 = index.getPartitionDescriptor(fileTerm2);

        // Files have not been added yet.
        Collection<IndexPartitionDescriptor> all1 = index.getAllPartitionDescriptors();
        assertFalse(all1.contains(ret1));
        assertFalse(all1.contains(ret2));

        assertEquals(ret1, file1);
        assertEquals(ret1.toTerm(factory), file1.toTerm(factory));
        assertEquals(ret2, file2);
        assertEquals(ret2.toTerm(factory), file2.toTerm(factory));

        IndexPartition retf1 = index.getPartition(file1);
        IndexPartition retf2 = index.getPartition(file2);

        assertEquals(ret1, retf1.getDescriptor());
        assertEquals(retf1.toTerm(factory), file1.toTerm(factory));
        assertEquals(ret2, retf2.getDescriptor());
        assertEquals(retf2.toTerm(factory), file2.toTerm(factory));

        // Files have been added by calling getFile.
        Collection<IndexPartitionDescriptor> all2 = index.getAllPartitionDescriptors();
        assertTrue(all2.contains(ret1));
        assertTrue(all2.contains(ret2));

        endTransaction();
    }

    @Test
    public void getEntries() {
        IStrategoAppl def = def("Class", "java", "lang", "String");
        IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
        IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");

        startTransaction();

        assertEquals(0, size(index.get(def)));
        assertEquals(0, size(index.get(type)));
        assertEquals(0, size(index.get(defData)));

        index.add(def, file);
        index.add(type, file);
        index.add(defData, file);

        IIndexEntryIterable ret1 = index.get(def);
        IIndexEntryIterable ret2 = index.get(type);
        IIndexEntryIterable ret3 = index.get(defData);

        try {
            ret1.lock();
            ret2.lock();
            ret3.lock();

            assertTrue(matchAll(ret1, def));
            assertTrue(matchAll(ret2, type));
            assertTrue(matchAll(ret3, defData));
            assertFalse(matchAll(ret1, type));
            assertFalse(matchAll(ret1, defData));
            assertFalse(matchAll(ret2, def));
            assertFalse(matchAll(ret2, defData));
            assertFalse(matchAll(ret3, def));
            assertFalse(matchAll(ret3, type));
        } finally {
            ret1.unlock();
            ret2.unlock();
            ret3.unlock();
        }

        endTransaction();
    }

    @Test
    public void duplicateAddAndGetEntries() {
        IStrategoAppl def = def("Entity", "CRM", "Person");
        IStrategoAppl read = read("Function", "CRM", "Person", "GetName");
        IStrategoAppl longTerm =
            longTerm(str("Entity"), str("CRM"), str("Person"), "Function", "CRM", "Person", "GetName");

        startTransaction();

        assertEquals(0, size(index.get(def)));
        assertEquals(0, size(index.get(read)));
        assertEquals(0, size(index.get(longTerm)));

        index.add(def, file);
        index.add(def, file);
        index.add(def, file);
        index.add(read, file);
        index.add(read, file);
        index.add(longTerm, file);

        IIndexEntryIterable ret1 = index.get(def);
        IIndexEntryIterable ret2 = index.get(read);
        IIndexEntryIterable ret3 = index.get(longTerm);

        try {
            ret1.lock();
            ret2.lock();
            ret3.lock();
            assertEquals(3, size(ret1));
            assertEquals(2, size(ret2));
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
        } finally {
            ret1.unlock();
            ret2.unlock();
            ret3.unlock();
        }

        endTransaction();

        startTransaction();

        try {
            ret2.lock();
            for(IndexEntry entry : ret2.toArray())
                index.add(entry);
        } finally {
            ret2.unlock();
        }

        IIndexEntryIterable ret4 = index.get(read);

        try {
            ret4.lock();

            assertEquals(4, size(ret4));
            assertTrue(matchAll(ret4, read));
            assertFalse(matchAll(ret4, def));
            assertFalse(matchAll(ret4, longTerm));
        } finally {
            ret4.unlock();
        }

        endTransaction();
    }

    @Test
    public void addAllAndGetAllEntries() {
        IStrategoAppl def = def("Class", "java", "lang", "String");
        IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
        IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");
        IStrategoList all = factory.makeList(def, type, defData);

        startTransaction();

        assertEquals(0, size(index.get(def)));
        assertEquals(0, size(index.get(type)));
        assertEquals(0, size(index.get(defData)));

        index.addAll(all, file);

        IIndexEntryIterable ret = index.getAll();

        try {
            ret.lock();

            assertTrue(containsEntry(ret, def));
            assertTrue(containsEntry(ret, type));
            assertTrue(containsEntry(ret, defData));
            assertFalse(containsEntry(ret, all));
        } finally {
            ret.unlock();
        }

        endTransaction();
    }

    @Test
    public void getChildrenEntries() {
        IStrategoAppl classDef = def("Class", "java", "lang", "String");
        IStrategoAppl methodDef1 = def("Method", "java", "lang", "String", "charAt");
        IStrategoAppl methodDef2 = def("Method", "java", "lang", "String", "getBytes");
        IStrategoAppl fieldDef = def("Field", "java", "lang", "String", "length");

        IStrategoAppl methodsTemplate = def("Method", "java", "lang", "String");
        IStrategoAppl fieldsTemplate = def("Field", "java", "lang", "String");

        startTransaction();

        assertEquals(0, size(index.get(classDef)));
        assertEquals(0, size(index.get(methodDef1)));
        assertEquals(0, size(index.get(methodDef2)));
        assertEquals(0, size(index.get(fieldDef)));
        assertEquals(0, size(index.getChildren(methodsTemplate)));
        assertEquals(0, size(index.getChildren(fieldsTemplate)));

        index.add(classDef, file);
        index.add(methodDef1, file);
        index.add(methodDef2, file);
        index.add(fieldDef, file);

        IIndexEntryIterable ret1 = index.getChildren(methodsTemplate);
        IIndexEntryIterable ret2 = index.getChildren(fieldsTemplate);

        try {
            ret1.lock();
            ret2.lock();

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
        } finally {
            ret2.unlock();
            ret1.unlock();
        }

        endTransaction();
    }

    @Test
    public void getEntriesInFileAndRemoveFile() {
        IStrategoTerm fileTerm1 = file("TestFile", "Partition", "1");
        IStrategoTerm fileTerm2 = file("TestFile", "Partition", "2");
        IndexPartitionDescriptor file1 = setupIndex(fileTerm1);
        IndexPartitionDescriptor file2 = setupIndex(fileTerm2);

        IStrategoAppl def1 = def("Entity", "CRM", "Person");
        IStrategoAppl read = read("Function", "CRM", "Person", "GetName");

        IStrategoAppl def2 = def("Class", "java", "lang", "String");
        IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");

        startTransaction();

        assertEquals(0, size(index.get(def1)));
        assertEquals(0, size(index.get(read)));
        assertEquals(0, size(index.get(def2)));
        assertEquals(0, size(index.get(type)));
        assertEquals(0, size(index.getInPartition(file1)));
        assertEquals(0, size(index.getInPartition(file2)));

        index.add(def1, file1);
        index.add(read, file1);
        index.add(def2, file2);
        index.add(type, file2);

        IIndexEntryIterable ret1 = index.getInPartition(file1);
        IIndexEntryIterable ret2 = index.getInPartition(file2);

        try {
            ret1.lock();
            ret2.lock();

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
                assertSame(entry.getPartition(), file1);
                assertNotSame(entry.getPartition(), file2);
            }
            for(IndexEntry entry : ret2) {
                assertNotSame(entry.getPartition(), file1);
                assertSame(entry.getPartition(), file2);
            }

            index.clearPartition(fileTerm1);
            assertEquals(0, size(ret1));
            assertEquals(2, size(ret2));

            index.clearPartition(file2);
            assertEquals(0, size(ret2));
        } finally {
            ret1.unlock();
            ret2.unlock();
        }

        endTransaction();
    }

    @Test
    public void getPartitionsOf() {
        IStrategoTerm fileTerm1 = file("TestFile", "Partition", "1");
        IndexPartitionDescriptor file1 = setupIndex(fileTerm1);
        IndexPartitionDescriptor file2 = setupIndex(file("TestFile", "Partition", "2"));

        IStrategoAppl def = def("Entity", "CRM", "Person");
        IStrategoAppl read = read("Function", "CRM", "Person", "GetName");
        IStrategoAppl longTerm =
            longTerm(str("Entity"), str("CRM"), str("Person"), "Function", "CRM", "Person", "GetName");
        IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");

        startTransaction();

        assertEquals(0, size(index.get(def)));
        assertEquals(0, size(index.get(read)));
        assertEquals(0, size(index.get(longTerm)));
        assertEquals(0, size(index.get(defData)));
        assertEquals(0, size(index.getInPartition(file1)));
        assertEquals(0, size(index.getInPartition(file2)));

        index.add(def, file1);
        index.add(def, file2);
        index.add(def, file1);
        index.add(read, file2);
        index.add(read, file2);
        index.add(longTerm, file1);

        Collection<IndexPartitionDescriptor> ret1 = index.getPartitionsOf(def);
        Collection<IndexPartitionDescriptor> ret2 = index.getPartitionsOf(read);
        Collection<IndexPartitionDescriptor> ret3 = index.getPartitionsOf(longTerm);
        Collection<IndexPartitionDescriptor> ret4 = index.getPartitionsOf(defData);

        assertTrue(containsPartitionDescriptor(ret1, file1));
        assertTrue(containsPartitionDescriptor(ret1, file2));
        assertFalse(containsPartitionDescriptor(ret2, file1));
        assertTrue(containsPartitionDescriptor(ret2, file2));
        assertTrue(containsPartitionDescriptor(ret3, file1));
        assertFalse(containsPartitionDescriptor(ret3, file2));
        assertFalse(containsPartitionDescriptor(ret4, file1));
        assertFalse(containsPartitionDescriptor(ret4, file2));
        
        endTransaction();
    }

    @Test
    public void remove() {
        IStrategoTerm fileTerm1 = file("TestFile", "Partition", "1");
        IStrategoTerm fileTerm2 = file("TestFile", "Partition", "2");
        IndexPartitionDescriptor file1 = setupIndex(fileTerm1);
        IndexPartitionDescriptor file2 = setupIndex(fileTerm2);

        IStrategoAppl def1 = def("Entity", "CRM", "Person");
        IStrategoAppl def2 = def("Class", "java", "lang", "String");

        startTransaction();

        assertEquals(0, size(index.get(def1)));
        assertEquals(0, size(index.get(def2)));
        assertEquals(0, size(index.getInPartition(file1)));
        assertEquals(0, size(index.getInPartition(file2)));

        index.add(def1, file1);
        index.add(def1, file2);
        index.add(def2, file1);
        index.add(def2, file2);
        
        index.remove(def1, file1);
        
        IIndexEntryIterable ret1 = index.getInPartition(file1);
        IIndexEntryIterable ret2 = index.getInPartition(file2);

        try {
            ret1.lock();
            ret2.lock();

            assertFalse(containsEntry(ret1, def1));
            assertTrue(containsEntry(ret2, def1));
            assertTrue(containsEntry(ret1, def2));
            assertTrue(containsEntry(ret2, def2));
        } finally {
            ret1.unlock();
            ret2.unlock();
        }
    }
    
    @Test
    public void removeAll() {
        IStrategoTerm fileTerm1 = file("TestFile", "Partition", "1");
        IStrategoTerm fileTerm2 = file("TestFile", "Partition", "2");
        IndexPartitionDescriptor file1 = setupIndex(fileTerm1);
        IndexPartitionDescriptor file2 = setupIndex(fileTerm2);

        IStrategoAppl def1 = def("Entity", "CRM", "Person");
        IStrategoAppl def2 = def("Class", "java", "lang", "String");

        startTransaction();

        assertEquals(0, size(index.get(def1)));
        assertEquals(0, size(index.get(def2)));
        assertEquals(0, size(index.getInPartition(file1)));
        assertEquals(0, size(index.getInPartition(file2)));

        index.add(def1, file1);
        index.add(def1, file2);
        index.add(def2, file1);
        index.add(def2, file2);
        
        index.removeAll(def1);
        
        IIndexEntryIterable ret1 = index.getInPartition(file1);
        IIndexEntryIterable ret2 = index.getInPartition(file2);

        try {
            ret1.lock();
            ret2.lock();

            assertFalse(containsEntry(ret1, def1));
            assertFalse(containsEntry(ret2, def1));
            assertTrue(containsEntry(ret1, def2));
            assertTrue(containsEntry(ret2, def2));
        } finally {
            ret1.unlock();
            ret2.unlock();
        }
    }
    
    @Test
    public void removeOne() {
        IStrategoTerm fileTerm1 = file("TestFile", "Partition", "1");
        IStrategoTerm fileTerm2 = file("TestFile", "Partition", "2");
        IndexPartitionDescriptor file1 = setupIndex(fileTerm1);
        IndexPartitionDescriptor file2 = setupIndex(fileTerm2);

        IStrategoAppl def = def("Entity", "CRM", "Person");
        IStrategoAppl defData = defData(constructor("Type"), constructor("Type", str("String")) ,"Class", "java", "lang", "String");
        IStrategoAppl defDataTemplate = defData(constructor("Type"), tuple() ,"Class", "java", "lang", "String");

        startTransaction();

        assertEquals(0, size(index.get(def)));
        assertEquals(0, size(index.get(defData)));
        assertEquals(0, size(index.getInPartition(file1)));
        assertEquals(0, size(index.getInPartition(file2)));

        index.add(def, file1);
        index.add(def, file2);
        index.add(defData, file1);
        index.add(defData, file2);
        
        index.removeOne(defDataTemplate);
        
        IIndexEntryIterable ret1 = index.getInPartition(file1);
        IIndexEntryIterable ret2 = index.getInPartition(file2);

        try {
            ret1.lock();
            ret2.lock();

            assertTrue(containsEntry(ret1, def));
            assertTrue(containsEntry(ret2, def));
            assertTrue(containsEntry(ret1, defData));
            assertTrue(containsEntry(ret2, defData));
        } finally {
            ret1.unlock();
            ret2.unlock();
        }
        
        index.removeOne(defData);
        
        IIndexEntryIterable ret3 = index.getInPartition(file1);
        IIndexEntryIterable ret4 = index.getInPartition(file2);

        try {
            ret3.lock();
            ret4.lock();

            assertTrue(containsEntry(ret3, def));
            assertTrue(containsEntry(ret4, def));
            assertFalse(containsEntry(ret3, defData));
            assertFalse(containsEntry(ret4, defData));
        } finally {
            ret3.unlock();
            ret4.unlock();
        }
    }
    
    @Test
    public void clear() {
        IndexPartitionDescriptor file1 = setupIndex(file("TestFile", "Partition", "1"));
        IndexPartitionDescriptor file2 = setupIndex(file("TestFile", "Partition", "2"));

        IStrategoAppl readAll = readAll("Str", "Class", "java", "lang");

        startTransaction();

        assertEquals(0, size(index.getAll()));

        index.add(readAll, file1);
        index.add(readAll, file2);

        assertEquals(2, size(index.getAll()));

        index.clearAll();

        assertEquals(0, size(index.getAll()));

        endTransaction();
    }
}
