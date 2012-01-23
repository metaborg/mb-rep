package org.spoofax.interpreter.library.language;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * A URI/time stamp container. Only one SemanticIndexFile may exist per URI.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexFile {
	
	public static final String DEFAULT_DESCRIPTOR = "";
	
	private final Set<SemanticIndexEntry> entries = new HashSet<SemanticIndexEntry>();

	private final URI uri;
	
	private final String descriptor;
	
	private Date time;
	
	public URI getURI() {
		return uri;
	}
	
	public Date getTime() {
		return time;
	}
	
	public String getDescriptor() {
		return descriptor;
	}
	
	public Set<SemanticIndexEntry> getEntries() {
		return entries;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
	
	public SemanticIndexFile(URI uri, String descriptor, Date time) {
		this.uri = uri;
		this.descriptor = descriptor;
		this.time = time;
		if (descriptor == null)
			throw new IllegalArgumentException("descriptor can't be null, use DEFAULT_DESCRIPTOR");
	}
	
	@Override
	public String toString() {
		return uri.toString();
	}
	
	public IStrategoTerm toTerm(ITermFactory factory) {
		IStrategoString uriString = factory.makeString(toString());
		return factory.makeTuple(uriString, factory.makeString(descriptor));
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof SemanticIndexFile
			&& ((SemanticIndexFile) obj).uri.equals(uri)
			&& ((SemanticIndexFile) obj).descriptor.equals(descriptor);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descriptor == null) ? 0 : descriptor.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}
}
