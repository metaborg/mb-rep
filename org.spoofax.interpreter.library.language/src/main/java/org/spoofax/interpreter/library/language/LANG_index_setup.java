package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_setup extends AbstractPrimitive {
    private static String NAME = "LANG_index_setup";

    private final IndexManager index;

    public LANG_index_setup(IndexManager index) {
        super(NAME, 0, 3);
        this.index = index;
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
        IStrategoString language = (IStrategoString) tvars[0];
        IStrategoList projectPaths = (IStrategoList) tvars[1];
        IStrategoTerm partitionTerm = tvars[2];
        if(projectPaths.size() != 1) {
            throw new NotImplementedException("Multiple project paths");
        }
        IOAgent agent = SSLLibrary.instance(env).getIOAgent();
        IndexPartitionDescriptor project = IndexPartitionDescriptor.fromTerm(agent, projectPaths.head());
        IndexPartitionDescriptor partition = IndexPartitionDescriptor.fromTerm(agent, partitionTerm);
        index.loadIndex(asJavaString(language), project.getURI(), env.getFactory(), agent);
        index.setCurrentPartition(partition);
        index.getCurrent().initialize(env.getFactory(), agent);
        return true;
    }
}
