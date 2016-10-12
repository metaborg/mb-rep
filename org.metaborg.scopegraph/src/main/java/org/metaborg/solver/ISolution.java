package org.metaborg.solver;

import org.metaborg.fastutil.persistent.Object2ObjectPMap;
import org.metaborg.fastutil.persistent.ObjectPSet;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IResolvingScopeGraph;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.unification.ITermUnifier;

public interface ISolution {

    ITermUnifier getUnifier();

    ISolution setUnifier(ITermUnifier unifier);

    IResolvingScopeGraph getScopeGraph();

    ISolution setScopeGraph(IResolvingScopeGraph scopeGraph);

    Object2ObjectPMap<IOccurrence,IFullPath> getResolution();

    ISolution setResolution(Object2ObjectPMap<IOccurrence,IFullPath> resolution);

    ObjectPSet<String> getErrors();

    ISolution setErrors(ObjectPSet<String> errors);

}