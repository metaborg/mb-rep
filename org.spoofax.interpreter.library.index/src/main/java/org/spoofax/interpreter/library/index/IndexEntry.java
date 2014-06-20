package org.spoofax.interpreter.library.index;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

/**
 * A key-value pair that can be stored in an {@link IIndex}.
 */
public class IndexEntry implements Serializable {
	private static final long serialVersionUID = -1073077973341978805L;

	public final IStrategoTerm key;
	public final IStrategoTerm value;
	public final IStrategoTerm source;
	public final ImploderAttachment origin;

	public IndexEntry(IStrategoTerm key, IStrategoTerm value, IStrategoTerm source, ImploderAttachment origin) {
		this.key = key;
		this.value = value;
		this.source = source;
		this.origin = origin;
	}

	public IndexEntry(IStrategoTerm key, IStrategoTerm source, ImploderAttachment origin) {
		this.key = key;
		this.value = key;
		this.source = source;
		this.origin = origin;
	}

	@Override
	public String toString() {
		if(key.match(value))
			return key.toString();
		return key.toString() + " : " + value.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key.hashCode();
		result = prime * result + value.hashCode();
		result = prime * result + source.hashCode();
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;

		IndexEntry other = (IndexEntry) obj;

		if(!key.equals(other.key))
			return false;

		if(!value.equals(other.value))
			return false;

		if(!source.equals(other.source))
			return false;

		if(origin == null) {
			if(other.origin != null)
				return false;
		} else if(!origin.equals(other.origin))
			return false;

		return true;
	}
}
