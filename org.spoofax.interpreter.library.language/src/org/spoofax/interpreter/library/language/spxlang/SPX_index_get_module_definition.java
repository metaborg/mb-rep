package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SPX_index_get_module_definition extends AbstractPrimitive {

	private static String NAME = "SPX_index_get_module_definition";
	private static int PROJECT_NAME_INDEX = 0;
	private static int MODULE_ID_INDEX = 1;
	
	private final SpxSemanticIndex index;

	public SPX_index_get_module_definition(SpxSemanticIndex index) {
		super(NAME, 0, 2);
		this.index = index;
	}
	
	/* Retrieve Spoofaxlang ModuleDefinition with Module ID 
	 * specified in {@code tvars}.    
	 * 
	 * {@code tvars} contains name of the project and typed qualified ModuleID  
	 * @see org.spoofax.interpreter.library.AbstractPrimitive#call(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
	 */
	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		boolean successStatement = false;
		if ( Tools.isTermString(tvars[PROJECT_NAME_INDEX]) && Tools.isTermAppl(tvars[MODULE_ID_INDEX])) 
		{
			IStrategoString projectName    = (IStrategoString)tvars[PROJECT_NAME_INDEX];
			IStrategoAppl typedModuleQName = (IStrategoAppl)tvars[MODULE_ID_INDEX];
			try {
				IStrategoTerm t = index.getModuleDefinition(projectName, typedModuleQName);
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
