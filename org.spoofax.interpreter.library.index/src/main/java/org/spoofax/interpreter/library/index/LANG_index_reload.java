package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.imp.runtime.stratego.NotificationCenter;

public class LANG_index_reload extends AbstractPrimitive {
    private static String NAME = "LANG_index_reload";

    private final IndexManager index;

    public LANG_index_reload(IndexManager index) {
        super(NAME, 0, 0);
        this.index = index;
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        index.getCurrent().clearAll();
        NotificationCenter.notifyNewProject(index.getCurrentProject());
        return true;
    }
}
