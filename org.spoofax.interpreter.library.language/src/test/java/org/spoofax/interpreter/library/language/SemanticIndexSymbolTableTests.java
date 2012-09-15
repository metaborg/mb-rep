package org.spoofax.interpreter.library.language;

import java.util.Collection;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SemanticIndexSymbolTableTests extends SemanticIndexTestCase 
{
  protected SemanticIndexFileDescriptor setupIndex(IStrategoTerm fileTerm)
  {
    SemanticIndexFileDescriptor file = getFile(fileTerm);
    indexManager.setCurrentFile(file);
    return file;
  }
  
  protected SemanticIndexFileDescriptor getFile(IStrategoTerm fileTerm)
  {
    return index.getFileDescriptor(fileTerm);
  }
  
  public void testGetFile()
  {
    index.clear();
    
    IStrategoTerm fileTerm1 = file("a/b/c");
    IStrategoTerm fileTerm2 = file("a/b/c", "some", "element");
    
    SemanticIndexFileDescriptor file1 = setupIndex(fileTerm1);
    SemanticIndexFileDescriptor file2 = setupIndex(fileTerm2);
    
    SemanticIndexFileDescriptor ret1 = index.getFileDescriptor(fileTerm1);
    SemanticIndexFileDescriptor ret2 = index.getFileDescriptor(fileTerm2);
    
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
  }
  
  public void testGetEntries()
  {
    SemanticIndexFileDescriptor file = setupIndex(file("TestFile"));
    index.clear();
    
    IStrategoAppl def1 = def("Class", "java", "lang", "String");
    IStrategoAppl def2 = use("Package", "java", "lang");
    
    assertEquals(index.getEntries(def1).size(), 0);
    assertEquals(index.getEntries(def2).size(), 0);
    
    index.add(def1, file);
    index.add(def2, file);
    
    IStrategoAppl ret1 = index.getEntries(def1).toArray(new SemanticIndexEntry[0])[0].toTerm(factory);
    IStrategoAppl ret2 = index.getEntries(def2).toArray(new SemanticIndexEntry[0])[0].toTerm(factory);
    
    assertTrue(def1.match(ret1));
    assertTrue(def2.match(ret2));
    assertFalse(def1.match(ret2));
    assertFalse(def2.match(ret1));
  }
  
  public void testMultipleGetEntries()
  {
    SemanticIndexFileDescriptor file = setupIndex(file("TestFile"));
    index.clear();
    
    IStrategoAppl def1 = def("Entity", "CRM", "Person");
    IStrategoAppl def2 = read("Function", "CRM", "Person", "GetName");
    
    assertEquals(index.getEntries(def1).size(), 0);
    assertEquals(index.getEntries(def2).size(), 0);
    
    index.add(def1, file);
    index.add(def1, file);
    index.add(def1, file);
    index.add(def2, file);
    index.add(def2, file);
    
    Collection<SemanticIndexEntry> ret1 = index.getEntries(def1);
    Collection<SemanticIndexEntry> ret2 = index.getEntries(def2);
    
    assertEquals(ret1.size(), 3);
    assertEquals(ret2.size(), 2);
    
    for(SemanticIndexEntry entry : ret1)
      assertTrue(def1.match(entry.toTerm(factory)));
    for(SemanticIndexEntry entry : ret2)
      assertTrue(def2.match(entry.toTerm(factory)));
    for(SemanticIndexEntry entry : ret2)
      assertFalse(def1.match(entry.toTerm(factory)));
    for(SemanticIndexEntry entry : ret1)
      assertFalse(def2.match(entry.toTerm(factory)));
  }
}
