package org.spoofax.interpreter.library.language;

import java.util.Collection;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexEntry {

	private IStrategoTerm contents;
	
	private SemanticIndexURI uri;
	
	private SemanticIndexFileDescriptor fileDescriptor;
	
	private transient IStrategoAppl term;

	protected SemanticIndexEntry(IStrategoTerm contents, SemanticIndexURI uri, 
			SemanticIndexFileDescriptor fileDescriptor) {
		this.contents = contents;
		this.uri = uri;
		this.fileDescriptor = fileDescriptor;
		
		assert contents != null || uri.getConstructor().getArity() < 2 : "Contents can't be null for Use/2 or DefData/3";
	}
	
	public IStrategoTerm getContents() {
		return contents;
	}
	
	public SemanticIndexURI getURI() {
		return uri;
	}
	
	public SemanticIndexFileDescriptor getFileDescriptor() {
		return fileDescriptor;
	}
	
	/**
	 * Returns a term representation of this entry.
	 */
	public IStrategoAppl toTerm(ITermFactory factory) {
		if (term != null)
			return term;
		
		term = uri.toTerm(factory, contents);
		
		return forceImploderAttachment(term);
	}
	
	/**
	 * Returns a list with representations of given entries.
	 */
	public static IStrategoList toTerms(ITermFactory factory, Collection<SemanticIndexEntry> entries) {
		IStrategoList results = factory.makeList();
		for (SemanticIndexEntry entry : entries) {
			results = factory.makeListCons(entry.toTerm(factory), results);
		}
		return results;
	}
	
	/**
	 * Force an imploder attachment for a term.
	 * This ensures that there is always some form of position info,
	 * and makes sure that origin info is not added to the term.
	 * (The latter would be bad since we cache in {@link #term}.)
	 */
	private IStrategoAppl forceImploderAttachment(IStrategoAppl term) {
		ImploderAttachment attach = ImploderAttachment.get(uri.getId());
		if (attach != null) {
			ImploderAttachment.putImploderAttachment(term, false, attach.getSort(), attach.getLeftToken(), attach.getRightToken());
		} else {
			String fn = fileDescriptor == null ? null : fileDescriptor.getURI().getPath();
			attach = ImploderAttachment.createCompactPositionAttachment(fn, 0, 0, 0, -1);
			term.putAttachment(attach);
		}
		return term;
	}
	
	@Override
	public String toString() {
		String result = uri.toString();
		if (contents != null) result += "," + contents; 
		return result + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contents == null) ? 0 : contents.hashCode());
		result = prime * result
				+ ((fileDescriptor == null) ? 0 : fileDescriptor.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SemanticIndexEntry))
			return false;
		SemanticIndexEntry other = (SemanticIndexEntry) obj;
		if (contents == null) {
			if (other.contents != null)
				return false;
		} else if (!contents.equals(other.contents))
			return false;
		if (fileDescriptor == null) {
			if (other.fileDescriptor != null)
				return false;
		} else if (!fileDescriptor.equals(other.fileDescriptor))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
