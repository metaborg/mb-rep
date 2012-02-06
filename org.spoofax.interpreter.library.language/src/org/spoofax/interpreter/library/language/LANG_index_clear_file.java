package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_clear_file extends AbstractPrimitive {

	private static String NAME = "LANG_index_clear_file";
	
	private final SemanticIndexManager index;
	
	public LANG_index_clear_file(SemanticIndexManager index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if (index.isInitialized() && (isTermString(tvars[0]) || isTermTuple(tvars[0]))) {
			SemanticIndex ind = index.getCurrent();
			ind.clear(ind.getFile(tvars[0]));
			return true;
		} else {
			return false;
		}
	}
}
