package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.attachments.TermAttachmentStripper;

public class IndexEntryFactory {
    private final ITermFactory factory;
    private final TermAttachmentStripper stripper;

    public IndexEntryFactory(ITermFactory termFactory) {
        this.factory = termFactory;
        this.stripper = new TermAttachmentStripper(termFactory);
    }

    public ITermFactory getTermFactory() {
        return factory;
    }

    public IndexEntry create(IStrategoTerm key, IStrategoTerm value, IStrategoTerm source) {
        final ImploderAttachment origin = getImploderAttachment(value);

		// TODO: what is the performance of attachment stripping operations?
		key = stripper.strip(key);
		value = stripper.strip(value);
		if (origin != null) {
			ImploderAttachment.putImploderAttachment(value, false,
					origin.getSort(), origin.getLeftToken(),
					origin.getRightToken(), origin.isBracket(), origin.isCompletion(), origin.isNestedCompletion(), origin.isSinglePlaceholderCompletion());
		}

        final IndexEntry entry = new IndexEntry(key, value, source, origin);

        return entry;
    }

    public IndexEntry create(IStrategoTerm key, IStrategoTerm source) {
        final ImploderAttachment origin = getImploderAttachment(key);

		// TODO: what is the performance of attachment stripping operations?
		key = stripper.strip(key);
		if (origin != null) {
			ImploderAttachment.putImploderAttachment(key, false,
					origin.getSort(), origin.getLeftToken(),
					origin.getRightToken(), origin.isBracket(), origin.isCompletion(), origin.isNestedCompletion(), origin.isSinglePlaceholderCompletion());
		}

        final IndexEntry entry = new IndexEntry(key, source, origin);

        return entry;
    }

	private ImploderAttachment getImploderAttachment(IStrategoTerm term) {
		final IStrategoTerm termWithImploder = ImploderAttachment.getImploderOrigin(term);
		if(termWithImploder == null)
			return null;
		return ImploderAttachment.get(termWithImploder);
	}

    public IStrategoTerm toPair(IndexEntry entry) {
        return factory.makeTuple(entry.key, entry.value);
    }

	public IStrategoTerm toTerm(IndexEntry entry) {
		if(entry.origin != null)
			return factory.makeTuple(entry.key, entry.value, entry.source,
				ImploderAttachment.TYPE.toTerm(factory, entry.origin));
		else
			return factory.makeTuple(entry.key, entry.value, entry.source);
	}

	public IStrategoList toKeyTerms(Iterable<IndexEntry> entries) {
		IStrategoList list = factory.makeList();
		for(IndexEntry entry : entries) {
			list = factory.makeListCons(entry.key, list);
		}
		return list;
	}

	public IStrategoList toValueTerms(Iterable<IndexEntry> entries) {
		IStrategoList list = factory.makeList();
		for(IndexEntry entry : entries) {
			list = factory.makeListCons(entry.value, list);
		}
		return list;
	}

	public IStrategoList toPairTerms(Iterable<IndexEntry> entries) {
		IStrategoList list = factory.makeList();
		for(IndexEntry entry : entries) {
			list = factory.makeListCons(toPair(entry), list);
		}
		return list;
	}

	public IndexEntry fromTerm(IStrategoTerm term) {
		final IStrategoTerm key = term.getSubterm(0);
		final IStrategoTerm value = term.getSubterm(1);
		final IStrategoTerm source = term.getSubterm(2);
		if(term.getSubtermCount() == 3)
			return new IndexEntry(key, value, source, null);
		final ImploderAttachment origin = ImploderAttachment.TYPE.fromTerm((IStrategoAppl) term.getSubterm(3));
		return new IndexEntry(key, value, source, origin);
	}
}
