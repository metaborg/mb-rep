package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_clear_file extends AbstractPrimitive {
	private static String NAME = "LANG_index_clear_file";

	public LANG_index_clear_file() {
		super(NAME, 0, 1);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if(isTermString(tvars[0]) || isTermTuple(tvars[0])) {
			IIndex ind = IndexManager.getInstance().getCurrent();
			ind.clearPartition(tvars[0]);
			return true;
		} else {
			return false;
		}
	}
}
