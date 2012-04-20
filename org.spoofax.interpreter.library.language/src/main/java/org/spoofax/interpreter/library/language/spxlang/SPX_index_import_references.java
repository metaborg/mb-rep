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
public class SPX_index_import_references extends SpxAbstractPrimitive {

	private final static String NAME = "SPX_index_import_references";
	private final static int IMPORT_REFERENCES_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_index_import_references(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateApplTermAt(IMPORT_REFERENCES_INDEX);
	}


	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoAppl importReferences   = (IStrategoAppl) tvars[IMPORT_REFERENCES_INDEX];
		return index.indexImportReferences(getProjectPath(tvars), importReferences);
	}
	
	
}