package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SPX_symtab_define_symbol extends SpxAbstractPrimitive {
	private static String NAME = "SPX_symtab_define_symbol";
	private final static int SYMBOL_DEF_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_symtab_define_symbol(SpxSemanticIndex symTable) {
		super(symTable, NAME, 0, NO_ARGS);
	}

	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateApplTermAt(SYMBOL_DEF_INDEX);
	}
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoAppl symbolDef   = (IStrategoAppl) tvars[SYMBOL_DEF_INDEX];
		return index.indexSymbolDefinition(getProjectPath(tvars), symbolDef);
	}
}
