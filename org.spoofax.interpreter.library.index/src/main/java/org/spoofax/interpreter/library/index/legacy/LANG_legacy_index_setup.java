package org.spoofax.interpreter.library.index.legacy;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_legacy_index_setup extends AbstractPrimitive {
	private static String NAME = "LANG_index_setup";

	public LANG_legacy_index_setup() {
		super(NAME, 0, 3);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		final IOAgent agent = SSLLibrary.instance(env).getIOAgent();
		final IndexManager indexManager = IndexManager.getInstance();
		final IStrategoString language = (IStrategoString) tvars[0];
		final IStrategoTerm projectPaths = tvars[1];
		IStrategoString projectPath;
		if(Tools.isTermList(projectPaths)) {
			projectPath = (IStrategoString) projectPaths.getSubterm(0);
		} else {
			projectPath = (IStrategoString) projectPaths;
		}

		indexManager.loadIndex(projectPath.stringValue(), language.stringValue(), env.getFactory(), agent);
		return true;
	}
}
