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
		ImploderAttachment origin = ImploderAttachment.get(value);

		// TODO: what is the performance of attachment stripping operations?
		key = stripper.strip(key);
		value = stripper.strip(value);
		if(origin != null)
			value.putAttachment(origin);

		final IndexEntry entry = new IndexEntry(key, value, source, origin);

		return entry;
	}

	public IndexEntry create(IStrategoTerm key, IStrategoTerm source) {
		ImploderAttachment origin = ImploderAttachment.get(key);

		// TODO: what is the performance of attachment stripping operations?
		key = stripper.strip(key);
		if(origin != null)
			key.putAttachment(origin);

		final IndexEntry entry = new IndexEntry(key, source, origin);

		return entry;
	}

	public IStrategoTerm toTerm(IndexEntry entry) {
		return factory.makeTuple(entry.key, entry.value, entry.source,
			ImploderAttachment.TYPE.toTerm(factory, entry.origin));
	}

	public IStrategoList toValueTerms(Iterable<IndexEntry> entries) {
		IStrategoList list = factory.makeList();
		for(IndexEntry entry : entries) {
			list = factory.makeListCons(entry.value, list);
		}
		return list;
	}

	public IndexEntry fromTerm(IStrategoTerm term) {
		final IStrategoTerm key = term.getSubterm(0);
		final IStrategoTerm value = term.getSubterm(1);
		final IStrategoTerm source = term.getSubterm(2);
		final ImploderAttachment origin = ImploderAttachment.TYPE.fromTerm((IStrategoAppl) term.getSubterm(3));

		return new IndexEntry(key, value, source, origin);
	}
}
