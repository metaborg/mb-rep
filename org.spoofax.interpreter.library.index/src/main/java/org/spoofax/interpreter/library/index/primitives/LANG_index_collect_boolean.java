package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_collect_boolean extends AbstractPrimitive {
	private static String NAME = "LANG_index_collect_boolean";

	public LANG_index_collect_boolean() {
		super(NAME, 0, 1);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		final IIndex index = IndexManager.getInstance().getCurrent();
		final IStrategoTerm key = tvars[0];
		index.collect(key);
		return true;
	}
}
