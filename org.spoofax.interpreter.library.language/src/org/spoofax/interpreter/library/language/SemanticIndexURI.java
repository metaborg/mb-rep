package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Gabri�l Konat
 */
public class SemanticIndexURI {
	private IStrategoConstructor constructor;

	private IStrategoTerm namespace;
	
	private IStrategoList id;
	
	private IStrategoTerm contentsType;
	
	private transient IStrategoAppl term;
	
	/**
	 * @param namespace The namespace of the entry, e.g., 'Foo()'
	 * @param id        The identifier of the entry, e.g., '["foo", Foo()]'
	 */
	protected SemanticIndexURI(IStrategoConstructor constructor, IStrategoTerm namespace, 
			IStrategoList id, IStrategoTerm contentsType) {
		this.constructor = constructor;
		this.id = id;
		this.namespace = namespace;
		this.contentsType = contentsType;
		
		assert constructor != null && id != null && namespace != null;
		assert contentsType == null || "DefData".equals(constructor.getName()) : "Contents type only expected for DefData";
	}
	
	public IStrategoConstructor getConstructor() {
		return constructor;
	}
	
	public IStrategoTerm getNamespace() {
		return namespace;
	}

	public IStrategoList getId() {
		return id;
	}
	
	public IStrategoTerm getType() {
		return contentsType;
	}
	
	public SemanticIndexURI getParent() {
		if(id.size() > 0)
			return new SemanticIndexURI(constructor, namespace, id.tail(), contentsType);
		else
			return null;
	}
	
	/**
	 * Returns a term representation of this entry.
	 */
	public IStrategoAppl toTerm(ITermFactory factory, IStrategoTerm contents) {
		if (term != null)
			return term;
		
		IStrategoList namespaceId = factory.makeListCons(namespace, id);
		if (constructor.getArity() == 3) {
			term = factory.makeAppl(constructor, namespaceId, contentsType, contents);
		} else if (constructor.getArity() == 2) {
			term = factory.makeAppl(constructor, namespaceId, contents);
		} else {
			term = factory.makeAppl(constructor, namespaceId);
		}
		
		return term;
	}

	
	@Override
	public String toString() {
		String result = constructor.getName() + "([" + namespace + "|" + id + "]";
		if (contentsType != null) result += "," + contentsType; 
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		result = prime * result + (contentsType == null ? 0 : contentsType.hashCode());
		result = prime * result + namespace.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SemanticIndexURI))
			return false;
		
		SemanticIndexURI other = (SemanticIndexURI) obj;
		if (constructor != other.constructor && !constructor.match(other.constructor))
			return false;
		if (namespace != other.namespace && !namespace.match(other.namespace))
			return false;
		if (contentsType != other.contentsType && contentsType != null && !contentsType.match(other.contentsType))
			return false;
		if (id != other.id && !id.match(other.id))
			return false;

		return true;
	}
}
