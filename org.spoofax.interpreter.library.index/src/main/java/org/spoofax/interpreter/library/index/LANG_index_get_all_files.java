package org.spoofax.interpreter.library.index;

import java.util.Collection;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class LANG_index_get_all_files extends AbstractPrimitive {
	private static String NAME = "LANG_index_all_files";

	public LANG_index_get_all_files() {
		super(NAME, 0, 0);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		ITermFactory factory = env.getFactory();
		IStrategoList results = getAllPartitions(IndexManager.getInstance().getCurrent(), factory);
		System.out.println(results.toString());
		env.setCurrent(results);
		return true;
	}

	public static IStrategoList getAllPartitions(IIndex index, ITermFactory factory) {
		Collection<IndexPartitionDescriptor> allPartitionDescriptors = index.getAllPartitionDescriptors();
		IStrategoList results = factory.makeList();
		for(IndexPartitionDescriptor partitionDescriptor : allPartitionDescriptors) {
			results = factory.makeListCons(partitionDescriptor.toTerm(factory), results);
		}
		return results;
	}
}
