package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermAppl;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_get extends AbstractPrimitive {

	private static String NAME = "LANG_index_get";
	
	private final SemanticIndexManager index;
	
	public LANG_index_get(SemanticIndexManager index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if (isTermAppl(tvars[0])) {
			IStrategoAppl template = (IStrategoAppl) tvars[0];
			SemanticIndex ind = index.getCurrent();
			SemanticIndexEntry entry = ind.getEntries(template);
			SemanticIndexEntryFactory fact = ind.getFactory();
			
			if(entry == null) {
			    env.setCurrent(fact.getTermFactory().makeList());
			} else {
			    env.setCurrent(entry.toTerms(env.getFactory()));
			}
			
			return true;
		} else {
			return false;
		}
	}
}
