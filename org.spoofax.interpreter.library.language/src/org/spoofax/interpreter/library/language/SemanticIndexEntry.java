package org.spoofax.interpreter.library.language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.Term;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexEntry {
	
	private IStrategoConstructor constructor;

	private IStrategoTerm namespace;
	
	private IStrategoList id;

	private IStrategoTerm contentsType;

	private IStrategoTerm contents;
	
	private SemanticIndexFile file;
	
	private List<SemanticIndexEntry> tail = null;
	
	private transient IStrategoAppl term;

	/**
	 * @param namespace The namespace of the entry, e.g., 'Foo()'
	 * @param id        The identifier of the entry, e.g., '["foo", Foo()]'
	 */
	protected SemanticIndexEntry(IStrategoConstructor constructor, IStrategoTerm namespace,
			IStrategoList id, IStrategoTerm contentsType, IStrategoTerm contents, SemanticIndexFile file) {
		this.constructor = constructor;
		this.id = id;
		this.namespace = namespace;
		this.contentsType = contentsType;
		this.contents = contents;
		this.file = file;
		assert constructor != null && id != null && namespace != null;
		assert contents != null || constructor.getArity() < 2 : "Contents can't be null for Use/2 or DefData/3";
		assert contentsType == null || "DefData".equals(constructor.getName()) : "Contents type only expected for DefData";
	}
	
	public IStrategoConstructor getConstructor() {
		return constructor;
	}
	
	public IStrategoTerm getType() {
		return contentsType;
	}
	
	public IStrategoList getId() {
		return id;
	}
	
	public IStrategoTerm getNamespace() {
		return namespace;
	}
	
	public IStrategoTerm getContents() {
		return contents;
	}
	
	public SemanticIndexFile getFile() {
		return file;
	}
	
	public List<SemanticIndexEntry> getTail() {
		if (tail == null)
			return Collections.emptyList();
		else
			return tail;
	}
	
	public boolean isParent() {
		return constructor == SemanticIndexEntryParent.CONSTRUCTOR;
	}
	
	public void setTail(List<SemanticIndexEntry> tail) {
		this.tail = tail;
	}
	
	public void addToTail(SemanticIndexEntry entry) {
		if (tail == null)
			tail = new ArrayList<SemanticIndexEntry>();
		tail.add(entry);
	}
	
	public void removeFromTail(SemanticIndexEntry entry) {
		if (tail == null) return;
		tail.remove(entry);
	}
	
	/**
	 * Reinitialize this template. Used for maintaining a reusable lookup object
	 * in the index.
	 */
	protected void internalReinit(IStrategoConstructor constructor, IStrategoTerm namespace, IStrategoList id, IStrategoTerm contentsType) {
		this.constructor = constructor;
		this.contentsType = contentsType;
		this.namespace = namespace;
		this.id = id;
	}
	
	/**
	 * Returns a term representation of this entry,
	 * ignoring its tail.
	 * (Null for {@link SemanticIndexEntryParent} terms.)
	 */
	public IStrategoAppl toTerm(SemanticIndexEntryFactory factory) {
		if (term != null)
			return term;
		
		ITermFactory terms = factory.getTermFactory();
		IStrategoList namespaceId = terms.makeListCons(namespace, id);
		if (constructor.getArity() == 3) {
			term = terms.makeAppl(constructor, namespaceId, contentsType, contents);
		} else if (constructor.getArity() == 2) {
			term = terms.makeAppl(constructor, namespaceId, contents);
		} else {
			term = terms.makeAppl(constructor, namespaceId);
		}
		return forceImploderAttachment(term);
	}

	/**
	 * Returns a term representation of this entry and its tail as a list.
	 * (Null for {@link SemanticIndexEntryParent} terms.)
	 */
	public final IStrategoList toTerms(SemanticIndexEntryFactory factory) {
		IStrategoList results = factory.getTermFactory().makeList();
		return toTerms(factory, results);
	}

	protected IStrategoList toTerms(SemanticIndexEntryFactory factory, IStrategoList results) {
		ITermFactory termFactory = factory.getTermFactory();
		IStrategoAppl result = toTerm(factory);
		results = termFactory.makeListCons(result, results);
		List<SemanticIndexEntry> tail = getTail();
		
		for (int i = 0, max = tail.size(); i < max; i++) {
			result = tail.get(i).toTerm(factory);
			results = termFactory.makeListCons(result, results);
		}
		
		return results;
	}
	
	/**
	 * Returns a term representation of these entries their tails as a list.
	 * (Null for {@link SemanticIndexEntryParent} terms.)
	 */
	public static IStrategoList toTerms(SemanticIndexEntryFactory factory, Iterable<SemanticIndexEntry> entries) {
		IStrategoList results = factory.getTermFactory().makeList();
		for (SemanticIndexEntry entry : entries) {
			results = entry.toTerms(factory, results);
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
		ImploderAttachment attach = ImploderAttachment.get(id);
		if (attach != null) {
			ImploderAttachment.putImploderAttachment(term, false, attach.getSort(), attach.getLeftToken(), attach.getRightToken());
		} else {
			String fn = file == null ? null : file.toString();
			term.putAttachment(ImploderAttachment.createCompactPositionAttachment(
					fn, 0, 0, 0, -1));
		}
		return term;
	}
	
	@Override
	public String toString() {
		String result = constructor.getName() + "([" + namespace + "," + id + "]";
		if (contentsType != null) result += "," + contentsType; 
		if (contents != null) result += "," + contents; 
		return result + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		result = prime * result + (contentsType == null ? 0 : contentsType.hashCode());
		/* Not considered: data is not part of the key, makes it impossible to look up!
		result = prime * result + (contents == null ? 0 : contents.hashCode());
		*/
		result = prime * result + namespace.hashCode();
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
		if (namespace != other.namespace && !namespace.match(other.namespace))
			return false;
		if (contentsType != other.contentsType && contentsType != null && !contentsType.match(other.contentsType))
			return false;
		if (id != other.id && !id.match(other.id))
			return false;
		/* Not considered: data is not part of the key, makes it impossible to look up!
		if (contents != other.contents && contents != null && !contents.match(other.contents))
			return false;
		*/
		return true;
	}
}
