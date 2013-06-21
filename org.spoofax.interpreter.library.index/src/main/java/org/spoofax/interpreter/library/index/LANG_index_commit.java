package org.spoofax.interpreter.library.index;

import java.io.IOException;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_commit extends AbstractPrimitive {
	private static String NAME = "LANG_index_commit";

	public LANG_index_commit() {
		super(NAME, 0, 0);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		try {
			IndexManager.getInstance().storeCurrent(env.getFactory());
		} catch(IOException e) {
			throw new RuntimeException("Failed to store index.", e);
		}
		return true;
	}
}
