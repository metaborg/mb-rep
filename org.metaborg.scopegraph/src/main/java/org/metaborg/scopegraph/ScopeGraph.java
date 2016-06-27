package org.metaborg.scopegraph;

import org.apache.commons.lang3.tuple.Pair;

public interface ScopeGraph {

    Iterable<Pair<Label,Scope>> getDirectEdges(Scope scope);

    Iterable<Occurrence> getDeclarations(Scope scope);

    Iterable<Occurrence> getReferences(Scope scope, Identifier id);

    Iterable<Pair<Label,Occurrence>> getExports(Scope scope);

    Iterable<Pair<Label,Scope>> getExports(Occurrence occurrence);

    Iterable<Scope> getImports(Occurrence ref, Label left);

    Iterable<Scope> getScopes();

}