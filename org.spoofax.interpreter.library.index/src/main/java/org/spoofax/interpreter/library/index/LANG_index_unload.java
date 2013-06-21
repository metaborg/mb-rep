package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.isTermString;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_unload extends AbstractPrimitive {
	private static String NAME = "LANG_index_unload";

	public LANG_index_unload() {
		super(NAME, 0, 1);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if(isTermString(tvars[0])) {
			IStrategoString projectPath = (IStrategoString) tvars[0];
			IOAgent agent = SSLLibrary.instance(env).getIOAgent();
			IndexManager.getInstance().unloadIndex(projectPath.stringValue(), agent);
			return true;
		} else {
			return false;
		}
	}
}
