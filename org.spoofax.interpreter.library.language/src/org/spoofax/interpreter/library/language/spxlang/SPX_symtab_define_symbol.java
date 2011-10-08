package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.isTermAppl;
import static org.spoofax.interpreter.core.Tools.isTermString;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SPX_symtab_define_symbol extends AbstractPrimitive {
	private static String NAME = "SPX_symtab_define_symbol";
	
	private final static int PROJECT_NAME_INDEX = 0;
	private final static int SYMBOL_DEF_INDEX = 1;
	
	private final static int NO_ARGS = 2;
	
	private final SpxSemanticIndex index;
	
	public SPX_symtab_define_symbol(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		boolean successStatement = false;
		
		if ((NO_ARGS == tvars.length) && isTermString(tvars[PROJECT_NAME_INDEX]) && isTermAppl(tvars[SYMBOL_DEF_INDEX])) {
			
			IStrategoString projectName = (IStrategoString)tvars[PROJECT_NAME_INDEX];
			IStrategoAppl symbolDef   = (IStrategoAppl) tvars[SYMBOL_DEF_INDEX];
			
			try
			{
				successStatement = index.indexSymbolDefinition(projectName, symbolDef);
			}
			catch(Exception ex)
			{ 
				SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invocation failed . "+ ex.getClass().getSimpleName() +" | error message: " + ex.getMessage());
			}
		}
		else
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invocation failed .  Error :  Mismatch in provided arguments. Variables provided : "+ tvars);
		
		return successStatement;	
	}
}
