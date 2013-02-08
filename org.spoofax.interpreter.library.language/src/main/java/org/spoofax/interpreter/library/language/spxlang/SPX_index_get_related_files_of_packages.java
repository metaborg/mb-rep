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
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SPX_index_get_related_files_of_packages extends SpxAbstractPrimitive {
    private final static String NAME = "SPX_index_get_related_files_of_packages";
    private final static int PACKAGE_LIST_INDEX = 1;
    private final static int NO_ARGS = 2;

    public SPX_index_get_related_files_of_packages(SpxSemanticIndex index) {
    	super(index, NAME, 0, NO_ARGS);
    }
    
    @Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateListTermAt(PACKAGE_LIST_INDEX);
					
	}

	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoList packageList= (IStrategoList) tvars[PACKAGE_LIST_INDEX];
		
		IStrategoTerm retTerm = index.getRelatedFilesOfPackages(getProjectPath(tvars), packageList);
		env.setCurrent(retTerm);
		return true;
	}
}
