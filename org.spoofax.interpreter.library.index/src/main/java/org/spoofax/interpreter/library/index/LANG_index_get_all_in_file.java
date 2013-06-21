package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class LANG_index_get_all_in_file extends AbstractPrimitive {
    private static String NAME = "LANG_index_get_all_in_file";

    public LANG_index_get_all_in_file() {
        super(NAME, 0, 1);
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        if(isTermTuple(tvars[0]) || isTermString(tvars[0])) {
            IIndex ind = IndexManager.getInstance().getCurrent();
            IndexPartitionDescriptor partitionDescriptor = ind.getPartitionDescriptor(tvars[0]);
            IIndexEntryIterable results = ind.getInPartition(partitionDescriptor);
            env.setCurrent(IndexEntry.toTerms(env.getFactory(), results));
            return true;
        } else {
            return false;
        }
    }
}
