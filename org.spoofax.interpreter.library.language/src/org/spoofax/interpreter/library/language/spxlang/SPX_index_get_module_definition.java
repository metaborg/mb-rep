package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SPX_index_get_module_definition extends SpxAbstractPrimitive {
	private static String NAME = "SPX_index_get_module_definition";
	private static int MODULE_ID_INDEX = 1;
	private final static int NO_ARGS = 2;

	public SPX_index_get_module_definition(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}

	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateApplTermAt(MODULE_ID_INDEX);
	}
	
	/* Retrieve ModuleDefinition indexed by specified ModuleId mentioned 
	 * in {@code tvars} 
	 * 
	 * @see org.spoofax.interpreter.library.language.spxlang.SpxAbstractPrimitive#executePrimitive(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
	 */
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoAppl typedModuleQName = (IStrategoAppl)tvars[MODULE_ID_INDEX];
		IStrategoTerm t = index.getModuleDefinition(getProjectPath(tvars), typedModuleQName);
		env.setCurrent(t);
		return true;
	}
}
