package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_stop_collection extends AbstractPrimitive {
	private static String NAME = "LANG_index_stop_collection";

	private final IndexManager index;

	public LANG_index_stop_collection(IndexManager index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
		env.setCurrent(index.getCurrent().stopCollection());
		return true;
	}
}
