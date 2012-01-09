package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;


/**
 * @author Md. Adil Akhter
 * Created On : Jan 7, 2012
 */
public class SPX_index_get_summary extends SpxAbstractPrimitive {
	private static String NAME = "SPX_index_get_summary";
	private final static int NO_ARGS = 1;
	
	public SPX_index_get_summary(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars);
	}
	
	
	/* Retrieve Spoofaxlang  index snapshots . It returns the spx packages defined in the current 
	 * project along with their enclosed modules and import references. Typical use case of this primitive 
	 * is to provide content certain views such as dependency graph, outline view.  
	 *     
	 */
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoTerm t = index.getIndexSummary(getProjectPath(tvars) ,null);
		env.setCurrent(t);
		return true;
	}
}