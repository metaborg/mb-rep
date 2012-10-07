package org.spoofax.interpreter.library.language;

import java.util.Date;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class SemanticIndexFile {
	private final SemanticIndexFileDescriptor descriptor;
	
	private Date time;
	
	private long revision;
	
	protected SemanticIndexFile(SemanticIndexFileDescriptor descriptor, Date time) {
		this.descriptor = descriptor;
		this.time = time;
	}
	
	public SemanticIndexFileDescriptor getDescriptor() {
		return descriptor;
	}
	
	public Date getTime() {
		return time;
	}
	
	public long getRevision() {
		return revision;
	}
	
	public void setTimeRevision(Date time, long revision) {
		this.time = time;
		this.revision = revision;
	}
 	
	public IStrategoTerm toTerm(ITermFactory factory) {
		return descriptor.toTerm(factory);
	}
	
	@Override
	public String toString() {
		return descriptor.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SemanticIndexFile) {
			if (obj == this) return true;
			SemanticIndexFile other = (SemanticIndexFile) obj;
			return other.descriptor.equals(descriptor);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + descriptor.hashCode();
		return result;
	}
}
