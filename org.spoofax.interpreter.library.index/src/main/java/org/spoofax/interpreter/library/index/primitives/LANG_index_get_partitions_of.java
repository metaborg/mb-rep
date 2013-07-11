package org.spoofax.interpreter.library.index.primitives;

import static org.spoofax.interpreter.core.Tools.isTermAppl;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.library.index.IndexManager;
import org.spoofax.interpreter.library.index.IndexPartitionDescriptor;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_get_partitions_of extends AbstractPrimitive {
    private static String NAME = "LANG_index_get_partitions_of";

    public LANG_index_get_partitions_of() {
        super(NAME, 0, 1);
    }

    /**
     * Returns [] if URI not in index.
     */
    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        if(isTermAppl(tvars[0])) {
        	final IIndex index = IndexManager.getInstance().getCurrent();
            final IStrategoAppl template = (IStrategoAppl) tvars[0];
            env.setCurrent(IndexPartitionDescriptor.toTerms(env.getFactory(), index.getPartitionsOf(template)));
            return true;
        } else {
            return false;
        }
    }
}
