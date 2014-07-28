package org.spoofax.interpreter.library.index.legacy;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class NoopAbstractPrimitive extends AbstractPrimitive {
	public NoopAbstractPrimitive(String name, int svars, int tvars) {
		super(name, svars, tvars);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
		return true;
	}
}
