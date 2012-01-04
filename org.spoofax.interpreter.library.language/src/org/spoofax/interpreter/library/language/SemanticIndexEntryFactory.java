package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermList;

import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.attachments.TermAttachmentStripper;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexEntryFactory {

	private final ITermFactory termFactory;

	private final TermAttachmentStripper stripper;

	private final IStrategoConstructor defCon;

	private final IStrategoConstructor useCon;

	private final IStrategoConstructor defDataCon;

	private final IStrategoConstructor badDefCon;

	private final IStrategoConstructor badUseCon;

	public SemanticIndexEntryFactory(ITermFactory termFactory) {
		this.termFactory = termFactory;
		this.stripper = new TermAttachmentStripper(termFactory);
		defCon = termFactory.makeConstructor("Def", 1);
		useCon = termFactory.makeConstructor("Use", 1);
		defDataCon = termFactory.makeConstructor("DefData", 3);
		badDefCon = termFactory.makeConstructor("BadDef", 1);
		badUseCon = termFactory.makeConstructor("BadUse", 1);
	}
	
	public ITermFactory getTermFactory() {
		return termFactory;
	}
	
	public IStrategoConstructor getDefCon() {
		return defCon;
	}
	
	public IStrategoConstructor getDefDataCon() {
		return defDataCon;
	}
	
	public SemanticIndexEntry createEntry(IStrategoAppl entryTerm, SemanticIndexEntryParent parent, URI file) {
		return createEntry(getEntryType(entryTerm), getEntryNamespace(entryTerm), getEntryId(entryTerm), getEntryData(entryTerm), parent, file);
	}
	
	public SemanticIndexEntry createEntry(IStrategoTerm type, IStrategoTerm namespace, IStrategoList id, IStrategoTerm data,
			SemanticIndexEntryParent parent, URI file) {
		
		ImploderAttachment idAttachment = ImploderAttachment.getCompactPositionAttachment(id, true);
		ImploderAttachment dataAttachment =
			data == null ? null : ImploderAttachment.getCompactPositionAttachment(data, false);
		
		id = createSanitizedId(id, parent);
		data = stripper.strip(data);
		type = stripper.strip(type);
		assert namespace == stripper.strip(namespace);
		
		id.putAttachment(idAttachment);
		if (data != null)
			data.putAttachment(dataAttachment);

		return new SemanticIndexEntry(type, namespace, id, data, file);
	}

	private IStrategoList createSanitizedId(IStrategoList id, SemanticIndexEntryParent parent) {
		if (parent != null) {
			// Share the parent's identifier prefix for efficiency
			return termFactory.makeListCons(stripper.strip(id.head()), parent.getId());
		} else {
			return (IStrategoList) stripper.strip(id);
		}
	}
	
	public SemanticIndexEntryParent createEntryParent(IStrategoTerm namespace, IStrategoList id, SemanticIndexEntryParent parent) {
		assert namespace == stripper.strip(namespace);
		id = createSanitizedId(id, parent);
		return new SemanticIndexEntryParent(namespace, id);
	}
	
	public IStrategoTerm getEntryType(IStrategoAppl entry) {
		IStrategoConstructor type = entry.getConstructor();
		if (type == defCon || type == badDefCon) {
			return defCon;
		} else if (type == useCon || type == badUseCon) {
			return useCon;
		} else if (type == defDataCon) {
			return entry.getSubterm(1);
		} else {
			throw new IllegalArgumentException("Illegal index entry: " + entry);
		}
	}
	
	public IStrategoList getEntryId(IStrategoAppl entry) {
		IStrategoTerm result = entry.getSubterm(0);
		if (isTermList(result)) {
			IStrategoList full = (IStrategoList) result;
			return full.isEmpty() ? full : full.tail();
		} else {
			throw new IllegalArgumentException("Illegal index entry: " + entry);
		}
	}
	
	public IStrategoTerm getEntryNamespace(IStrategoAppl entry) {
		IStrategoTerm result = entry.getSubterm(0);
		if (isTermList(result)) {
			IStrategoList full = (IStrategoList) result;
			return stripper.strip(full.isEmpty() ? full : full.head());
		} else {
			throw new IllegalArgumentException("Illegal index entry: " + entry);
		}
	}
	
	public IStrategoTerm getEntryData(IStrategoAppl entry) {
		if (entry.getSubtermCount() == 3) {
			return entry.getSubterm(2);
		} else {
			assert entry.getSubtermCount() < 3;
			return null;
		}
	}
}
