package org.spoofax.interpreter.library.index.primitives;

import static org.spoofax.interpreter.core.Tools.isTermAppl;
import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.index.IndexPartition;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_add extends AbstractPrimitive {
	private static String NAME = "LANG_index_add";

	public LANG_index_add() {
		super(NAME, 0, 2);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if(isTermAppl(tvars[0]) && (isTermTuple(tvars[1]) || isTermString(tvars[1]))) {
			final IOAgent agent = SSLLibrary.instance(env).getIOAgent();
			final IIndex ind = IndexManager.getInstance().getCurrent();
			final IStrategoAppl entryTerm = (IStrategoAppl) tvars[0];
			final IndexPartition partition = IndexPartition.fromTerm(agent, tvars[1]);
			final IndexEntry entry = ind.getFactory().createEntry(entryTerm, partition);
			ind.add(entry);
			return true;
		} else {
			return false;
		}
	}
}
