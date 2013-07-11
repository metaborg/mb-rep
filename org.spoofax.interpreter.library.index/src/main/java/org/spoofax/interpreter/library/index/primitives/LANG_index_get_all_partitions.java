package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.index.IndexPartitionDescriptor;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class LANG_index_get_all_partitions extends AbstractPrimitive {
	private static String NAME = "LANG_index_all_partitions";

	public LANG_index_get_all_partitions() {
		super(NAME, 0, 0);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		final ITermFactory factory = env.getFactory();
		final IStrategoList results = getAllPartitions(IndexManager.getInstance().getCurrent(), factory);
		env.setCurrent(results);
		return true;
	}

	public static IStrategoList getAllPartitions(IIndex index, ITermFactory factory) {
		final Iterable<IndexPartitionDescriptor> allPartitionDescriptors = index.getAllPartitionDescriptors();
		IStrategoList results = factory.makeList();
		for(IndexPartitionDescriptor partitionDescriptor : allPartitionDescriptors) {
			results = factory.makeListCons(partitionDescriptor.toTerm(factory), results);
		}
		return results;
	}
}
