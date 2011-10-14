package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.language.spxlang.SpxAbstractPrimitive.SpxPrimitiveValidator;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Primitive to index spoofaxlang SpxCompilationUnit
 * 
 * @author Md. Adil Akhter
 */
public class SPX_index_compilation_unit extends SpxAbstractPrimitive {
	private static String NAME = "SPX_index_compilation_unit";
	
	private static int COMPILATION_UNIT_PATH_INDEX = 1;
	private static int COMPILATION_UNIT_AST_INDEX  = 2;
	private final static int NO_ARGS = 3;
	
	public SPX_index_compilation_unit(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateStringTermAt(COMPILATION_UNIT_PATH_INDEX)
					.validateApplTermAt(COMPILATION_UNIT_AST_INDEX);
	}

    /** 
     * Retrieve Spoofaxlang ModuleDeclaration with Module ID 
	 * specified in {@code tvars}. 
     * in the following a Package or a Compilation Unit specified in \
     * {@code tvars}.     
     */
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoString spxCompilationUnitPath = (IStrategoString)tvars[COMPILATION_UNIT_PATH_INDEX];
		IStrategoAppl  compilationUnitRTree = (IStrategoAppl)tvars[COMPILATION_UNIT_AST_INDEX];
		
		return index.indexCompilationUnit(getProjectPath(tvars),  spxCompilationUnitPath ,  compilationUnitRTree);
	}
}
