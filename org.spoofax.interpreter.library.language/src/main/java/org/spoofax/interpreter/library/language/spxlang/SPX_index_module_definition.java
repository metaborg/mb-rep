/**
 * 
 */
package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SPX_index_module_definition extends SpxAbstractPrimitive  {
	private static String NAME = "SPX_index_module_definition";
	private static int MODULE_DEF_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_index_module_definition(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateApplTermAt(MODULE_DEF_INDEX);
	}

	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoAppl moduleDef = (IStrategoAppl) tvars[MODULE_DEF_INDEX];
		return index.indexModuleDefinition(this.getProjectPath(tvars), moduleDef);
	}
}