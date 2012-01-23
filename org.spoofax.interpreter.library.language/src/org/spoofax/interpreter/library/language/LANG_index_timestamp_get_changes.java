package org.spoofax.interpreter.library.language;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_timestamp_get_changes extends AbstractPrimitive {

	private static String NAME = "LANG_index_timestamp_get_changes";

	private final SemanticIndexManager index;

	public LANG_index_timestamp_get_changes(SemanticIndexManager index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
  public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
	  throw new NotImplementedException();
  }
}
