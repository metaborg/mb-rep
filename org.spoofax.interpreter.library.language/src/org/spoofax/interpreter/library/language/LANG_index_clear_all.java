package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_clear_all extends AbstractPrimitive {

	private static String NAME = "LANG_index_clear_all";
	
	private final SemanticIndex index;
	
	public LANG_index_clear_all(SemanticIndex index) {
		super(NAME, 0, 0);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		index.clear();
		return true;
	}
}
