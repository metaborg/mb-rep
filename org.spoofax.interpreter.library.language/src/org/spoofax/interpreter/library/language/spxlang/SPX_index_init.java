package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Md. Adil Akhter
 * Created On : Aug 25, 2011
 */
public class SPX_index_init extends AbstractPrimitive {

	private static String NAME = "SPX_index_init";

	private final SpxSemanticIndex index;

	public SPX_index_init(SpxSemanticIndex index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.AbstractPrimitive#call(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
	 */
	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars)
	{
		if ( tvars.length != 1)
			return false;

		IStrategoString projectName = (IStrategoString)tvars[0];
		try
		{
			return index.initialize(projectName , env.getFactory(), SSLLibrary.instance(env).getIOAgent());
		}
		catch(Exception ex)
		{
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"] Invokation failed. Error : "+ ex.getMessage());
			return false;
		}
	}

}
