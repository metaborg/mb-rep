package org.spoofax.interpreter.library.index;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.vfs2.FileObject;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.ParseError;
import org.spoofax.terms.io.binary.SAFWriter;
import org.spoofax.terms.io.binary.TermReader;

public class IndexManager {
    public static IIndex create(ITermFactory termFactory) {
        return new Index(termFactory);
    }

    public static IIndex read(FileObject indexFile, ITermFactory termFactory) throws ParseError, IOException, Exception {
        final IStrategoTerm term = readTerm(indexFile, termFactory);
        final IndexFactory factory = factory(termFactory);
        final IIndex index = create(termFactory);
        return factory.indexFromTerm(index, term);
    }

    public static void write(IIndex index, FileObject indexFile, ITermFactory termFactory) throws IOException {
        indexFile.createFile();
        final IndexFactory factory = factory(termFactory);
        final IStrategoTerm term = factory.indexToTerm(index);
        writeTerm(indexFile, term);
    }


    private static IndexFactory factory(ITermFactory termFactory) {
        return new IndexFactory(termFactory, new IndexEntryFactory(termFactory));
    }


    private static IStrategoTerm readTerm(FileObject file, ITermFactory termFactory) throws ParseError, IOException {
        final TermReader termReader = new TermReader(termFactory);
        return termReader.parseFromStream(file.getContent().getInputStream());
    }

    private static void writeTerm(FileObject file, IStrategoTerm term) throws IOException {
        file.createFile();
        final OutputStream output = file.getContent().getOutputStream();
        try {
            SAFWriter.writeTermToSAFStream(term, output);
            output.flush();
        } finally {
            output.close();
        }
    }
}
