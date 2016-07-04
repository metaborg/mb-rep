package org.metaborg.scopegraph.resolution;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.lang3.tuple.Pair;
import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.Scope;
import org.metaborg.scopegraph.ScopeGraph;
import org.metaborg.scopegraph.resolution.path.D;
import org.metaborg.scopegraph.resolution.path.E;
import org.metaborg.scopegraph.resolution.path.N;
import org.metaborg.scopegraph.resolution.path.Path;
import org.metaborg.scopegraph.resolution.path.Path.PathVisitor;
import org.metaborg.scopegraph.resolution.path.Paths;
import org.metaborg.scopegraph.resolution.path.R;
import org.metaborg.util.collection.BiLinkedHashMultimap;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Reachability implements PathVisitor<Void> {
    ILogger logger = LoggerUtils.logger(Reachability.class);

    final ScopeGraph scopeGraph;
    final Deque<Path> stack = new ArrayDeque<>(); 
    final Multimap<Scope,Path> pathsFromScope = HashMultimap.create();
    final Multimap<Occurrence,Path> pathsToDecl = HashMultimap.create();
    final Multimap<Occurrence,Path> pathsFromRef = HashMultimap.create();
 
    final BiLinkedHashMultimap<Path,Path> partialPathDependencies =
            new BiLinkedHashMultimap<Path, Path>();
    final BiLinkedHashMultimap<Path,Path> fullPathDependencies =
            new BiLinkedHashMultimap<Path, Path>();
 
    public Reachability(ScopeGraph scopeGraph) {
        this.scopeGraph = scopeGraph;
        resolve();
    }
 
    public void print() {
        System.out.println("=== Reachability ===");
        for ( Scope s : pathsFromScope.keySet() ) {
            System.out.println("Scope "+s+":");
            for ( Path p : pathsFromScope.get(s) ) {
                System.out.println("* "+p);
            }
        }
        System.out.println();
        for ( Occurrence ref : pathsFromRef.keySet() ) {
            System.out.println("Reference "+ref+":");
            for ( Path p : pathsFromRef.get(ref) ) {
                System.out.println("* "+p);
            }
        }
    }

    private void resolve() {
        seed();
        Path path;
        while ( (path = stack.poll()) != null ) {
            path.visit(this);
        } 
    }

    private void seed() {
        for ( Scope scope : scopeGraph.getScopes() ) {
            for ( Occurrence declaration : scopeGraph.getDeclarations(scope) ) {
                D d = new D(declaration, scope);
                pathsFromScope.put(scope, d);
                stack.push(d);
            }
        }
    }
 
    @Override public Void visit(R r) {
        Occurrence decl = Paths.getDeclaration(r);
        for ( Pair<Label,Scope> ls : scopeGraph.getExports(decl) ) {
            Label label = ls.getLeft();
            for ( Scope scope : scopeGraph.getImports(r.getReference(), label) ) {
                for ( Path path : pathsFromScope.get(ls.getRight()) ) {
                    N n = new N(scope,label,r,path);
                    pathsFromScope.put(scope, n);
                    partialPathDependencies.put(n, r);
                    stack.push(n);
                }
            }
        }
        return null;
    }

    @Override public Void visit(E e) {
        return visit(e.getScope(), e);
    }

    @Override public Void visit(N n) {
        return visit(n.getScope(), n);
    }

    @Override public Void visit(D d) {
        return visit(d.getScope(), d);
    }

    private Void visit(Scope scope, Path path) {
        addDirect(scope, path);
        addNamed(scope, path);
        addReachable(scope, path);
        return null;
    }
    
    private void addDirect(Scope scope, Path path) {
        for ( Pair<Label,Scope> ls : scopeGraph.getDirectEdges(scope) ) {
            if ( !Paths.visits(path,ls.getRight()) ) {
                Scope scope2 = ls.getRight();
                E e = new E(scope2, ls.getLeft(), path);
                pathsFromScope.put(scope2, e);
                partialPathDependencies.putAll(e, partialPathDependencies.get(path));
                stack.push(e);
            }
        }
    }

    private void addNamed(Scope scope, Path path) {
        for ( Pair<Label,Occurrence> ld : scopeGraph.getExports(scope) ) {
            Label label = ld.getLeft();
            for ( Path inner : pathsToDecl.get(ld.getRight()) ) {
                for ( Scope scope2 : scopeGraph.getImports(Paths.getReference(inner),label) ) {
                    N n = new N(scope2,label,inner,path);
                    pathsFromScope.put(scope2, n);
                    partialPathDependencies.put(n, inner);
                    partialPathDependencies.putAll(n, partialPathDependencies.get(path));
                    stack.push(n);
                }
            }
        }
    }

    private void addReachable(Scope scope, Path path) {
        Occurrence decl = Paths.getDeclaration(path);
        for ( Occurrence ref : scopeGraph.getReferences(scope, decl.getId()) ) {
            if ( !Paths.imports(path,ref) ) {
                R r = new R(ref, path);
                pathsToDecl.put(decl, r);
                pathsFromRef.put(ref, r);
                fullPathDependencies.putAll(r, partialPathDependencies.get(path));
                stack.push(r);
            }
        }
    }

    public Iterable<Path> env(Scope scope) {
        return pathsFromScope.get(scope);
    }

    public Iterable<Path> resolve(Occurrence reference) {
        return pathsFromRef.get(reference);
    }

}