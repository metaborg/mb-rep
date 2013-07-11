package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.TermAttachmentStripper;

public class IndexEntryFactory {
    private static final int DATA_TYPE_POS = 1;
    private static final int DATA_VALUE_POS = 2;
    private static final IStrategoConstructor DEFDATA_CONSTRUCTOR = new TermFactory().makeConstructor("DefData", 3);
    private static final IStrategoConstructor PROP_CONSTRUCTOR = new TermFactory().makeConstructor("Prop", 3);

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
    
	public IndexEntry createEntry(IStrategoAppl entry, IndexPartitionDescriptor partitionDescriptor) {
		final IStrategoConstructor constructor = entry.getConstructor();
		final IStrategoTerm type = getEntryType(entry);
		final IStrategoTerm identifier = getEntryIdentifier(entry);
		final IStrategoTerm value = getEntryValue(entry);

		return createEntry(constructor, identifier, type, value, partitionDescriptor);
	}

    public static boolean isData(IStrategoAppl term) {
        return isData(term.getConstructor());
    }

    public static boolean isData(IStrategoConstructor constructor) {
        return constructor.equals(DEFDATA_CONSTRUCTOR) || constructor.equals(PROP_CONSTRUCTOR);
    }

    public IStrategoTerm getEntryType(IStrategoAppl entry) {
        if(isData(entry)) {
            return entry.getSubterm(DATA_TYPE_POS);
        } else {
            return null;
        }
    }

    public IStrategoTerm getEntryIdentifier(IStrategoAppl entry) {
        if(entry.getSubtermCount() > 0) {
            return entry.getSubterm(0);
        } else {
            throw new IllegalArgumentException("Illegal index entry: " + entry
                + ". Entry should contain at least one subterm that identifiers the entry.");
        }
    }

    public IStrategoTerm getEntryValue(IStrategoAppl entry) {
        if(isData(entry)) {
            return entry.getSubterm(DATA_VALUE_POS);
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
