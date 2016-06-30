package org.metaborg.scopegraph.resolution;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.metaborg.scopegraph.Identifier;
import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Labels;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class Visibility implements PathVisitor<Void> {
    ILogger logger = LoggerUtils.logger(Visibility.class);

    final ScopeGraph scopeGraph;
    final Comparator<Label> order;
    
    final Deque<Path> stack = new ArrayDeque<>(); 
    final Multimap<Occurrence,Path> pathsToDecl = HashMultimap.create();
    final Multimap<Scope,Path> pathsFromScope = HashMultimap.create();
    final Multimap<Occurrence,Path> pathsFromRef = HashMultimap.create();

    final BiLinkedHashMultimap<Path,Path> pathDependencies = new BiLinkedHashMultimap<Path, Path>();
 
    public Visibility(ScopeGraph scopeGraph, Comparator<Label> order) {
        this.scopeGraph = scopeGraph;
        this.order = order;
        resolve();
    }
 
    public void print() {
        System.out.println("=== Visibility ===");
        for ( Scope s : pathsFromScope.keySet() ) {
            System.out.println("Scope "+s+":");
            for ( Path p : pathsFromScope.get(s) ) {
                System.out.println("* "+p);
            }
        }
        System.out.println();
        for ( Occurrence ref : pathsFromRef.keySet() ) {
            System.out.println("Reference "+ref+":");
            for ( Path path : pathsFromRef.get(ref) ) {
                System.out.println("* "+path);
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
                    addPathFromScope(scope, n);
                    pathDependencies.put(n, r);
                    pathDependencies.putAll(n, pathDependencies.get(r));
                    pathDependencies.putAll(n, pathDependencies.get(path));
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
                addPathFromScope(scope2, e);
                pathDependencies.putAll(e, pathDependencies.get(path));
                stack.push(e);
            }
        }
    }

    private void addNamed(Scope scope, Path path) {
        for ( Pair<Label,Occurrence> ld : scopeGraph.getExports(scope) ) {
            Label label = ld.getLeft();
            for ( Path inner : pathsToDecl.get(ld.getRight()) ) {
                for ( Scope scope2 : scopeGraph.getImports(((R)inner).getReference(),label) ) {
                    N n = new N(scope2,label,inner,path);
                    addPathFromScope(scope2, n);
                    pathDependencies.put(n, inner);
                    pathDependencies.putAll(n, pathDependencies.get(inner));
                    pathDependencies.putAll(n, pathDependencies.get(path));
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
                addPathToDecl(decl, r);
                addPathFromRef(ref, r);
                pathDependencies.putAll(r, pathDependencies.get(path));
                stack.push(r);
            }
        }
    }

    private void addPathFromScope(Scope scope, Path path) {
        if ( addToPaths(pathsFromScope.get(scope).iterator(), path) ) {
            pathsFromScope.put(scope, path);
        }
    }
 
    private void addPathToDecl(Occurrence decl, R path) {
        @SuppressWarnings("unchecked")
        Iterator<Path> paths = (Iterator<Path>) ((Object) pathsToDecl.get(decl).iterator());
        if ( addToPaths(paths, path) ) {
            pathsToDecl.put(decl, path);
        }
    }
 
    private void addPathFromRef(Occurrence ref, R path) {
        @SuppressWarnings("unchecked")
        Iterator<Path> paths = (Iterator<Path>) ((Object) pathsFromRef.get(ref).iterator());
        if ( addToPaths(paths, path) ) {
            pathsFromRef.put(ref, path);
        }
    }
 
    private boolean addToPaths(Iterator<Path> paths, Path path) {
        Identifier id = Paths.getDeclaration(path).getId();
        List<Label> labels = Paths.labels(path);
        while ( paths.hasNext() ) {
            Path path2 = paths.next();
            Identifier id2 = Paths.getDeclaration(path2).getId();
            List<Label> labels2 = Paths.labels(path2);
            if ( !id.equals(id2) ) {
                continue;
            }
            int diff = Labels.compare(labels,labels2,order);
            if ( diff < 0 ) {
                System.out.println("Drop "+path2);
                for ( Path invalidated : pathDependencies.getInverse(path2) ) {
                    System.out.println("Invalidated "+invalidated);
                    removeAll(pathsFromScope,invalidated);
                    removeAll(pathsToDecl,invalidated);
                    removeAll(pathsFromRef,invalidated);
                    stack.remove(invalidated);
                }
                pathDependencies.removeAllInverse(path2);
                paths.remove();
            } else if ( diff > 0 ) {
                return false;
            }
        }
        return true;
    }
 
    private <K,V> void removeAll(Multimap<K,V> map, V value) {
        for ( K key : Lists.newArrayList(map.keySet()) ) {
            map.remove(key, value);
        }
    }
    
    public Iterable<Path> env(Scope scope) {
        return pathsFromScope.get(scope);
    }

    @SuppressWarnings("unchecked")
    public Iterable<Path> resolvePaths(Occurrence reference) {
        return (Iterable<Path>) ((Object) pathsFromRef.get(reference));
    }

    public Iterable<Occurrence> resolveDeclarations(Occurrence reference) {
        List<Occurrence> decls = Lists.newArrayList();
        for ( Path path : pathsFromRef.get(reference) ) {
            decls.add(Paths.getDeclaration(path));
        }
        return decls;
    }

}
