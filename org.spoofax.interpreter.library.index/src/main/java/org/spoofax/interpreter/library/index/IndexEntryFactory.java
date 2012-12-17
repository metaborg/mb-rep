package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.isTermAppl;
import static org.spoofax.interpreter.core.Tools.isTermList;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.TermAttachmentStripper;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 * @author Gabriël Konat
 */
public class IndexEntryFactory {
    private static final int DEFDATA_TYPE = 1;
    private static final int DEFDATA_VALUE = 2;
    private static final IStrategoConstructor DEFDATA_CONSTRUCTOR = new TermFactory().makeConstructor("DefData", 3);

    private final ITermFactory termFactory;
    private final TermAttachmentStripper stripper;

    public IndexEntryFactory(ITermFactory termFactory) {
        this.termFactory = termFactory;
        this.stripper = new TermAttachmentStripper(termFactory);
    }

    public ITermFactory getTermFactory() {
        return termFactory;
    }

    public IndexURI createURI(IStrategoConstructor constructor, IStrategoTerm identifier, IStrategoTerm type) {
        ImploderAttachment idAttachment = ImploderAttachment.getCompactPositionAttachment(identifier, true);
        type = stripper.strip(type);
        identifier.putAttachment(idAttachment);
        return new IndexURI(constructor, identifier, type);
    }

    public IndexURI createURIFromTemplate(IStrategoAppl template) {
        return createURI(template.getConstructor(), getEntryIdentifier(template), getEntryType(template));
    }

    public IndexEntry createEntry(IStrategoConstructor constructor, IStrategoTerm identifier, IStrategoTerm type,
        IStrategoTerm value, IndexPartitionDescriptor partition) {
        return createEntry(value, createURI(constructor, identifier, type), partition);
    }

    public IndexEntry createEntry(IStrategoTerm value, IndexURI key, IndexPartitionDescriptor partition) {
        ImploderAttachment dataAttachment =
            value == null ? null : ImploderAttachment.getCompactPositionAttachment(value, false);
        value = stripper.strip(value);
        if(value != null)
            value.putAttachment(dataAttachment);

        return new IndexEntry(key, value, partition);
    }

    public static boolean isURI(IStrategoTerm term) {
        if(isTermList(term)) {
            return true;
        } else if(isTermAppl(term)) {
            for(int i = 0; i < term.getSubtermCount(); ++i) {
                if(isTermList(term.getSubterm(i)))
                    return true;
            }
        }
        return false;
    }

    public static boolean isDefData(IStrategoAppl term) {
        return isDefData(term.getConstructor());
    }

    public static boolean isDefData(IStrategoConstructor constructor) {
        return constructor.equals(DEFDATA_CONSTRUCTOR);
    }

    public IStrategoTerm getEntryType(IStrategoAppl entry) {
        if(isDefData(entry)) {
            return entry.getSubterm(DEFDATA_TYPE);
        } else {
            return null;
        }
    }

    public IStrategoTerm getEntryIdentifier(IStrategoAppl entry) {
        IStrategoTerm result = entry.getSubterm(0);
        if(isURI(result)) {
            return result;
        } else {
            throw new IllegalArgumentException("Illegal index entry: " + entry
                + ". First subterm should be a list representing the key of the entry"
                + ", or an application containing a list.");
        }
    }

    public IStrategoTerm getEntryValue(IStrategoAppl entry) {
        if(isDefData(entry)) {
            return entry.getSubterm(DEFDATA_VALUE);
        } else if(entry.getSubtermCount() == 2) {
            return entry.getSubterm(1);
        } else if(entry.getSubtermCount() == 1) {
            return null;
        } else {
            int termsToCopy = entry.getSubtermCount() - 1;
            IStrategoTerm[] terms = new IStrategoTerm[termsToCopy];
            System.arraycopy(entry.getAllSubterms(), 1, terms, 0, termsToCopy);
            return termFactory.makeTuple(terms);
        }
    }
}
