package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_start_collection extends AbstractPrimitive {
	private static String NAME = "LANG_index_start_collection";

	private final IndexManager index;

	public LANG_index_start_collection(IndexManager index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
		if(isTermTuple(tvars[0]) || isTermString(tvars[0])) {
			IIndex ind = index.getCurrent();
			IndexPartitionDescriptor partitionDescriptor = ind.getPartitionDescriptor(tvars[0]);
			index.getCurrent().startCollection(partitionDescriptor);
			return true;
		} else {
			return false;
		}
	}
}
