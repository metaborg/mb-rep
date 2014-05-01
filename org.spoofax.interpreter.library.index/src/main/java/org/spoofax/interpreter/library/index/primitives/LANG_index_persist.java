package org.spoofax.interpreter.library.index.primitives;

import java.io.IOException;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_persist extends AbstractPrimitive {
	private static String NAME = "LANG_index_persist";

	public LANG_index_persist() {
		super(NAME, 0, 0);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		try {
			IndexManager.getInstance().writeCurrent(env.getFactory());
		} catch(IOException e) {
			throw new RuntimeException("Failed to persist index.", e);
		}
		return true;
	}
}
