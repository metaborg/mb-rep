package org.spoofax.interpreter.library.language;

import java.io.IOException;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_commit extends AbstractPrimitive {
    private static String NAME = "LANG_index_commit";

    private final IndexManager index;

    public LANG_index_commit(IndexManager index) {
        super(NAME, 0, 0);
        this.index = index;
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        try {
            index.storeCurrent();
        } catch(IOException e) {
            e.printStackTrace(); // ignore
        }
        return true;
    }
}
