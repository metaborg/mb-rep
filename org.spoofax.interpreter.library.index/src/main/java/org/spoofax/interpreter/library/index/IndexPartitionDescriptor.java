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
 * Describes a partition consisting of a file URI and an optional partition identifier.
 */
public class IndexPartitionDescriptor implements Serializable {
	private static final long serialVersionUID = -5167366407344668956L;

	private final URI uri;
	private final IStrategoTerm partition;

	private transient IStrategoTerm cachedTerm;
	private transient String cachedString;

	public IndexPartitionDescriptor(URI uri, IStrategoTerm subpartition) {
		this.uri = uri;
		if(subpartition == null || (isTermList(subpartition) && ((IStrategoList) subpartition).isEmpty()))
			this.partition = null;
		else
			this.partition = subpartition;
	}

	public URI getURI() {
		return uri;
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
	public static IStrategoList toTerms(ITermFactory factory, Iterable<IndexPartitionDescriptor> partitionDescriptors) {
		IStrategoList results = factory.makeList();
		for(IndexPartitionDescriptor entry : partitionDescriptors) {
			results = factory.makeListCons(entry.toTerm(factory), results);
		}
		return results;
	}

	/**
	 * Converts a term partition representation to a IndexPartitionDescriptor, using the {@link IOAgent} to create an
	 * absolute path.
	 * 
	 * @param agent The agent that provides the current path and partition system access, or null if the path should be
	 *            used as-is.
	 * @param term A string or (string, string) tuple with the file name or the file name and partition identifier.
	 */
	public static IndexPartitionDescriptor fromTerm(IOAgent agent, IStrategoTerm term) {
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
		return new IndexPartitionDescriptor(file.toURI(), subpartition);
	}

	@Override
	public String toString() {
		if(cachedString != null)
			return cachedString;

		cachedString =
			"file".equals(uri.getScheme()) ? new File(uri).getAbsolutePath().replace("\\", "/") : uri.toString();

		return cachedString;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IndexPartitionDescriptor) {
			if(obj == this)
				return true;
			IndexPartitionDescriptor other = (IndexPartitionDescriptor) obj;
			return other.uri.equals(uri)
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
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}
}
