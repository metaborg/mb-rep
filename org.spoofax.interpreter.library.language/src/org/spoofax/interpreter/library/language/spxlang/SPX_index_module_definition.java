/**
 * 
 */
package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.isTermAppl;
import static org.spoofax.interpreter.core.Tools.isTermString;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Md. Adil Akhter
 *
 */
public class SPX_index_module_definition extends AbstractPrimitive {

	private static String NAME = "SPX_index_module_definition";
	private static int PROJECT_NAME_INDEX = 0;
	private static int MODULE_DEF_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	private final SpxSemanticIndex index;

	public SPX_index_module_definition(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		boolean successStatement = false;
		
		if ( (tvars.length == NO_ARGS) && isTermString(tvars[PROJECT_NAME_INDEX]) && isTermAppl(tvars[MODULE_DEF_INDEX])) {
			
			IStrategoString projectName = (IStrategoString)tvars[PROJECT_NAME_INDEX];
			IStrategoAppl moduleDef = (IStrategoAppl) tvars[MODULE_DEF_INDEX ];
			
			try
			{
				successStatement = index.indexModuleDefinition(projectName, moduleDef);
			}
			catch(Exception ex)
			{ 
				SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invokation failed . Error : "+ ex.getMessage());
			}
		}
		else
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invokation failed .  Error :  Mismatch in provided arguments. Variables provided : "+ tvars);
		
		return successStatement;	
	}
}