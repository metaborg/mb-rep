package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.isTermAppl;
import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_remove_all extends AbstractPrimitive {
    private static String NAME = "LANG_index_remove_all";

    private final IndexManager index;

    public LANG_index_remove_all(IndexManager index) {
        super(NAME, 0, 2);
        this.index = index;
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        if(isTermAppl(tvars[0]) && (isTermTuple(tvars[1]) || isTermString(tvars[1]))) {
            IStrategoAppl template = (IStrategoAppl) tvars[0];
            IIndex ind = index.getCurrent();
            ind.removeAll(template);
            return true;
        } else {
            return false;
        }
    }
}
