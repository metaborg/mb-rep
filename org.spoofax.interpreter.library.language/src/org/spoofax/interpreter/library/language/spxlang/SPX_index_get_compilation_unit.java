package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.language.spxlang.SpxAbstractPrimitive.SpxPrimitiveValidator;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Primitive to return a Spoofaxlang CompilationUnit
 *  
 * @author Md. Adil Akhter
 * Created On : Aug 25, 2011
 */
public class SPX_index_get_compilation_unit extends SpxAbstractPrimitive {

	private static String NAME = "SPX_index_get_compilation_unit";
	private final static int COMP_UNIT_PATH_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_index_get_compilation_unit(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateStringTermAt(COMP_UNIT_PATH_INDEX);
	}

    /** 
     * Retrieve Spoofaxlang ModuleDeclaration with Module ID 
	 * specified in {@code tvars}. 
     * in the following a Package or a Compilation Unit specified in \
     * {@code tvars}.     
     */
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoString spxCompilationUnitPath = (IStrategoString)tvars[COMP_UNIT_PATH_INDEX];
		IStrategoTerm t = index.getCompilationUnit(getProjectPath(tvars),  spxCompilationUnitPath);
		env.setCurrent(t);
		return true;
	}
}
