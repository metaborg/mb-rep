package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Primitive to save Spoofaxlang Semantic Index in disk and close any 
 * underlying open connection . 
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 25, 2011
 */
public class SPX_index_close extends AbstractPrimitive {

	private static String NAME = "SPX_index_close";
	private final static int PROJECT_NAME_INDEX = 0;
	private final static int NO_ARGS = 1;
	
	private final SpxSemanticIndex index;

	public SPX_index_close(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.AbstractPrimitive#call(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
	 */
	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars)
			throws InterpreterException {
	
		if ( tvars.length != NO_ARGS || !Tools.isTermString(tvars[PROJECT_NAME_INDEX]))
		{
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+" Invokation failed . ] Error :  Mismatch in provided arguments. Variables provided : "+ tvars);
			return false;
		}
		
		try 
		{
			return index.close(tvars[PROJECT_NAME_INDEX]);
		}
		catch(Exception ex)
		{
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"] Invokation failed. Error : "+ ex.getMessage());
			return false;
		}
	}	
}
