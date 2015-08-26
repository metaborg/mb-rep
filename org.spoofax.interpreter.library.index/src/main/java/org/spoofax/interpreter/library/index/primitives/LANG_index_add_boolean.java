package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_add_boolean extends IndexPrimitive {
    private static String NAME = "LANG_index_add_boolean";

    public LANG_index_add_boolean() {
        super(NAME, 0, 2);
    }

    @Override public boolean call(IIndex index, IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        final IStrategoTerm key = tvars[0];
        final IStrategoTerm source = tvars[1];
        index.add(index.entryFactory().create(key, source));
        return true;
    }
}
