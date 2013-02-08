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
public class SPX_index_get_imported_to_references extends SpxAbstractPrimitive {

	private final static String NAME = "SPX_index_get_imported_to_references";
	private final static int NAMESPACE_ID_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_index_get_imported_to_references(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateApplTermAt(NAMESPACE_ID_INDEX);
	}


	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoAppl namespaceID   = (IStrategoAppl) tvars[NAMESPACE_ID_INDEX];
		IStrategoTerm t =  index.getImportedToReferences(getProjectPath(tvars), namespaceID);
		env.setCurrent(t);
		return true;
	}
		
}