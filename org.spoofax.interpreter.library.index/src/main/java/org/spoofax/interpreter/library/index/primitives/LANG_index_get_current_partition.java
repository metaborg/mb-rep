package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_get_current_partition extends AbstractPrimitive {
	private static String NAME = "LANG_index_get_current_partition";

	public LANG_index_get_current_partition() {
		super(NAME, 0, 0);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		env.setCurrent(IndexManager.getInstance().getCurrentPartition().toTerm(env.getFactory()));
		return true;
	}
}
