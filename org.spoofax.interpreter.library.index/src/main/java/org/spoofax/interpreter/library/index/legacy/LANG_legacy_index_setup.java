package org.spoofax.interpreter.library.index.legacy;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.index.IndexPartition;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
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
		final IStrategoList projectPaths = (IStrategoList) tvars[1];
		final IStrategoString projectPath = (IStrategoString)projectPaths.getSubterm(0);
		final IStrategoTerm partitionTerm = tvars[2];
		final IndexPartition partition = IndexPartition.fromTerm(agent, partitionTerm);
		
		indexManager.loadIndex(projectPath.stringValue(), language.stringValue(), env.getFactory(), agent);
		indexManager.setCurrentPartition(partition);
		return true;
	}
}
