package org.spoofax.interpreter.library.index.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.library.index.IndexPartition;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.Iterables;

public class IndexSymbolTableTest extends IndexTest {
	public IndexSymbolTableTest() {
	}

	@Before
	public void setUp() {
		index.clearAll();
	}

	@Test
	public void files() {
		IStrategoAppl def = def("Class", "java", "lang", "String");

		IStrategoTerm fileTerm1 = partition("a/b/c");
		IStrategoTerm fileTerm2 = partition("a/b/c", "some", "element");

		IndexPartition file1 = getPartition(fileTerm1);
		IndexPartition file2 = getPartition(fileTerm2);

		// Files have not been added yet.
		Iterable<IndexPartition> all1 = index.getAllPartitions();
		assertFalse(Iterables.contains(all1, file1));
		assertFalse(Iterables.contains(all1, file2));

		add(def, file1);
		add(def, file2);

		// Files have been added by calling getFile.
		Iterable<IndexPartition> all2 = index.getAllPartitions();
		assertTrue(Iterables.contains(all2, file1));
		assertTrue(Iterables.contains(all2, file2));
	}

	@Test
	public void getEntries() {
		IStrategoAppl def = def("Class", "java", "lang", "String");
		IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
		IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");

		assertEquals(0, size(index.get(def)));
		assertEquals(0, size(index.get(type)));
		assertEquals(0, size(index.get(defData)));

		add(def, file);
		add(type, file);
		add(defData, file);

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

	@Test
	public void duplicateAddAndGetEntries() {
		IStrategoAppl def = def("Entity", "CRM", "Person");
		IStrategoAppl read = read("Function", "CRM", "Person", "GetName");
		IStrategoAppl longTerm =
			longTerm(str("Entity"), str("CRM"), str("Person"), "Function", "CRM", "Person", "GetName");

		assertEquals(0, size(index.get(def)));
		assertEquals(0, size(index.get(read)));
		assertEquals(0, size(index.get(longTerm)));

		add(def, file);
		add(def, file);
		add(def, file);
		add(read, file);
		add(read, file);
		add(longTerm, file);

		Iterable<IndexEntry> ret1 = index.get(def);
		Iterable<IndexEntry> ret2 = index.get(read);
		Iterable<IndexEntry> ret3 = index.get(longTerm);

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

		for(IndexEntry entry : Iterables.toArray(ret2, IndexEntry.class))
			index.add(entry);

		Iterable<IndexEntry> ret4 = index.get(read);

		assertEquals(4, size(ret4));
		assertTrue(matchAll(ret4, read));
		assertFalse(matchAll(ret4, def));
		assertFalse(matchAll(ret4, longTerm));
	}

	@Test
	public void getChildrenEntries() {
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
		assertEquals(0, size(index.getChildren(methodsTemplate)));
		assertEquals(0, size(index.getChildren(fieldsTemplate)));

		add(classDef, file);
		add(methodDef1, file);
		add(methodDef2, file);
		add(fieldDef, file);

		Iterable<IndexEntry> ret1 = index.getChildren(methodsTemplate);
		Iterable<IndexEntry> ret2 = index.getChildren(fieldsTemplate);

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

	@Test
	public void getEntriesInFileAndRemoveFile() {
		IStrategoTerm fileTerm1 = partition("TestFile", "Partition", "1");
		IStrategoTerm fileTerm2 = partition("TestFile", "Partition", "2");
		IndexPartition file1 = getPartition(fileTerm1);
		IndexPartition file2 = getPartition(fileTerm2);

		IStrategoAppl def1 = def("Entity", "CRM", "Person");
		IStrategoAppl read = read("Function", "CRM", "Person", "GetName");

		IStrategoAppl def2 = def("Class", "java", "lang", "String");
		IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");

		assertEquals(0, size(index.get(def1)));
		assertEquals(0, size(index.get(read)));
		assertEquals(0, size(index.get(def2)));
		assertEquals(0, size(index.get(type)));
		assertEquals(0, size(index.getInPartition(file1)));
		assertEquals(0, size(index.getInPartition(file2)));

		add(def1, file1);
		add(read, file1);
		add(def2, file2);
		add(type, file2);

		Iterable<IndexEntry> ret1 = index.getInPartition(file1);
		Iterable<IndexEntry> ret2 = index.getInPartition(file2);

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

		index.clearPartition(file1);
		assertEquals(0, size(ret1));
		assertEquals(2, size(ret2));

		index.clearPartition(file2);
		assertEquals(0, size(ret2));
	}

	@Test
	public void getPartitionsOf() {
		IStrategoTerm fileTerm1 = partition("TestFile", "Partition", "1");
		IndexPartition file1 = getPartition(fileTerm1);
		IndexPartition file2 = getPartition(partition("TestFile", "Partition", "2"));

		IStrategoAppl def = def("Entity", "CRM", "Person");
		IStrategoAppl read = read("Function", "CRM", "Person", "GetName");
		IStrategoAppl longTerm =
			longTerm(str("Entity"), str("CRM"), str("Person"), "Function", "CRM", "Person", "GetName");
		IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");

		assertEquals(0, size(index.get(def)));
		assertEquals(0, size(index.get(read)));
		assertEquals(0, size(index.get(longTerm)));
		assertEquals(0, size(index.get(defData)));
		assertEquals(0, size(index.getInPartition(file1)));
		assertEquals(0, size(index.getInPartition(file2)));

		add(def, file1);
		add(def, file2);
		add(def, file1);
		add(read, file2);
		add(read, file2);
		add(longTerm, file1);

		Iterable<IndexPartition> ret1 = index.getPartitionsOf(def);
		Iterable<IndexPartition> ret2 = index.getPartitionsOf(read);
		Iterable<IndexPartition> ret3 = index.getPartitionsOf(longTerm);
		Iterable<IndexPartition> ret4 = index.getPartitionsOf(defData);

		assertTrue(containsPartition(ret1, file1));
		assertTrue(containsPartition(ret1, file2));
		assertFalse(containsPartition(ret2, file1));
		assertTrue(containsPartition(ret2, file2));
		assertTrue(containsPartition(ret3, file1));
		assertFalse(containsPartition(ret3, file2));
		assertFalse(containsPartition(ret4, file1));
		assertFalse(containsPartition(ret4, file2));
	}

	@Test
	public void clear() {
		IndexPartition file1 = getPartition(partition("TestFile", "Partition", "1"));
		IndexPartition file2 = getPartition(partition("TestFile", "Partition", "2"));

		IStrategoAppl readAll = readAll("Str", "Class", "java", "lang");

		assertEquals(0, size(index.getAll()));

		add(readAll, file1);
		add(readAll, file2);

		assertEquals(2, size(index.getAll()));

		index.clearAll();

		assertEquals(0, size(index.getAll()));
	}
}
