package org.metaborg.scopegraph.resolution;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class CalculusVisibility {
    ILogger logger = LoggerUtils.logger(CalculusVisibility.class);

    final Comparator<Label> order;
    final Reachability reachability;
    
    final Multimap<Scope,Path> pathsFromScope = HashMultimap.create();
    final Multimap<Occurrence,Path> pathsFromRef = HashMultimap.create();
 
    public CalculusVisibility(ScopeGraph scopeGraph, Comparator<Label> order) {
        this.order = order;
        this.reachability = new Reachability(scopeGraph);
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
            for ( Path p : pathsFromRef.get(ref) ) {
                System.out.println("* "+p);
            }
        }
    }

    private void resolve() {
        for ( Occurrence ref : reachability.pathsFromRef.keySet() ) {
            prune(reachability.pathsFromRef.get(ref), pathsFromRef.get(ref));
        }
        for ( Scope scope : reachability.pathsFromScope.keySet() ) {
            prune(reachability.pathsFromScope.get(scope), pathsFromScope.get(scope));
        }
    }

    private void prune(Iterable<Path> reachable, Collection<Path> visible) {
        Iterator<Path> rit = reachable.iterator();
        while ( rit.hasNext() ) {
            Path r = rit.next();
            Iterator<Path> vit = visible.iterator();
            boolean shadowed = false;
            while ( vit.hasNext() ) {
                Path v = vit.next();
                int o = compare(r, v);
                System.out.println("compare("+r+","+v+") = "+o);
                if ( o < 0 ) {
                    vit.remove();
                } else if ( o > 0 ) {
                    shadowed = true;
                }
            }
            if ( !shadowed ) {
                visible.add(r);
            }
        }
    }

    private int compare(final Path p1, final Path p2) {
        return p1.visit(new PathVisitor<Integer>() {
            @Override public Integer visit(final R r) {
                return p2.visit(new PathVisitor<Integer>() {
                    @Override public Integer visit(R r2) {
                        return compare(r.getTail(), r2.getTail());
                    }
                    @Override public Integer visit(E e2) {
                        throw new IllegalArgumentException();
                    }
                    @Override public Integer visit(N n2) {
                        throw new IllegalArgumentException();
                    }
                    @Override public Integer visit(D d2) {
                        throw new IllegalArgumentException();
                    }
                });
            }
            @Override public Integer visit(final E e) {
                return p2.visit(new PathVisitor<Integer>() {
                    @Override public Integer visit(R r2) {
                        throw new IllegalArgumentException();
                    }
                    @Override public Integer visit(E e2) {
                        int o = order.compare(e.getLabel(), e2.getLabel());
                        if ( o == 0 ) {
                            o = compare(e.getTail(), e2.getTail());
                        }
                        return o;
                    }
                    @Override public Integer visit(N n2) {
                        int o = order.compare(e.getLabel(), n2.getLabel());
                        if ( o == 0 ) {
                            o = compare(e.getTail(), n2.getTail());
                        }
                        return o;
                    }
                    @Override public Integer visit(D d2) {
                        return order.compare(e.getLabel(), Labels.D);
                    }
                });
            }
            @Override public Integer visit(final N n) {
                return p2.visit(new PathVisitor<Integer>() {
                    @Override public Integer visit(R r2) {
                        throw new IllegalArgumentException();
                    }
                    @Override public Integer visit(E e2) {
                        int o = order.compare(n.getLabel(), e2.getLabel());
                        if ( o == 0 ) {
                            o = compare(n.getTail(), e2.getTail());
                        }
                        return o;
                    }
                    @Override public Integer visit(N n2) {
                        int o = order.compare(n.getLabel(), n2.getLabel());
                        if ( o == 0 && Paths.getReference(n.getInner()).equals(Paths.getReference(n2.getInner()))) {
                            o = compare(n.getInner(), n2.getInner());
                        }
                        if ( o == 0 ) {
                            o = compare(n.getTail(), n2.getTail());
                        }
                        return o;
                    }
                    @Override public Integer visit(D d2) {
                        return order.compare(n.getLabel(), Labels.D);
                    }
                });
            }
            @Override public Integer visit(final D d) {
                return p2.visit(new PathVisitor<Integer>() {
                    @Override public Integer visit(R r2) {
                        throw new IllegalArgumentException();
                    }
                    @Override public Integer visit(E e2) {
                        return order.compare(Labels.D, e2.getLabel());
                    }
                    @Override public Integer visit(N n2) {
                        return order.compare(Labels.D, n2.getLabel());
                    }
                    @Override public Integer visit(D d2) {
                        return 0;
                    }
                });
            }
        });
    }
    
    public Collection<Path> env(Scope scope) {
        return pathsFromScope.get(scope);
    }

    public Collection<Path> resolve(Occurrence reference) {
        return pathsFromRef.get(reference);
    }

}
