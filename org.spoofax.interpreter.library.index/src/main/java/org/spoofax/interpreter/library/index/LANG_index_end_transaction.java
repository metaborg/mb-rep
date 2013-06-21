package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_end_transaction extends AbstractPrimitive {
	private static String NAME = "LANG_index_end_transaction";

	public LANG_index_end_transaction() {
		super(NAME, 0, 0);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
		IndexManager.getInstance().endTransaction();
		return true;
	}
}
