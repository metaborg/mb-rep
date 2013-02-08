/**
 * 
 */
package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Md. Adil Akhter
 *
 */
public class SPX_index_package_declaration extends SpxAbstractPrimitive {

	private static String NAME = "SPX_index_package_declaration";
	private final static int PACKAGE_DECL_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_index_package_declaration(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		return super.validateArguments(env, svars, tvars)
					.validateApplTermAt(PACKAGE_DECL_INDEX);	
	};
	
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoAppl packageDecl   = (IStrategoAppl) tvars[PACKAGE_DECL_INDEX];
		return index.indexPackageDeclaration(getProjectPath(tvars), packageDecl);
	}
}