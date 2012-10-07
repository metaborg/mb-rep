package org.spoofax.interpreter.library.language;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Gabri�l Konat
 */
public class SemanticIndexURI implements Serializable {

  private static final long serialVersionUID = 1619836759792533807L;

  private IStrategoConstructor constructor;

	private IStrategoTerm namespace;

	private IStrategoList id;

	private IStrategoTerm contentsType;

	private transient IStrategoAppl term;

	/**
	 * @param namespace
	 *            The namespace of the entry, e.g., 'Foo()'
	 * @param id
	 *            The identifier of the entry, e.g., '["foo", Foo()]'
	 */
	protected SemanticIndexURI(IStrategoConstructor constructor,
			IStrategoTerm namespace, IStrategoList id,
			IStrategoTerm contentsType) {
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
		if (id.size() > 0)
			return new SemanticIndexURI(constructor, namespace, id.tail(),
					contentsType);
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
			term = factory.makeAppl(constructor, namespaceId, contentsType,
					contents);
		} else if (constructor.getArity() == 2) {
			term = factory.makeAppl(constructor, namespaceId, contents);
		} else {
			term = factory.makeAppl(constructor, namespaceId);
		}

		return term;
	}

	@Override
	public String toString() {
		String result = constructor.getName() + "([" + namespace + "|" + id
				+ "]";
		if (contentsType != null)
			result += "," + contentsType;
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constructor == null) ? 0 : constructor.hashCode());
		result = prime * result
				+ ((contentsType == null) ? 0 : contentsType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((namespace == null) ? 0 : namespace.hashCode());
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
		if (constructor == null) {
			if (other.constructor != null)
				return false;
		} else if (!constructor.equals(other.constructor))
			return false;
		if (contentsType == null) {
			if (other.contentsType != null)
				return false;
		} else if (!contentsType.equals(other.contentsType))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}
}
