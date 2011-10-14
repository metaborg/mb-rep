package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Primitive to remove compilation unit from the {@link SpxSemanticIndex}
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 25, 2011
 */
public class SPX_index_remove_compilation_unit extends SpxAbstractPrimitive {

	private static String NAME = "SPX_index_remove_compilation_unit";
	private final static int FILE_PATH_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	/**
	 * Instantiates a new instance of {@link SPX_index_remove_compilation_unit}  that removes a
	 * Spoofaxlang Compilation Unit entry from the symbol table given the absolute path of the resource.
	 *   
	 * @param index an instance of {@link SpxSemanticIndex}
	 */
	public SPX_index_remove_compilation_unit(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateStringTermAt(FILE_PATH_INDEX);
	}
	
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoString spxCompilationUnitPath = (IStrategoString)tvars[FILE_PATH_INDEX];
		return index.removeCompilationUnit(getProjectPath(tvars) ,  spxCompilationUnitPath);
	}
}
