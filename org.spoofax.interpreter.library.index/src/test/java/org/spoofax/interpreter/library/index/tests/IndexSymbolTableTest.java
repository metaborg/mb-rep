package org.spoofax.interpreter.library.index.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.spoofax.interpreter.library.index.IIndex;
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
		index.reset();
	}

	@Test
	public void partitions() {
		IStrategoAppl def = def("Class", "java", "lang", "String");

		IStrategoTerm partitionTerm1 = partition("a/b/c");
		IStrategoTerm partitionTerm2 = partition("a/b/c", "some", "element");

		IndexPartition partition1 = getPartition(partitionTerm1);
		IndexPartition partition2 = getPartition(partitionTerm2);

		// Partitions have not been added yet.
		Iterable<IndexPartition> all1 = index.getAllPartitions();
		assertFalse(Iterables.contains(all1, partition1));
		assertFalse(Iterables.contains(all1, partition2));

		add(def, partition1);
		add(def, partition2);

		// Partitions have been added by calling getPartition.
		Iterable<IndexPartition> all2 = index.getAllPartitions();
		assertTrue(Iterables.contains(all2, partition1));
		assertTrue(Iterables.contains(all2, partition2));
	}

	@Test
	public void getEntries() {
		IStrategoAppl def = def("Class", "java", "lang", "String");
		IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
		IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");

		assertEquals(0, size(index.get(def)));
		assertEquals(0, size(index.get(type)));
		assertEquals(0, size(index.get(defData)));

		add(def, partition);
		add(type, partition);
		add(defData, partition);

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

		add(def, partition);
		add(def, partition);
		add(def, partition);
		add(read, partition);
		add(read, partition);
		add(longTerm, partition);

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

		add(classDef, partition);
		add(methodDef1, partition);
		add(methodDef2, partition);
		add(fieldDef, partition);

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
	public void getEntriesInPartitionAndRemovePartition() {
		IStrategoTerm partitionTerm1 = partition("TestPartition", "Partition", "1");
		IStrategoTerm partitionTerm2 = partition("TestPartition", "Partition", "2");
		IndexPartition partition1 = getPartition(partitionTerm1);
		IndexPartition partition2 = getPartition(partitionTerm2);

		IStrategoAppl def1 = def("Entity", "CRM", "Person");
		IStrategoAppl read = read("Function", "CRM", "Person", "GetName");

		IStrategoAppl def2 = def("Class", "java", "lang", "String");
		IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");

		assertEquals(0, size(index.get(def1)));
		assertEquals(0, size(index.get(read)));
		assertEquals(0, size(index.get(def2)));
		assertEquals(0, size(index.get(type)));
		assertEquals(0, size(index.getInPartition(partition1)));
		assertEquals(0, size(index.getInPartition(partition2)));

		add(def1, partition1);
		add(read, partition1);
		add(def2, partition2);
		add(type, partition2);

		Iterable<IndexEntry> ret1 = index.getInPartition(partition1);
		Iterable<IndexEntry> ret2 = index.getInPartition(partition2);

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
			assertSame(entry.getPartition(), partition1);
			assertNotSame(entry.getPartition(), partition2);
		}
		for(IndexEntry entry : ret2) {
			assertNotSame(entry.getPartition(), partition1);
			assertSame(entry.getPartition(), partition2);
		}

		index.clearPartition(partition1);
		assertEquals(0, size(ret1));
		assertEquals(2, size(ret2));

		index.clearPartition(partition2);
		assertEquals(0, size(ret2));
	}

	@Test
	public void getPartitionsOf() {
		IStrategoTerm partitionTerm1 = partition("TestPartition", "Partition", "1");
		IndexPartition partition1 = getPartition(partitionTerm1);
		IndexPartition partition2 = getPartition(partition("TestPartition", "Partition", "2"));

		IStrategoAppl def = def("Entity", "CRM", "Person");
		IStrategoAppl read = read("Function", "CRM", "Person", "GetName");
		IStrategoAppl longTerm =
			longTerm(str("Entity"), str("CRM"), str("Person"), "Function", "CRM", "Person", "GetName");
		IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");

		assertEquals(0, size(index.get(def)));
		assertEquals(0, size(index.get(read)));
		assertEquals(0, size(index.get(longTerm)));
		assertEquals(0, size(index.get(defData)));
		assertEquals(0, size(index.getInPartition(partition1)));
		assertEquals(0, size(index.getInPartition(partition2)));

		add(def, partition1);
		add(def, partition2);
		add(def, partition1);
		add(read, partition2);
		add(read, partition2);
		add(longTerm, partition1);

		Iterable<IndexPartition> ret1 = index.getPartitionsOf(def);
		Iterable<IndexPartition> ret2 = index.getPartitionsOf(read);
		Iterable<IndexPartition> ret3 = index.getPartitionsOf(longTerm);
		Iterable<IndexPartition> ret4 = index.getPartitionsOf(defData);

		assertTrue(containsPartition(ret1, partition1));
		assertTrue(containsPartition(ret1, partition2));
		assertFalse(containsPartition(ret2, partition1));
		assertTrue(containsPartition(ret2, partition2));
		assertTrue(containsPartition(ret3, partition1));
		assertFalse(containsPartition(ret3, partition2));
		assertFalse(containsPartition(ret4, partition1));
		assertFalse(containsPartition(ret4, partition2));
	}

	@Test
	public void clear() {
		IndexPartition partition1 = getPartition(partition("TestPartition", "Partition", "1"));
		IndexPartition partition2 = getPartition(partition("TestPartition", "Partition", "2"));

		IStrategoAppl readAll = readAll("Str", "Class", "java", "lang");

		assertEquals(0, size(index.getAll()));

		add(readAll, partition1);
		add(readAll, partition2);

		assertEquals(2, size(index.getAll()));

		index.reset();

		assertEquals(0, size(index.getAll()));
	}
	
	// Changes in current index are not visible in parent index.
	@Test
	public void testStackNoParentChange() {
		final IIndex parent = index;
		IIndex current = parent;
		
		final IStrategoAppl def = def("Class", "java", "lang", "String");
		
		assertEquals(0, size(current.get(def)));
		
		current.startCollection(partition);
		add(current, def, partition);
		current.stopCollection();
		
		assertEquals(1, size(current.get(def)));
		
		current = indexManager.pushIndex(factory);
		assertNotSame(parent, current);
		
		assertEquals(1, size(current.get(def)));
		
		final IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
		final IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");
		
		assertEquals(0, size(current.get(type)));
		assertEquals(0, size(current.get(defData)));
		
		current.startCollection(partition);
		add(current, type, partition);
		add(current, defData, partition);
		current.stopCollection();
		
		assertEquals(0, size(current.get(def)));
		assertEquals(1, size(current.get(type)));
		assertEquals(1, size(current.get(defData)));
		
		assertEquals(1, size(parent.get(def)));
		assertEquals(0, size(parent.get(type)));
		assertEquals(0, size(parent.get(defData)));
		
		current = indexManager.popIndex();
		assertSame(parent, current);
	}
	
	// Changes in parent index are visible in current index.
	@Test
	public void testStackParentChangeVisible() {
		final IIndex parent = index;
		IIndex current = parent;
		
		final IStrategoAppl def = def("Class", "java", "lang", "String");
		
		assertEquals(0, size(current.get(def)));
		
		current.startCollection(partition);
		add(current, def, partition);
		current.stopCollection();
		
		assertEquals(1, size(current.get(def)));
		
		current = indexManager.pushIndex(factory);
		assertNotSame(parent, current);
		
		assertEquals(1, size(current.get(def)));
		
		final IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
		final IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");
		
		assertEquals(0, size(current.get(type)));
		assertEquals(0, size(current.get(defData)));
		
		current.startCollection(partition);
		add(current, type, partition);
		add(current, defData, partition);
		current.stopCollection();
		
		assertEquals(0, size(current.get(def)));
		assertEquals(1, size(current.get(type)));
		assertEquals(1, size(current.get(defData)));
		
		assertEquals(1, size(parent.get(def)));
		assertEquals(0, size(parent.get(type)));
		assertEquals(0, size(parent.get(defData)));
		
		final IStrategoAppl read = read("Function", "CRM", "Person", "GetName");
		
		assertEquals(0, size(current.get(read)));
		
		// Change the parent while current is still the new index.
		final IndexPartition partition2 = getPartition(partition("TestPartition", "Partition", "2"));
		parent.startCollection(partition2);
		add(parent, read, partition2);
		parent.stopCollection();
		
		assertEquals(0, size(current.get(def)));
		assertEquals(1, size(current.get(type)));
		assertEquals(1, size(current.get(defData)));
		assertEquals(1, size(current.get(read))); // Read is also visible in current.
		
		assertEquals(1, size(parent.get(def)));
		assertEquals(0, size(parent.get(type)));
		assertEquals(0, size(parent.get(defData)));
		assertEquals(1, size(parent.get(read)));
		
		current = indexManager.popIndex();
		assertSame(parent, current);
	}
	
	// Clearing a partition from the current index makes it invisible in parent index.
	@Test
	public void testStackClear() {
		final IIndex parent = index;
		IIndex current = parent;
		
		final IStrategoAppl def = def("Class", "java", "lang", "String");
		final IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
		final IStrategoAppl defData = defData(constructor("Type"), str("String"), "Class", "java", "lang", "String");
		
		assertEquals(0, size(current.get(def)));
		assertEquals(0, size(current.get(type)));
		assertEquals(0, size(current.get(defData)));
		
		current.startCollection(partition);
		add(current, def, partition);
		add(current, type, partition);
		add(current, defData, partition);
		current.stopCollection();
		
		assertEquals(1, size(current.get(def)));
		assertEquals(1, size(current.get(type)));
		assertEquals(1, size(current.get(defData)));
		
		current = indexManager.pushIndex(factory);
		assertNotSame(parent, current);
		
		assertEquals(1, size(current.get(def)));
		assertEquals(1, size(current.get(type)));
		assertEquals(1, size(current.get(defData)));
		
		current.clearPartition(partition);
		
		assertEquals(0, size(current.get(def)));
		assertEquals(0, size(current.get(type)));
		assertEquals(0, size(current.get(defData)));
		
		current = indexManager.popIndex();
		assertSame(parent, current);
		
		assertEquals(1, size(current.get(def)));
		assertEquals(1, size(current.get(type)));
		assertEquals(1, size(current.get(defData)));
	}
	
	// Popping an index discards all its changes.
	@Test
	public void testStackPop() {
		final IIndex parent = index;
		IIndex current = parent;
		
		final IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
		
		assertEquals(0, size(current.get(type)));
		
		final IndexPartition partition2 = getPartition(partition("TestPartition", "Partition", "2"));
		current.startCollection(partition2);
		add(current, type, partition2);
		current.stopCollection();
		
		assertEquals(1, size(current.get(type)));
		
		current = indexManager.pushIndex(factory);
		assertNotSame(parent, current);
		
		final IStrategoAppl def = def("Class", "java", "lang", "String");
		
		assertEquals(0, size(current.get(def)));
		
		current.startCollection(partition);
		add(current, def, partition);
		current.stopCollection();
		
		assertEquals(1, size(current.get(def)));
		
		current.clearPartition(partition2);
		
		assertEquals(0, size(current.get(type)));
		
		current = indexManager.popIndex();
		assertSame(parent, current);
		
		assertEquals(1, size(current.get(type)));
		assertEquals(0, size(current.get(def)));
	}
	
	// Merging an index engine preserves all its changes.
	@Test
	public void testStackMerge() {
		final IIndex parent = index;
		IIndex current = parent;
		
		final IStrategoAppl type = type(constructor("Type", str("String")), "Class", "java", "lang", "String");
		
		assertEquals(0, size(current.get(type)));
		
		final IndexPartition partition2 = getPartition(partition("TestPartition", "Partition", "2"));
		current.startCollection(partition2);
		add(current, type, partition2);
		current.stopCollection();
		
		assertEquals(1, size(current.get(type)));
		
		current = indexManager.pushIndex(factory);
		assertNotSame(parent, current);
		
		final IStrategoAppl def = def("Class", "java", "lang", "String");
		
		assertEquals(0, size(current.get(def)));
		
		current.startCollection(partition);
		add(current, def, partition);
		current.stopCollection();
		
		assertEquals(1, size(current.get(def)));
		
		current.clearPartition(partition2);
		
		assertEquals(0, size(current.get(type)));
		
		current = indexManager.mergeIndex();
		assertSame(parent, current);
		
		assertEquals(0, size(current.get(type))); // Partition2 was cleared.
		assertEquals(1, size(current.get(def)));  // Def was added.
	}
}
