package org.spoofax.interpreter.library.language.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.spoofax.interpreter.library.language.SemanticIndexEntry;
import org.spoofax.interpreter.library.language.SemanticIndexFile;
import org.spoofax.interpreter.library.language.SemanticIndexFileDescriptor;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SemanticIndexSymbolTableTest extends SemanticIndexTest {
  @Test
  public void files() {
    IStrategoTerm fileTerm1 = file("a/b/c");
    IStrategoTerm fileTerm2 = file("a/b/c", "some", "element");

    SemanticIndexFileDescriptor file1 = setupIndex(fileTerm1);
    SemanticIndexFileDescriptor file2 = setupIndex(fileTerm2);

    SemanticIndexFileDescriptor ret1 = index.getFileDescriptor(fileTerm1);
    SemanticIndexFileDescriptor ret2 = index.getFileDescriptor(fileTerm2);

    // Files have not been added yet.
    Collection<SemanticIndexFileDescriptor> all1 = index
        .getAllFileDescriptors();
    assertFalse(all1.contains(ret1));
    assertFalse(all1.contains(ret2));

    assertEquals(file1, ret1);
    assertEquals(file1.toTerm(factory), ret1.toTerm(factory));
    assertEquals(file2, ret2);
    assertEquals(file2.toTerm(factory), ret2.toTerm(factory));

    SemanticIndexFile retf1 = index.getFile(file1);
    SemanticIndexFile retf2 = index.getFile(file2);

    assertEquals(retf1.getDescriptor(), ret1);
    assertEquals(file1.toTerm(factory), retf1.toTerm(factory));
    assertEquals(retf2.getDescriptor(), ret2);
    assertEquals(file2.toTerm(factory), retf2.toTerm(factory));

    // Files have been added by calling getFile.
    Collection<SemanticIndexFileDescriptor> all2 = index
        .getAllFileDescriptors();
    assertTrue(all2.contains(ret1));
    assertTrue(all2.contains(ret2));
  }

  @Test
  public void getEntries() {
    IStrategoAppl def = def("Class", "java", "lang", "String");
    IStrategoAppl type = type(constructor("Type", str("String")), "Class",
        "java", "lang", "String");

    assertEquals(index.getEntries(def).size(), 0);
    assertEquals(index.getEntries(type).size(), 0);

    index.add(def, file);
    index.add(type, file);

    Collection<SemanticIndexEntry> ret1 = index.getEntries(def);
    Collection<SemanticIndexEntry> ret2 = index.getEntries(type);

    assertTrue(matchAll(ret1, def));
    assertTrue(matchAll(ret2, type));
    assertFalse(matchAll(ret1, type));
    assertFalse(matchAll(ret2, def));
  }

  @Test
  public void duplicateAddAndGetEntries() {
    IStrategoAppl def = def("Entity", "CRM", "Person");
    IStrategoAppl read = read("Function", "CRM", "Person", "GetName");

    assertEquals(index.getEntries(def).size(), 0);
    assertEquals(index.getEntries(read).size(), 0);

    index.add(def, file);
    index.add(def, file);
    index.add(def, file);
    index.add(read, file);
    index.add(read, file);

    Collection<SemanticIndexEntry> ret1 = index.getEntries(def);
    Collection<SemanticIndexEntry> ret2 = index.getEntries(read);

    assertEquals(ret1.size(), 3);
    assertEquals(ret2.size(), 2);

    assertTrue(matchAll(ret1, def));
    assertTrue(matchAll(ret2, read));
    assertFalse(matchAll(ret1, read));
    assertFalse(matchAll(ret2, def));

    // Add entries from ret2 again using the other add function.
    // Need to make a copy of ret2, because ret2 is a view over the index and
    // could cause a ConcurrentModificationException
    for (SemanticIndexEntry entry : ret2.toArray(new SemanticIndexEntry[0]))
      index.add(entry);

    Collection<SemanticIndexEntry> ret3 = index.getEntries(read);
    assertEquals(ret2.size(), 4);
    assertTrue(matchAll(ret3, read));
    assertFalse(matchAll(ret3, def));
  }

  @Test
  public void addAllAndGetAllEntries() {
    IStrategoAppl def = def("Class", "java", "lang", "String");
    IStrategoAppl type = type(constructor("Type", str("String")), "Class",
        "java", "lang", "String");
    IStrategoList all = factory.makeList(def, type);

    assertEquals(index.getEntries(def).size(), 0);
    assertEquals(index.getEntries(type).size(), 0);

    index.addAll(all, file);

    Collection<SemanticIndexEntry> ret = index.getAllEntries();

    assertTrue(contains(ret, def));
    assertTrue(contains(ret, type));
    assertFalse(contains(ret, all));
  }

  @Test
  public void getChildEntries() {
    IStrategoAppl classDef = def("Class", "java", "lang", "String");
    IStrategoAppl methodDef1 = def("Method", "java", "lang", "String", "charAt");
    IStrategoAppl methodDef2 = def("Method", "java", "lang", "String",
        "getBytes");
    IStrategoAppl fieldDef = def("Field", "java", "lang", "String", "length");

    IStrategoAppl methodsTemplate = def("Method", "java", "lang", "String");
    IStrategoAppl fieldsTemplate = def("Field", "java", "lang", "String");

    assertEquals(index.getEntries(classDef).size(), 0);
    assertEquals(index.getEntries(methodDef1).size(), 0);
    assertEquals(index.getEntries(methodDef2).size(), 0);
    assertEquals(index.getEntries(fieldDef).size(), 0);
    assertEquals(index.getEntryChildTerms(methodsTemplate).size(), 0);
    assertEquals(index.getEntryChildTerms(fieldsTemplate).size(), 0);

    index.add(classDef, file);
    index.add(methodDef1, file);
    index.add(methodDef2, file);
    index.add(fieldDef, file);

    Collection<SemanticIndexEntry> ret1 = index
        .getEntryChildTerms(methodsTemplate);
    Collection<SemanticIndexEntry> ret2 = index
        .getEntryChildTerms(fieldsTemplate);

    assertEquals(ret1.size(), 2);
    assertEquals(ret2.size(), 1);

    assertTrue(contains(ret1, methodDef1));
    assertTrue(contains(ret1, methodDef2));
    assertFalse(contains(ret1, fieldDef));
    assertFalse(contains(ret1, classDef));

    assertFalse(contains(ret2, methodDef1));
    assertFalse(contains(ret2, methodDef2));
    assertTrue(contains(ret2, fieldDef));
    assertFalse(contains(ret2, classDef));
  }

  @Test
  public void getEntriesInFileAndRemoveFile() {
    IStrategoTerm fileTerm1 = file("TestFile", "Partition", "1");
    SemanticIndexFileDescriptor file1 = setupIndex(fileTerm1);
    SemanticIndexFileDescriptor file2 = setupIndex(file("TestFile",
        "Partition", "2"));

    IStrategoAppl def1 = def("Entity", "CRM", "Person");
    IStrategoAppl read = read("Function", "CRM", "Person", "GetName");

    IStrategoAppl def2 = def("Class", "java", "lang", "String");
    IStrategoAppl type = type(constructor("Type", str("String")), "Class",
        "java", "lang", "String");

    assertEquals(index.getEntries(def1).size(), 0);
    assertEquals(index.getEntries(read).size(), 0);
    assertEquals(index.getEntries(def2).size(), 0);
    assertEquals(index.getEntries(type).size(), 0);
    assertEquals(index.getEntriesInFile(file1).size(), 0);
    assertEquals(index.getEntriesInFile(file2).size(), 0);

    index.add(def1, file1);
    index.add(read, file1);
    index.add(def2, file2);
    index.add(type, file2);

    Collection<SemanticIndexEntry> ret1 = index.getEntriesInFile(file1);
    Collection<SemanticIndexEntry> ret2 = index.getEntriesInFile(file2);

    assertEquals(ret1.size(), 2);
    assertEquals(ret2.size(), 2);

    assertTrue(contains(ret1, def1));
    assertTrue(contains(ret1, read));
    assertFalse(contains(ret1, def2));
    assertFalse(contains(ret1, type));

    assertFalse(contains(ret2, def1));
    assertFalse(contains(ret2, read));
    assertTrue(contains(ret2, def2));
    assertTrue(contains(ret2, type));

    for (SemanticIndexEntry entry : ret1) {
      assertSame(entry.getFileDescriptor(), file1);
      assertNotSame(entry.getFileDescriptor(), file2);
    }
    for (SemanticIndexEntry entry : ret2) {
      assertNotSame(entry.getFileDescriptor(), file1);
      assertSame(entry.getFileDescriptor(), file2);
    }

    index.removeFile(fileTerm1);
    assertEquals(index.getEntriesInFile(file1).size(), 0);
    assertEquals(index.getEntriesInFile(file2).size(), 2);

    index.removeFile(file2);
    assertEquals(index.getEntriesInFile(file2).size(), 0);
  }

  @Test
  public void clear() {
    SemanticIndexFileDescriptor file1 = setupIndex(file("TestFile",
        "Partition", "1"));
    SemanticIndexFileDescriptor file2 = setupIndex(file("TestFile",
        "Partition", "2"));

    IStrategoAppl readAll = readAll("Str", "Class", "java", "lang");

    assertEquals(index.getAllEntries().size(), 0);

    index.add(readAll, file1);
    index.add(readAll, file2);

    assertEquals(index.getAllEntries().size(), 2);

    index.clear();

    assertEquals(index.getAllEntries().size(), 0);
  }
}
