package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermString;
import static org.spoofax.interpreter.core.Tools.isTermTuple;

import java.util.Collection;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_get_all_in_file extends AbstractPrimitive {
    private static String NAME = "LANG_index_get_all_in_file";

    private final IndexManager index;

    public LANG_index_get_all_in_file(IndexManager index) {
        super(NAME, 0, 1);
        this.index = index;
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        if(isTermTuple(tvars[0]) || isTermString(tvars[0])) {
            IIndex ind = index.getCurrent();
            IndexPartitionDescriptor partitionDescriptor = ind.getPartitionDescriptor(tvars[0]);
            Collection<IndexEntry> results = ind.getEntriesInPartition(partitionDescriptor);
            env.setCurrent(IndexEntry.toTerms(env.getFactory(), results));
            return true;
        } else {
            return false;
        }
    }
}
