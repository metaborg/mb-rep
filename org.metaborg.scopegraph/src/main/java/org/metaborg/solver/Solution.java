package org.metaborg.solver;

import org.metaborg.fastutil.persistent.Object2ObjectOpenHashPMap;
import org.metaborg.fastutil.persistent.Object2ObjectPMap;
import org.metaborg.fastutil.persistent.ObjectOpenHashPSet;
import org.metaborg.fastutil.persistent.ObjectPSet;
import org.metaborg.regexp.IRegExp;
import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IResolvingScopeGraph;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.scopegraph.resolution.BackwardResolvingScopeGraph;
import org.metaborg.unification.ITermUnifier;

public final class Solution implements ISolution {

    private final ITermUnifier unifier;
    private final IResolvingScopeGraph scopeGraph;
    private final Object2ObjectPMap<IOccurrence,IFullPath> resolution;
    private final ObjectPSet<String> errors;

    public Solution(ITermUnifier unifier, IRegExp<ILabel> wf) {
        this(unifier, new BackwardResolvingScopeGraph(wf), new Object2ObjectOpenHashPMap<IOccurrence,IFullPath>(),
                new ObjectOpenHashPSet<String>());
    }

    private Solution(ITermUnifier unifier, IResolvingScopeGraph scopeGraph,
            Object2ObjectPMap<IOccurrence,IFullPath> resolution, ObjectPSet<String> errors) {
        this.unifier = unifier;
        this.scopeGraph = scopeGraph;
        this.resolution = resolution;
        this.errors = errors;
    }

    @Override public ITermUnifier getUnifier() {
        return unifier;
    }

    @Override public Solution setUnifier(ITermUnifier unifier) {
        return new Solution(unifier, scopeGraph, resolution, errors);
    }

    @Override public ObjectPSet<String> getErrors() {
        return errors;
    }

    @Override public Solution setErrors(ObjectPSet<String> errors) {
        return new Solution(unifier, scopeGraph, resolution, errors);
    }


    @Override public IResolvingScopeGraph getScopeGraph() {
        return scopeGraph;
    }

    @Override public ISolution setScopeGraph(IResolvingScopeGraph scopeGraph) {
        return new Solution(unifier, scopeGraph, resolution, errors);
    }

    @Override public Object2ObjectPMap<IOccurrence,IFullPath> getResolution() {
        return resolution;
    }

    @Override public ISolution setResolution(Object2ObjectPMap<IOccurrence,IFullPath> resolution) {
        return new Solution(unifier, scopeGraph, resolution, errors);
    }

}