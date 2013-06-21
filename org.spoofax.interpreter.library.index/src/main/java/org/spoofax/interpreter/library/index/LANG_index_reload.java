package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_reload extends AbstractPrimitive {
	private static String NAME = "LANG_index_reload";

	public LANG_index_reload() {
		super(NAME, 0, 0);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		final IndexManager indexManager = IndexManager.getInstance();
		indexManager.getCurrent().clearAll();
		NotificationCenter.notifyNewProject(indexManager.getCurrentProject());
		return true;
	}
}
