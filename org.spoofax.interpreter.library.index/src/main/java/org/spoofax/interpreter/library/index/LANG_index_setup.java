package org.spoofax.interpreter.library.index;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_setup extends AbstractPrimitive {
	private static String NAME = "LANG_index_setup";

	public LANG_index_setup() {
		super(NAME, 0, 3);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		final IStrategoString language = (IStrategoString) tvars[0];
		final IStrategoTerm projectPaths = tvars[1];
		final IStrategoTerm partitionTerm = tvars[2];

		final String projectPath;
		if(projectPaths.getSubtermCount() == 0) {
			projectPath = ((IStrategoString) projectPaths).stringValue();
		} else if(projectPaths.getSubtermCount() == 1) {
			projectPath = ((IStrategoString) projectPaths.getSubterm(0)).stringValue();
		} else {
			throw new NotImplementedException("Multiple project paths");
		}

		final IOAgent agent = SSLLibrary.instance(env).getIOAgent();
		final IndexPartitionDescriptor partition = IndexPartitionDescriptor.fromTerm(agent, partitionTerm);
		final IndexManager indexManager = IndexManager.getInstance();
		indexManager.loadIndex(projectPath, language.stringValue(), env.getFactory(), agent);
		indexManager.setCurrentPartition(partition);
		return true;
	}
}
