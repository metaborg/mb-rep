package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Primitive to initialize Spoofaxlang Semantic Index
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 25, 2011
 */
public class SPX_index_init extends SpxAbstractPrimitive {

	private static String NAME = "SPX_index_init";
	private final static int NO_ARGS = 1;
	
	public SPX_index_init(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS );
	}

	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		return index.initialize(this.getProjectPath(tvars), env.getFactory(), SSLLibrary.instance(env).getIOAgent());  
	}
}
