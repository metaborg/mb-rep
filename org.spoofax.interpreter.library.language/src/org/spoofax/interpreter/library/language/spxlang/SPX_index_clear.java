package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;


//TODO : Generalize state management primitive. Abstract them to one primitive only.  

/**
 * @author Md. Adil Akhter
 * Created On : Aug 25, 2011
 */
public class SPX_index_clear extends SpxAbstractPrimitive {

	private final static String NAME = "SPX_index_clear";
	private final static int NO_ARGS = 1;
	
	public SPX_index_clear(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}

	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		return index.clearall(this.getProjectPath(tvars)); 
	}
}
