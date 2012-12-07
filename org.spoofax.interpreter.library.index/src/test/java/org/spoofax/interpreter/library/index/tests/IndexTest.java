package org.spoofax.interpreter.library.index.tests;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized.Parameters;
import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.index.IndexPartitionDescriptor;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

public class IndexTest {
    protected static Interpreter interpreter;
    protected static ITermFactory factory;
    protected static IOAgent agent;

    protected static IStrategoString language;
    protected static IStrategoString projectPath;
    protected static IStrategoTerm fileTerm;

    protected static IndexManager indexManager;
    protected static IndexPartitionDescriptor project;
    protected static IndexPartitionDescriptor file;
    protected static IIndex index;

    @Parameters
    public static List<Object[]> data() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    }

    @BeforeClass
    public static void setUpOnce() {
        interpreter = new Interpreter();
        factory = interpreter.getFactory();
        agent = interpreter.getIOAgent();

        language = str("TestLanguage");
        projectPath = str("TestPath");
        fileTerm = file("TestFile");

        project = IndexPartitionDescriptor.fromTerm(agent, projectPath);
        indexManager = new IndexManager();
        indexManager.loadIndex(asJavaString(language), project.getURI(), factory, agent);
        index = indexManager.getCurrent();
        index.initialize(factory, agent);
        file = setupIndex(fileTerm);
    }

    @AfterClass
    public static void tearDownOnce() {
        index.clear();
        index = null;
        indexManager = null;
        project = null;
        language = null;
        projectPath = null;
        fileTerm = null;
        interpreter.shutdown();
        interpreter = null;
        factory = null;
        agent = null;
    }

    public static IndexPartitionDescriptor setupIndex(IStrategoTerm fileTerm) {
        IndexPartitionDescriptor file = getFile(fileTerm);
        indexManager.setCurrentPartition(file);
        return file;
    }

    public static void setupIndex(IndexPartitionDescriptor file) {
        indexManager.setCurrentPartition(file);
    }

    public static void doStartTransaction() {
        indexManager.startTransaction(factory, agent);
        index = indexManager.getCurrent();
    }

    public static void doEndTransaction() {
        indexManager.endTransaction();
        index = indexManager.getCurrent();
    }

    public static IndexPartitionDescriptor getFile(IStrategoTerm fileTerm) {
        return index.getPartitionDescriptor(fileTerm);
    }

    public static IStrategoString str(String str) {
        return factory.makeString(str);
    }

    public static IStrategoAppl constructor(String constructor, IStrategoTerm... terms) {
        return factory.makeAppl(factory.makeConstructor(constructor, terms.length), terms);
    }

    public static IStrategoTuple tuple(IStrategoTerm... terms) {
        return factory.makeTuple(terms);
    }

    public static IStrategoString file(String file) {
        return str(file);
    }

    public static IStrategoTuple file(String file, String namespace, String... path) {
        return factory.makeTuple(str(file), uri(namespace, path));
    }

    public static IStrategoList path(String... path) {
        IStrategoString[] strategoPath = new IStrategoString[path.length];
        for(int i = 0; i < path.length; ++i)
            // Paths are reversed in Stratego for easy appending of new names.
            strategoPath[i] = str(path[path.length - i - 1]);
        return factory.makeList(strategoPath);
    }

    public static IStrategoList uri(String namespace, String... path) {
        return factory.makeListCons(constructor(namespace), path(path));
    }

    public static IStrategoAppl def(String namespace, String... path) {
        return factory.makeAppl(factory.makeConstructor("Def", 1), uri(namespace, path));
    }

    public static IStrategoAppl use(String namespace, String... path) {
        return factory.makeAppl(factory.makeConstructor("Use", 1), uri(namespace, path));
    }

    public static IStrategoAppl read(String namespace, String... path) {
        return factory.makeAppl(factory.makeConstructor("Read", 1), uri(namespace, path));
    }

    public static IStrategoAppl readAll(String prefix, String namespace, String... path) {
        return factory.makeAppl(factory.makeConstructor("ReadAll", 2), uri(namespace, path), str(prefix));
    }

    public static IStrategoAppl type(IStrategoTerm type, String namespace, String... path) {
        return factory.makeAppl(factory.makeConstructor("Type", 2), uri(namespace, path), type);
    }

    public static IStrategoAppl defData(IStrategoTerm type, IStrategoTerm value, String namespace, String... path) {
        return factory.makeAppl(factory.makeConstructor("DefData", 3), uri(namespace, path), type, value);
    }

    public static IStrategoAppl longTerm(IStrategoTerm t1, IStrategoTerm t2, IStrategoTerm t3, String namespace,
        String... path) {
        return factory.makeAppl(factory.makeConstructor("LongTerm", 4), uri(namespace, path), t1, t2, t3);
    }

    public static boolean containsEntry(Collection<IndexEntry> entries, IStrategoTerm term) {
        boolean found = false;
        for(IndexEntry entry : entries)
            found = found || entry.toTerm(factory).match(term);
        return found;
    }

    public static boolean containsPartitionDescriptor(Collection<IndexPartitionDescriptor> partitionDescriptors,
        IndexPartitionDescriptor partition) {
        boolean found = false;
        for(IndexPartitionDescriptor partitionDescriptor : partitionDescriptors)
            found = found || partitionDescriptor.equals(partition);
        return found;
    }

    public static boolean matchAll(Collection<IndexEntry> entries, IStrategoTerm term) {
        if(entries.size() == 0)
            return false;
        boolean matchAll = true;
        for(IndexEntry entry : entries)
            matchAll = matchAll && entry.toTerm(factory).match(term);
        return matchAll;
    }
}
