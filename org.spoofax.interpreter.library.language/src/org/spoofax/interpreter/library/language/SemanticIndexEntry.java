package org.spoofax.interpreter.library.language;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexEntry {

	private IStrategoTerm type;
	
	private IStrategoList id;

	private IStrategoTerm namespace;

	private IStrategoTerm data;
	
	private URI file;
	
	private List<SemanticIndexEntry> tail = null;
	
	private transient IStrategoAppl term;

	/**
	 * @param namespace The namespace of the entry, e.g., 'Foo()'
	 * @param id        The identifier of the entry, e.g., '["foo", Foo()]'
	 */
	protected SemanticIndexEntry(IStrategoTerm type, IStrategoTerm namespace,
			IStrategoList id, IStrategoTerm data, URI file) {
		this.type = type;
		this.id = id;
		this.namespace = namespace;
		this.data = data;
		this.file = file;
		assert id != null && namespace != null && id != null;
		assert data != null || type instanceof IStrategoConstructor;
	}
	
	public IStrategoTerm getType() {
		return type;
	}
	
	public IStrategoList getId() {
		return id;
	}
	
	public IStrategoTerm getNamespace() {
		return namespace;
	}
	
	public IStrategoTerm getData() {
		return data;
	}
	
	public URI getFile() {
		return file;
	}
	
	public List<SemanticIndexEntry> getTail() {
		if (tail == null)
			return Collections.emptyList();
		else
			return tail;
	}
	
	public boolean isParent() {
		return type == SemanticIndexEntryParent.TYPE;
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
	protected void internalReinit(IStrategoTerm type, IStrategoTerm namespace, IStrategoList id, IStrategoTerm data) {
		this.type = type;
		this.namespace = namespace;
		this.id = id;
		this.data = data;
	}
	
	/**
	 * Returns a term representation of this entry.
	 * (Null for {@link SemanticIndexEntryParent} terms.)
	 */
	public IStrategoAppl toTerm(SemanticIndexEntryFactory factory) {
		if (term != null)
			return term;
		
		ITermFactory terms = factory.getTermFactory();
		IStrategoList namespaceId = terms.makeListCons(namespace, id);
		if (!isDataEntry()) {
			// Def/Use/BadDef/BadUse
			term = terms.makeAppl((IStrategoConstructor) type, namespaceId);
		} else {
			assert !(type instanceof IStrategoConstructor) : "DefData expected";
			term = terms.makeAppl(factory.getDefDataCon(), namespaceId, type, data);
		}
		return forceImploderAttachment(term);
	}

	/**
	 * Determines if this is a data entry.
	 * If it is, the {@link #getType()} determines the data type,
	 * and is a {@link IStrategoTerm}. If it is not,
	 * {@link #getType()} is simply an {@link IStrategoConstructor}
	 * such as 'Def' or 'Use'.
	 */
	private boolean isDataEntry() {
		return data != null;
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
		if (isDataEntry()) {
			return "DefData(" + namespace + "," + id + "," + type + "," + data + ")";
		} else { 
			// Def/Use/BadDef/BadUse
			return ((IStrategoConstructor) type).getName() + "(" + namespace + "," + id + ")";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		result = prime * result + type.hashCode();
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
		if (type != other.type) {
			if (!isDataEntry()) {
				assert type instanceof IStrategoConstructor && !type.equals(other.type);
				return false;
			} else if (!type.match(other.type)) {
				return false;
			}
		}
		if (namespace != other.namespace && !namespace.match(other.namespace))
			return false;
		if (id != other.id && !id.match(other.id))
			return false;
		return true;
	}
}
