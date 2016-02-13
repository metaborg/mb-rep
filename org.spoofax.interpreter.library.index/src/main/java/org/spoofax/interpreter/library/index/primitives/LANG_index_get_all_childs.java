package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexEntry;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_get_all_childs extends IndexPrimitive {
    private static String NAME = "LANG_index_get_all_childs";

    public LANG_index_get_all_childs() {
        super(NAME, 0, 1);
    }

    @Override public boolean call(IIndex index, IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        final IStrategoTerm key = tvars[0];
        final Iterable<IndexEntry> entries = index.getChilds(key);
        env.setCurrent(index.entryFactory().toValueTerms(entries));
        return true;
    }
}
