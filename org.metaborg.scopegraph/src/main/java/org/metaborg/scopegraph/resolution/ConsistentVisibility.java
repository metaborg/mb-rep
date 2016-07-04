package org.metaborg.scopegraph.resolution;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.metaborg.scopegraph.Label;
import org.metaborg.scopegraph.Labels;
import org.metaborg.scopegraph.Occurrence;
import org.metaborg.scopegraph.Scope;
import org.metaborg.scopegraph.ScopeGraph;
import org.metaborg.scopegraph.resolution.path.Path;
import org.metaborg.scopegraph.resolution.path.Paths;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ConsistentVisibility {
    ILogger logger = LoggerUtils.logger(ConsistentVisibility.class);

    final Comparator<Label> order;
    final Reachability reachability;
    
    final Multimap<Scope,Path> pathsFromScope = HashMultimap.create();
    final Multimap<Occurrence,Path> pathsFromRef = HashMultimap.create();
    
    public ConsistentVisibility(ScopeGraph scopeGraph, Comparator<Label> order) {
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
        List<Path> pruned = Lists.newArrayList();
        for ( Occurrence ref : reachability.pathsFromRef.keySet() ) {
            pruned.addAll(prune(reachability.pathsFromRef.get(ref), pathsFromRef.get(ref)));
        }
        for ( Scope scope : reachability.pathsFromScope.keySet() ) {
            prune(reachability.pathsFromScope.get(scope), pathsFromScope.get(scope));
        }
        prune(pruned);
    }

    private Collection<Path> prune(Iterable<Path> reachable, Collection<Path> visible) {
        List<Path> pruned = Lists.newArrayList();
        Iterator<Path> rit = reachable.iterator();
        while ( rit.hasNext() ) {
            Path r = rit.next();
            Iterator<Path> vit = visible.iterator();
            boolean shadowed = false;
            while ( vit.hasNext() ) {
                Path v = vit.next();
                int o = Labels.compare(Paths.labels(r), Paths.labels(v), order);
                if ( o < 0 ) {
                    vit.remove();
                    pruned.add(v);
                } else if ( o > 0 ) {
                    shadowed = true;
                }
            }
            if ( !shadowed ) {
                visible.add(r);
            } else {
                pruned.add(r);
            }
        }
        return pruned;
    }

    private void prune(Iterable<Path> pruned) {
        for ( Path path : pruned ) {
            Set<Path> partialDeps = reachability.partialPathDependencies.getInverse(path);
            pathsFromScope.values().removeAll(partialDeps);
            Set<Path> fullDeps = reachability.fullPathDependencies.getInverse(path);
            pathsFromRef.values().removeAll(fullDeps);
        }
    }

    public Collection<Path> env(Scope scope) {
        return pathsFromScope.get(scope);
    }

    public Collection<Path> resolve(Occurrence reference) {
        return pathsFromRef.get(reference);
    }

}
