package org.spoofax.interpreter.library.index.tests;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized.Parameters;
import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.Iterables;

public class IndexTest {
    protected static Interpreter interpreter;
    protected static ITermFactory factory;

    protected static IStrategoString language;
    protected static IStrategoTerm source;
    protected static IIndex index;

    @Parameters public static List<Object[]> data() {
        Object[][] data = new Object[][] {};
        return Arrays.asList(data);
    }

    @BeforeClass public static void setUpOnce() {
        interpreter = new Interpreter();
        factory = interpreter.getFactory();

        language = str("TestLanguage");
        source = source("TestFile");

        index = IndexManager.create(factory);
    }

    @AfterClass public static void tearDownOnce() {
        index.reset();
        index = null;
        language = null;
        source = null;
        interpreter.shutdown();
        interpreter = null;
        factory = null;
    }


    public static void startCollection(IIndex index, IStrategoTerm source) {
        index.startCollection(source);
    }

    public static void startCollection(IStrategoTerm source) {
        index.startCollection(source);
    }

    public static void startCollection(IIndex index) {
        index.startCollection(source);
    }

    public static void startCollection() {
        index.startCollection(source);
    }

    public static IStrategoTerm stopCollection(IIndex index, IStrategoTerm source) {
        return index.stopCollection(source);
    }

    public static IStrategoTerm stopCollection(IStrategoTerm source) {
        return index.stopCollection(source);
    }

    public static IStrategoTerm stopCollection(IIndex index) {
        return index.stopCollection(source);
    }

    public static IStrategoTerm stopCollection() {
        return index.stopCollection(source);
    }


    public static IndexEntry collect(IIndex index, IStrategoTerm key, IStrategoTerm value) {
        final IndexEntry entry = index.collect(key, value);
        return entry;
    }

    public static IndexEntry collect(IIndex index, IStrategoTerm key) {
        final IndexEntry entry = index.collect(key);
        return entry;
    }

    public static IndexEntry collect(IStrategoTerm key, IStrategoTerm value) {
        final IndexEntry entry = index.collect(key, value);
        return entry;
    }

    public static IndexEntry collect(IStrategoTerm key) {
        final IndexEntry entry = index.collect(key);
        return entry;
    }


    public static IndexEntry add(IIndex index, IStrategoTerm key, IStrategoTerm value, IStrategoTerm source) {
        final IndexEntry entry = index.entryFactory().create(key, value, source);
        index.add(entry);
        return entry;
    }

    public static IndexEntry add(IIndex index, IStrategoTerm key, IStrategoTerm source) {
        final IndexEntry entry = index.entryFactory().create(key, source);
        index.add(entry);
        return entry;
    }

    public static IndexEntry add(IIndex index, IStrategoTerm key) {
        final IndexEntry entry = index.entryFactory().create(key, source);
        index.add(entry);
        return entry;
    }

    public static IndexEntry add(IStrategoTerm key, IStrategoTerm value, IStrategoTerm source) {
        final IndexEntry entry = index.entryFactory().create(key, value, source);
        index.add(entry);
        return entry;
    }

    public static IndexEntry add(IStrategoTerm key, IStrategoTerm source) {
        final IndexEntry entry = index.entryFactory().create(key, source);
        index.add(entry);
        return entry;
    }

    public static IndexEntry add(IStrategoTerm key) {
        final IndexEntry entry = index.entryFactory().create(key, source);
        index.add(entry);
        return entry;
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


    public static IStrategoString source(String file) {
        return str(file);
    }

    public static IStrategoTuple source(String file, String namespace, String... path) {
        return factory.makeTuple(str(file), uri(namespace, path));
    }

    public static IStrategoTerm uri(String namespace, String... path) {
        IStrategoList segments = factory.makeList();
        for(String name : path) {
            segments = factory.makeListCons(segment(namespace, name), segments);
        }
        return factory.makeAppl(factory.makeConstructor("URI", 2), language, segments);
    }

    public static IStrategoTerm segment(String namespace, String name) {
        return factory.makeAppl(factory.makeConstructor("ID", 2), factory.makeString(namespace),
            factory.makeString(name));
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


    public static boolean containsEntry(Iterable<IndexEntry> entries, IStrategoTerm source, IStrategoTerm value) {
        boolean found = false;
        for(IndexEntry entry : entries)
            found = found || (entry.source.equals(source) && entry.value.match(value));
        return found;
    }

    public static boolean containsEntry(Iterable<IndexEntry> entries, IStrategoTerm value) {
        boolean found = false;
        for(IndexEntry entry : entries)
            found = found || entry.value.match(value);
        return found;
    }

    public static boolean containsSource(Iterable<IStrategoTerm> sources, IStrategoTerm source) {
        boolean found = false;
        for(IStrategoTerm searchSource : sources)
            found = found || searchSource.equals(source);
        return found;
    }

    public static boolean matchAll(Iterable<IndexEntry> entries, IStrategoTerm value) {
        if(!entries.iterator().hasNext())
            return false;
        boolean matchAll = true;
        for(IndexEntry entry : entries)
            matchAll = matchAll && entry.value.match(value);
        return matchAll;
    }

    public static int size(Iterable<IndexEntry> entries) {
        return Iterables.size(entries);
    }
}
