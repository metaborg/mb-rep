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
 * Primitive to retrieve ModuleDeclarations from {@link SpxSemanticIndex}
 * 
 * @author Md. Adil Akhter
 * Created On : Sep 11, 2011
 */
public class SPX_index_get_module_declarations_of extends AbstractPrimitive {

    private static String NAME = "SPX_index_get_module_declarations_of";
    private static int PROJECT_NAME_INDEX = 0;
    private static int RES_INDEX = 1;

    private final static int NO_ARGS = 2;

    private final SpxSemanticIndex index;

    public SPX_index_get_module_declarations_of(SpxSemanticIndex index) {
	super(NAME, 0, NO_ARGS);
	this.index = index;
    }
	
    /* Retrieve Spoofaxlang ModuleDeclarations enclosed in the following resource - {@code tvars}.    
     * 
     * {@code tvars} contains name of the project and typed qualified ModuleID  
     * @see org.spoofax.interpreter.library.AbstractPrimitive#call(org.spoofax.interpreter.core.IContext, org.spoofax.interpreter.stratego.Strategy[], org.spoofax.interpreter.terms.IStrategoTerm[])
     */
    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
    	boolean successStatement = false;

    	if ((tvars.length == NO_ARGS)  && Tools.isTermString(tvars[PROJECT_NAME_INDEX])) 
    	{
    		IStrategoString projectName = (IStrategoString)tvars[PROJECT_NAME_INDEX];
    		try {
    			IStrategoTerm retTerm = index.getModuleDeclarations(projectName, tvars[RES_INDEX]);
    			successStatement = true;
    			env.setCurrent(retTerm);
    		}
    		catch(Exception ex)
    		{
    			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+" Invokation failed . ] Error : "+ ex.getMessage());
    		}
    	}
    	else
    		SSLLibrary.instance(env).getIOAgent().printError("["+NAME+" Invokation failed . ] Error :  Mismatch in provided arguments. Variables provided : "+ tvars);

    	return successStatement;
    }
}
