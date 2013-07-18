package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_recover extends AbstractPrimitive {
	private static String NAME = "LANG_index_recover";

	public LANG_index_recover() {
		super(NAME, 0, 0);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		IndexManager.getInstance().popToRootIndex();
		return true;
	}
}
