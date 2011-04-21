package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_retrieve_data extends AbstractPrimitive {

	private static String NAME = "LANG_index_persist";
	
	private final SemanticIndex index;
	
	public LANG_index_retrieve_data(SemanticIndex index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		// TODO Auto-generated method stub
		return false;
	}
}
