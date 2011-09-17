package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Md. Adil Akhter
 * Created On : Aug 25, 2011
 */
public class SPX_index_save extends AbstractPrimitive {

	private static String NAME = "SPX_index_save";
	private static int PROJECT_NAME_INDEX = 0;
	private final static int NO_ARGS = 1;
	
	private final SpxSemanticIndex index;

	public SPX_index_save(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.AbstractPrimitive#call(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
	 */
	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		boolean retValue = false;
		if ( (tvars.length == NO_ARGS) && Tools.isTermString(tvars[PROJECT_NAME_INDEX])) {
			try {
				retValue = index.save(tvars[PROJECT_NAME_INDEX]);
			}
			catch(Exception ex)	{
				SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invocation failed . "+ ex.getClass().getSimpleName() +" | error message: " + ex.getMessage());
			}
		}
		else
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"] Invocation failed . Error :  Mismatch in provided arguments. Variables provided : "+ tvars);
		return retValue;
	}

}
