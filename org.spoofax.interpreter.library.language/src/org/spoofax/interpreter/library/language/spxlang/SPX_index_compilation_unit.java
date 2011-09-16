package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Primitive to index spoofaxlang CompilationUnit
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 25, 2011
 */
public class SPX_index_compilation_unit extends AbstractPrimitive {

	private static String NAME = "SPX_index_compilation_unit";
	
	private static int PROJECT_NAME_INDEX = 0;
	private static int COMPILATION_UNIT_PATH_INDEX = 1;
	private static int COMPILATION_UNIT_AST_INDEX = 2;
	
	private final static int NO_ARGS = 3;
	
	private final SpxSemanticIndex index;

	public SPX_index_compilation_unit(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.AbstractPrimitive#call(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
	 */
	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) 
	{
		boolean successStatement = false;
		 
		if (Tools.isTermString(tvars[PROJECT_NAME_INDEX])
				&& Tools.isTermString(tvars[COMPILATION_UNIT_PATH_INDEX])
				&& Tools.isTermAppl(tvars[COMPILATION_UNIT_AST_INDEX])) {
			IStrategoString projectName = (IStrategoString)tvars[PROJECT_NAME_INDEX];
			IStrategoString spxCompilationUnitPath = (IStrategoString)tvars[COMPILATION_UNIT_PATH_INDEX];
			IStrategoAppl  compilationUnitRTree = (IStrategoAppl)tvars[COMPILATION_UNIT_AST_INDEX];
			
			try {
				successStatement  = index.indexCompilationUnit(projectName ,  spxCompilationUnitPath ,  compilationUnitRTree);
			} 
			catch(Exception ex)
			{
				SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invokation failed . Error : "+ ex.getMessage());
			}
		}
		else
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invokation failed . Error :  Mismatch in provided arguments. Variables provided : "+ tvars);
			
		return successStatement;
	}

}
