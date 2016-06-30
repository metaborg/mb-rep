package org.metaborg.scopegraph.impl;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.metaborg.scopegraph.Identifier;
import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.Scope;
import org.metaborg.scopegraph.ScopeGraph;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class DefaultScopeGraph implements ScopeGraph {

    private final Set<Scope> scopes = Sets.newHashSet();
    private final Multimap<Scope,Occurrence> declarations = HashMultimap.create();
    private final Multimap<Scope,Pair<Label,Scope>> directEdges = HashMultimap.create();
    private final Multimap<Scope,Pair<Label,Occurrence>> scopeExports = HashMultimap.create();
    private final Multimap<Occurrence,Pair<Label,Scope>> declarationExports = HashMultimap.create();

    private final Multimap<Pair<Occurrence,Label>,Scope> imports = HashMultimap.create();
    private final Multimap<Pair<Scope,Identifier>,Occurrence> references = HashMultimap.create();
 
    private long lastScope = 0;
    public DefaultScopeGraph() {
    }
 
    //**************************************************************************

    public Scope createScope() {
        final long scope = lastScope++;
        Scope s = new Scope() {
            @Override
            public String toString() {
                return String.format("s%d", scope);
            }
        };
        scopes.add(s);
        return s;
    }
 
    public void addDeclaration(Scope in, Occurrence declaration) {
        declarations.put(in, declaration);
    }

    public void addReference(Occurrence reference, Scope in) {
        references.put(Pair.of(in, reference.getId()), reference);
    }
    
    public void addDirectEdge(Scope from, Label label, Scope to) {
        directEdges.put(to, Pair.of(label, from));
    }
    
    public void addImportEdge(Scope from, Label label, Occurrence reference) {
        imports.put(Pair.of(reference, label), from);
    }
    
    public void addExportEdge(Occurrence declaration, Label label, Scope to) {
        scopeExports.put(to, Pair.of(label,declaration));
        declarationExports.put(declaration, Pair.of(label, to));
    }
    
    //**************************************************************************
    
    @Override
    public Iterable<Pair<Label, Scope>> getDirectEdges(Scope scope) {
        return directEdges.get(scope);
    }

    @Override
    public Iterable<Occurrence> getDeclarations(Scope scope) {
        return declarations.get(scope);
    }

    @Override
    public Iterable<Occurrence> getReferences(Scope scope, Identifier id) {
        return references.get(Pair.of(scope, id));
    }

    @Override
    public Iterable<Pair<Label, Occurrence>> getExports(Scope scope) {
        return scopeExports.get(scope);
    }

    @Override
    public Iterable<Pair<Label, Scope>> getExports(Occurrence occurrence) {
        return declarationExports.get(occurrence);
    }

    @Override
    public Iterable<Scope> getImports(Occurrence reference, Label label) {
        return imports.get(Pair.of(reference,label));
    }

    @Override
    public Iterable<Scope> getScopes() {
        return scopes;
    }
 
}
