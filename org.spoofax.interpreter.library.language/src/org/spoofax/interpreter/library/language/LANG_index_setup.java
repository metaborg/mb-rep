package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_setup extends AbstractPrimitive {

	private static String NAME = "LANG_index_setup";
	
	private final SemanticIndex index;
	
	public LANG_index_setup(SemanticIndex index) {
		super(NAME, 0, 4);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		// TODO Auto-generated method stub
		// gets:
		// - store path
		// - root paths
		// - lookup paths for additional root paths
		// - file extensionS for determining input files (or a strategy to determine it based on filename/contents?)
		// 
		// would be nice if it could read .project files for project refs
		// TODO: clear index when it is loaded from somwhere else
		index.initialize(env.getFactory(), SSLLibrary.instance(env).getIOAgent());
		return true;
	}
}
