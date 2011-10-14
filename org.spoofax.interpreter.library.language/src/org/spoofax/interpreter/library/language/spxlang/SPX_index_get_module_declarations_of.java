package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.language.spxlang.SpxAbstractPrimitive.SpxPrimitiveValidator;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Primitive to retrieve ModuleDeclarations from {@link SpxSemanticIndex}
 * 
 * @author Md. Adil Akhter
 */
public class SPX_index_get_module_declarations_of extends SpxAbstractPrimitive {

    private final static String NAME = "SPX_index_get_module_declarations_of";
    private final static int RES_INDEX = 1;
    private final static int NO_ARGS = 2;

    public SPX_index_get_module_declarations_of(SpxSemanticIndex index) {
    	super(index, NAME, 0, NO_ARGS);
    }
    
    @Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars);
					
	}

    /* Retrieve Spoofaxlang ModuleDeclarations enclosed 
     * in the following a Package or a Compilation Unit specified in 
     * {@code tvars}.  
     */
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoTerm retTerm = index.getModuleDeclarations(getProjectPath(tvars), tvars[RES_INDEX]);
		env.setCurrent(retTerm);
		return true;
	}
}
