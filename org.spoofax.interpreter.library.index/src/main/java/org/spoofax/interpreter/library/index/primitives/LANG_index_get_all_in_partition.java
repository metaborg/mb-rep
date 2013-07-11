package org.spoofax.interpreter.library.index.primitives;

import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.index.IndexPartitionDescriptor;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_get_all_in_partition extends AbstractPrimitive {
	private static String NAME = "LANG_index_get_all_in_partition";

	public LANG_index_get_all_in_partition() {
		super(NAME, 0, 1);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if(isTermTuple(tvars[0]) || isTermString(tvars[0])) {
			final IIndex ind = IndexManager.getInstance().getCurrent();
			final IndexPartitionDescriptor partitionDescriptor = ind.getPartitionDescriptor(tvars[0]);
			final Iterable<IndexEntry> results = ind.getInPartition(partitionDescriptor);
			env.setCurrent(IndexEntry.toTerms(env.getFactory(), results));
			return true;
		} else {
			return false;
		}
	}
}
