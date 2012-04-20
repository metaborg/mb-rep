package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Md. Adil Akhter
 * Created On : Sep 11, 2011
 */
public class SPX_index_get_package_declarations_of extends SpxAbstractPrimitive {
	private static String NAME = "SPX_index_get_package_declarations_of";
	private static int COMPILATION_UNIT_URI_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_index_get_package_declarations_of(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateStringTermAt(COMPILATION_UNIT_URI_INDEX);
	}
	
	/* Retrieve Spoofaxlang  PackageDeclarations of a SpxCompilationUnit 
	 * specified in {@code tvars}.    
	 */
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoString  compilationUnitUri= (IStrategoString)tvars[COMPILATION_UNIT_URI_INDEX];
		IStrategoTerm t = index.getPackageDeclarationsByUri(getProjectPath(tvars), compilationUnitUri);
		env.setCurrent(t);
		return true;
	}
}