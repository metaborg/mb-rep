package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Primitive to return a Spoofaxlang CompilationUnit
 *  
 * @author Md. Adil Akhter
 * Created On : Aug 25, 2011
 */
public class SPX_index_get_compilation_unit extends AbstractPrimitive {

	private static String NAME = "SPX_index_get_compilation_unit";
	private final static int PROJECT_NAME_INDEX = 0;
	private final static int COMP_UNIT_PATH_INDEX = 1;
	
	private final static int NO_ARGS = 2;
	private final SpxSemanticIndex index;

	public SPX_index_get_compilation_unit(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
		this.index = index;
	}	

	/* Retrieve Spoofaxlang Compilation Unit mapped by absolute path.  
	 * 
	 * @see org.spoofax.interpreter.library.AbstractPrimitive#call(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
	 */
	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars){ 
		boolean successStatement = false;
		
		if(tvars.length != NO_ARGS)
			return successStatement;
		
		
		if ( Tools.isTermString(tvars[PROJECT_NAME_INDEX]) && Tools.isTermString(tvars[COMP_UNIT_PATH_INDEX])) 
		{
			IStrategoString projectName = (IStrategoString)tvars[PROJECT_NAME_INDEX];
			IStrategoString spxCompilationUnitPath = (IStrategoString)tvars[COMP_UNIT_PATH_INDEX];
			try {
				IStrategoTerm t = index.getCompilationUnit(projectName ,  spxCompilationUnitPath);
				env.setCurrent(t);
				successStatement = true;
			} 
			catch(Exception ex)
			{
				// Logging any exception throw from the underlying symbol table. 
				SSLLibrary.instance(env).getIOAgent().printError("["+NAME+" Invokation failed . ] Error : "+ ex.getMessage());
			}
		}
		return successStatement;
	}

}
