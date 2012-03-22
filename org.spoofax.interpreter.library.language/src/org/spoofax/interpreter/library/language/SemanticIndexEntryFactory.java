package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermList;

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
	
	@SuppressWarnings("unused")
	private static final int DEFDATA_URI = 0;
	
	private static final int DEFDATA_TYPE = 1;
	
	private static final int DEFDATA_CONTENTS = 2;

	private final ITermFactory termFactory;

	private final TermAttachmentStripper stripper;

	private final IStrategoConstructor defCon;

	private final IStrategoConstructor defDataCon;

	public SemanticIndexEntryFactory(ITermFactory termFactory) {
		this.termFactory = termFactory;
		this.stripper = new TermAttachmentStripper(termFactory);
		defCon = termFactory.makeConstructor("Def", 1);
		defDataCon = termFactory.makeConstructor("DefData", 3);
	}
	
	public ITermFactory getTermFactory() {
		return termFactory;
	}
	
	public IStrategoConstructor getDefDataCon() {
		return defDataCon;
	}
	
	public IStrategoConstructor getDefCon() {
		return defCon;
	}
	
	public SemanticIndexURI createURI(IStrategoConstructor constructor,
			IStrategoTerm namespace, IStrategoList id, IStrategoTerm contentsType) {
		ImploderAttachment idAttachment = ImploderAttachment.getCompactPositionAttachment(id, true);
		contentsType = stripper.strip(contentsType);
		assert namespace == stripper.strip(namespace);
		
		id.putAttachment(idAttachment);
		
		return new SemanticIndexURI(constructor, namespace, id, contentsType);
	}
	
	public SemanticIndexURI createURIFromTemplate(IStrategoAppl template) {
		return createURI(template.getConstructor(), getEntryNamespace(template), getEntryId(template), 
				getEntryContentsType(template));
	}
	
	public SemanticIndexEntry createEntry(IStrategoConstructor constructor,
			IStrategoTerm namespace, IStrategoList id, IStrategoTerm contentsType, IStrategoTerm contents,
			SemanticIndexFile file) {
		return createEntry(contents, createURI(constructor, namespace, id, contentsType), file);
	}
	
	public SemanticIndexEntry createEntry(IStrategoTerm contents, SemanticIndexURI uri, SemanticIndexFile file) {
		
		ImploderAttachment dataAttachment =
			contents == null ? null : ImploderAttachment.getCompactPositionAttachment(contents, false);
		contents = stripper.strip(contents);
		if (contents != null)
			contents.putAttachment(dataAttachment);

		return new SemanticIndexEntry(contents, uri, file);
	}

	/*private IStrategoList createSanitizedId(IStrategoList id) {
		return (IStrategoList) stripper.strip(id);
	}*/
	
	public IStrategoTerm getEntryContentsType(IStrategoAppl entry) {
		IStrategoConstructor type = entry.getConstructor();
		if (type == defDataCon) {
			return entry.getSubterm(DEFDATA_TYPE);
		} else {
			return null;
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
	
	public IStrategoTerm getEntryContents(IStrategoAppl entry) {
		if (entry.getSubtermCount() == 3) {
			return entry.getSubterm(DEFDATA_CONTENTS);
		} else if (entry.getSubtermCount() == 2) {
			return entry.getSubterm(1);
		} else {
			assert entry.getSubtermCount() < 3;
			return null;
		}
	}
}
