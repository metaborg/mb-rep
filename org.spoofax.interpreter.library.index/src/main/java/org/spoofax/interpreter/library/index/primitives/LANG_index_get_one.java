package org.spoofax.interpreter.library.index.primitives;

import java.util.Iterator;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_get_one extends AbstractPrimitive {
	private static String NAME = "LANG_index_get_one";

	public LANG_index_get_one() {
		super(NAME, 0, 1);
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		final IIndex index = IndexManager.getInstance().getCurrent();
		final IStrategoTerm key = tvars[0];
		final Iterable<IndexEntry> entries = index.get(key);
		final Iterator<IndexEntry> entriesIterator = entries.iterator();
		if(!entriesIterator.hasNext())
			return false;
		final IndexEntry entry = entriesIterator.next();
		env.setCurrent(entry.getValue());
		return true;
	}
}
