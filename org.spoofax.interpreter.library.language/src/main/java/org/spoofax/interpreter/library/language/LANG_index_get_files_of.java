package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermAppl;

import java.util.Collection;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_get_files_of extends AbstractPrimitive {
    private static String NAME = "LANG_index_get_files_of";

    private final IndexManager index;

    public LANG_index_get_files_of(IndexManager index) {
        super(NAME, 0, 1);
        this.index = index;
    }

    /**
     * Returns [] if URI not in index.
     */
    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        if(isTermAppl(tvars[0])) {
            IStrategoAppl template = (IStrategoAppl) tvars[0];
            IIndex ind = index.getCurrent();
            Collection<IndexEntry> entries = ind.getEntries(template);
            IStrategoList partitions = env.getFactory().makeList();
            for(IndexEntry entry : entries) {
                IStrategoTerm partition = entry.getPartitionDescriptor().toTerm(env.getFactory());
                partitions = env.getFactory().makeListCons(partition, partitions);
            }
            env.setCurrent(partitions);
            return true;
        } else {
            return false;
        }
    }
}
