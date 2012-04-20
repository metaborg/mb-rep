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
 * Primitive to retrieve LangaugeDescriptor from {@link SpxSemanticIndex} for a specified Package
 * 
 * @author Md. Adil Akhter
 * Created On : Sep 11, 2011
 */
public class SPX_index_get_language_descriptor extends SpxAbstractPrimitive {

	private static String NAME = "SPX_index_get_language_descriptor";
	private final static int PACKAGE_ID_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_index_get_language_descriptor(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateApplTermAt(PACKAGE_ID_INDEX);
	}

    /** 
     * Retrieve Spoofaxlang LangaugeDescriptor for a specified Package. 
	 * The typed qualified PackageID is specified in {@code tvars}
     */
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoAppl typedPackageIdQName = (IStrategoAppl)tvars[PACKAGE_ID_INDEX];
		IStrategoTerm t = index.getLanguageDescriptor(getProjectPath(tvars), typedPackageIdQName);
		env.setCurrent(t);
		return true;
	}
}
