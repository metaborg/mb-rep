package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.isTermAppl;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_get_files_of extends AbstractPrimitive {
    private static String NAME = "LANG_index_get_files_of";

    private final IndexManager manager;

    public LANG_index_get_files_of(IndexManager index) {
        super(NAME, 0, 1);
        this.manager = index;
    }

    /**
     * Returns [] if URI not in index.
     */
    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        if(isTermAppl(tvars[0])) {
            IStrategoAppl template = (IStrategoAppl) tvars[0];
            IIndex index = manager.getCurrent();
            env.setCurrent(IndexPartitionDescriptor.toTerms(env.getFactory(), index.getPartitionsOf(template)));
            return true;
        } else {
            return false;
        }
    }
}
