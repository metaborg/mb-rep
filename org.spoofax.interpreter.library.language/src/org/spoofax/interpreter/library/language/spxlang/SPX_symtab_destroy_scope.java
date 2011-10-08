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

public class SPX_symtab_destroy_scope extends AbstractPrimitive {
	private static String NAME = "SPX_symtab_destroy_scope";
	
	private final static int PROJECT_NAME_INDEX = 0;
	private final static int ENCLOSING_NAMESPACE_ID = 1;
	
	private final static int NO_ARGS = 2;
	
	private final SpxSemanticIndex symTable;
	
	public SPX_symtab_destroy_scope(SpxSemanticIndex symTable) {
		super(NAME, 0, NO_ARGS);
		this.symTable = symTable;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		boolean successStatement = false;
		
		if ((NO_ARGS == tvars.length) && isTermString(tvars[PROJECT_NAME_INDEX]) && isTermAppl(tvars[ENCLOSING_NAMESPACE_ID])) {
			
			IStrategoString projectName = (IStrategoString)tvars[PROJECT_NAME_INDEX];
			IStrategoAppl namespaceAppl   = (IStrategoAppl) tvars[ENCLOSING_NAMESPACE_ID];
			
			try{
				IStrategoTerm term = symTable.destroyScope(projectName, namespaceAppl);
				successStatement = true;
				env.setCurrent(term);
			}
			catch(Exception ex){ 
				SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invocation failed . "+ ex.getClass().getSimpleName() +" | error message: " + ex.getMessage());
			}
		}
		else
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invocation failed .  Error :  Mismatch in provided arguments. Variables provided : "+ tvars);
		
		return successStatement;	
	}
}
