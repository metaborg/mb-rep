package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_setup extends AbstractPrimitive {
	private static String NAME = "LANG_index_setup";

	public LANG_index_setup() {
		super(NAME, 0, 2);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		final IOAgent agent = SSLLibrary.instance(env).getIOAgent();
		final IndexManager indexManager = IndexManager.getInstance();
		final IStrategoString language = (IStrategoString) tvars[0];
		final IStrategoString projectPath = (IStrategoString) tvars[1];

		indexManager.loadIndex(projectPath.stringValue(), language.stringValue(), env.getFactory(), agent);
		return true;
	}
}
