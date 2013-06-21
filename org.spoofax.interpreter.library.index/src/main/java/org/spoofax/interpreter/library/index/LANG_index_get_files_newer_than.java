package org.spoofax.interpreter.library.index;

import java.util.Date;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * Gets all partitions newer than (or equally old as) the specified partition, or gets all partitions if no partition
 * with the given name exists.
 */
public class LANG_index_get_files_newer_than extends AbstractPrimitive {
	private static String NAME = "LANG_index_get_files_newer_than";

	public LANG_index_get_files_newer_than() {
		super(NAME, 0, 1);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		IndexManager indexManager = IndexManager.getInstance();
		IIndex ind = indexManager.getCurrent();
		IndexPartition partition = ind.getPartition(ind.getPartitionDescriptor(tvars[0]));
		if(partition == null || partition.getTime() == null) {
			env.setCurrent(LANG_index_get_all_files.getAllPartitions(indexManager.getCurrent(), env.getFactory()));
		} else {
			Date time = partition.getTime();
			env.setCurrent(getPartitionsAfter(env.getFactory(), ind, time));
		}
		return true;
	}

	private static IStrategoList getPartitionsAfter(ITermFactory factory, IIndex ind, Date time) {
		IStrategoList results = factory.makeList();
		for(IndexPartition partition : ind.getAllPartitions()) {
			if(partition.getTime() != null && !partition.getTime().before(time)) {
				results = factory.makeListCons(partition.toTerm(factory), results);
			}
		}
		return results;
	}
}
