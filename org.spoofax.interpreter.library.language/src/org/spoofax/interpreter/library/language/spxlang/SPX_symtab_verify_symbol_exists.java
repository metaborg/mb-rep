package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbol;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public class SPX_symtab_verify_symbol_exists extends SpxAbstractPrimitive {
	private static String NAME = "SPX_symtab_verify_symbol_exists";
	private static int SEARCH_CRITERIA_INDEX = 1;
	private final static int NO_ARGS = 2;

	public SPX_symtab_verify_symbol_exists(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateTupleTermAt(SEARCH_CRITERIA_INDEX);
	}
	
	
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoTuple searchCriteria = (IStrategoTuple)tvars[SEARCH_CRITERIA_INDEX];
		SpxSymbol ret = index.verifySymbolExists(getProjectPath(tvars), searchCriteria);
		
		return ret != null;
	}
}