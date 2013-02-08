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
public class SPX_index_get_package_declarations_by_LanguageName extends SpxAbstractPrimitive {
	private static String NAME = "SPX_index_get_package_declarations_by_LanguageName";
	private static int LANG_NAME_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_index_get_package_declarations_by_LanguageName(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateStringTermAt(LANG_NAME_INDEX);
	}
	
	/* Retrieve Spoofaxlang  PackageDeclarations of a SpxCompilationUnit 
	 * specified in {@code tvars}.    
	 */
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoString  langName= (IStrategoString)tvars[LANG_NAME_INDEX];
		IStrategoTerm t = index.getPackageDeclarationsByLanguageName(getProjectPath(tvars), langName);
		env.setCurrent(t);
		return true;
	}
}