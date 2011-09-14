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

/**
 * Primitive to retrieve LangaugeDescriptor from {@link SpxSemanticIndex} for a specified Package
 * 
 * @author Md. Adil Akhter
 * Created On : Sep 11, 2011
 */
public class SPX_index_get_language_descriptor extends AbstractPrimitive {

	private static String NAME = "SPX_index_get_language_descriptor";
	
	private final static int PROJECT_NAME_INDEX = 0;
	private final static int PACKAGE_ID_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	private final SpxSemanticIndex index;

	
	public SPX_index_get_language_descriptor(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
		this.index = index;
	}
	
	/* Retrieve Spoofaxlang LangaugeDescriptor for a specified Package. 
	 * The typed qualified PackageID is specified in {@code tvars} 
	 *
	 * (non-Javadoc)
	 * @see org.spoofax.interpreter.library.AbstractPrimitive#call(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
	 */
	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars)
			throws InterpreterException {
		boolean successStatement = false;
		
		if ( (tvars.length == NO_ARGS)  && Tools.isTermString(tvars[PROJECT_NAME_INDEX]) && Tools.isTermAppl(tvars[PACKAGE_ID_INDEX])) 
		{
			IStrategoString projectName    = (IStrategoString)tvars[PROJECT_NAME_INDEX];
			IStrategoAppl typedPackageIdQName = (IStrategoAppl)tvars[PACKAGE_ID_INDEX];
			try {
				IStrategoTerm t = index.getLanguageDescriptor(projectName, typedPackageIdQName);
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
