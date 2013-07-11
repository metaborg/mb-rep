package org.spoofax.interpreter.library.index.primitives;

import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.index.IndexPartitionDescriptor;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_set_current_partition extends AbstractPrimitive {
	private static String NAME = "LANG_index_set_current_partition";

	public LANG_index_set_current_partition() {
		super(NAME, 0, 1);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if(isTermTuple(tvars[0]) || isTermString(tvars[0])) {
			final IndexManager indexManager = IndexManager.getInstance();
			final IIndex ind = indexManager.getCurrent();
			final IndexPartitionDescriptor partitionDescriptor = ind.getPartitionDescriptor(tvars[0]);
			indexManager.setCurrentPartition(partitionDescriptor);
			return true;
		} else {
			return false;
		}
	}
}
