package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.asJavaString;
import static org.spoofax.interpreter.core.Tools.isTermList;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * A partition in an {@link IIndex} that partitions {@link IndexEntry}s.
 */
public class IndexPartition implements Serializable {
	private static final long serialVersionUID = 1326140093669577122L;

	private final URI file;
	private final IStrategoTerm partition;

	private transient IStrategoTerm cachedTerm;
	private transient String cachedString;

	protected IndexPartition(URI uri, IStrategoTerm subpartition) {
		this.file = uri;
		if(subpartition == null || (isTermList(subpartition) && ((IStrategoList) subpartition).isEmpty()))
			this.partition = null;
		else
			this.partition = subpartition;
	}

	public URI getURI() {
		return file;
	}

	public IStrategoTerm getPartition() {
		return partition;
	}

	/**
	 * Returns the term representation.
	 */
	public IStrategoTerm toTerm(ITermFactory factory) {
		if(cachedTerm != null)
			return cachedTerm;

		IStrategoString uriString = factory.makeString(toString());
		cachedTerm = factory.makeTuple(uriString, partition == null ? factory.makeList() : partition);

		return cachedTerm;
	}

	/**
	 * Returns a list with term representations of given partition descriptors.
	 */
	public static IStrategoList toTerms(ITermFactory factory, Iterable<IndexPartition> partitions) {
		IStrategoList results = factory.makeList();
		for(IndexPartition entry : partitions) {
			results = factory.makeListCons(entry.toTerm(factory), results);
		}
		return results;
	}

	/**
	 * Converts a term partition representation to a IndexPartition, using the {@link IOAgent} to create an
	 * absolute path.
	 * 
	 * @param agent The agent that provides the current path and partition system access, or null if the path should be
	 *            used as-is.
	 * @param term A string or (string, string) tuple with the file name or the file name and partition identifier.
	 */
	public static IndexPartition fromTerm(IOAgent agent, IStrategoTerm term) {
		String name;
		IStrategoTerm subpartition;
		if(isTermTuple(term)) {
			name = asJavaString(term.getSubterm(0));
			subpartition = term.getSubterm(1);
		} else {
			name = asJavaString(term);
			subpartition = null;
		}
		File file = new File(name);
		if(!file.isAbsolute() && agent != null)
			file = new File(agent.getWorkingDir(), name);
		return new IndexPartition(file.toURI(), subpartition);
	}

	@Override
	public String toString() {
		if(cachedString != null)
			return cachedString;

		cachedString =
			"file".equals(file.getScheme()) ? new File(file).getAbsolutePath().replace("\\", "/") : file.toString();

		return cachedString;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IndexPartition) {
			if(obj == this)
				return true;
			IndexPartition other = (IndexPartition) obj;
			return other.getURI().equals(file)
				&& (partition == null ? other.partition == null : partition.equals(other.partition));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((partition == null) ? 0 : partition.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}
}
