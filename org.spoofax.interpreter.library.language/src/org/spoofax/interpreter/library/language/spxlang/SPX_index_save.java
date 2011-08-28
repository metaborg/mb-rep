package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.isTermAppl;

import java.io.IOException;

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

	private final SpxSemanticIndex index;

	public SPX_index_save(SpxSemanticIndex index) {
		super(NAME, 0, 1);
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.AbstractPrimitive#call(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
	 */
	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars)
			throws InterpreterException {
	
		if ( tvars.length != 1)
			return false;
		
		if (Tools.isTermString(tvars[0]))
			return false;
		
		try 
		{
			return index.save(tvars[0]);
		}
		catch(Exception ex)
		{
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"] Error : "+ ex.getMessage());
			return false;
		}
		
	}

}
