package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_collect extends IndexPrimitive {
    private static String NAME = "LANG_index_collect";

    public LANG_index_collect() {
        super(NAME, 0, 2);
    }

    @Override public boolean call(IIndex index, IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        final IStrategoTerm key = tvars[0];
        final IStrategoTerm value = tvars[1];
        index.collect(key, value);
        return true;
    }
}
