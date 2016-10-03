package org.metaborg.scopegraph.experimental.resolution;

import org.metaborg.fastutil.persistent.Object2ObjectOpenHashPMultimap;
import org.metaborg.fastutil.persistent.Object2ObjectPMultimap;
import org.metaborg.scopegraph.experimental.ILabel;
import org.metaborg.scopegraph.experimental.ILabeledOccurrence;
import org.metaborg.scopegraph.experimental.ILabeledScope;
import org.metaborg.scopegraph.experimental.INameResolution;
import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.IScopeGraph;
import org.metaborg.scopegraph.experimental.path.IDeclPath;
import org.metaborg.scopegraph.experimental.path.IFullPath;
import org.metaborg.scopegraph.experimental.path.IPath;
import org.metaborg.scopegraph.experimental.path.IPathVisitor;
import org.metaborg.scopegraph.experimental.path.IRefPath;
import org.metaborg.scopegraph.experimental.path.IScopePath;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.scopegraph.experimental.path.backward.DeclPath;
import org.metaborg.scopegraph.experimental.path.backward.DirectPath;
import org.metaborg.scopegraph.experimental.path.backward.NamedPath;
import org.metaborg.scopegraph.experimental.path.backward.RefPath;
import org.pcollections.PSet;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Algorithm description
 * 
 * <pre>
 * ADD:
 *    case (s) -> [d]:
 *       RESOLVE (s) -> [d]
 *    case [r] -> (s):
 *       for every (s) ->* [d] where r matches d:
 *          RESOLVE [r] -> (s) ->* [d]
 *    case (s) -l-> (s'):
 *       for every (s') ->* [d]
 *          RESOLVE (s) -l-> (s') ->* [d]
 *    case [d] =l=> (s):
 *       for every [d] *<- [r], [r] <=l= (s'), (s) ->* [d']:
 *          RESOLVE (s') =l=> [r] ->* [d] =l=> (s) ->* [d']
 *    case (s) =l=> [r]:
 *       for every [r] ->* [d], [d] =l=> (s), (s) ->* [d']:
 *          RESOLVE (s) =l=> [r] ->* [d] =l=> (s) ->* [d']
 *
 * RESOLVE:
 *    case (s) ->* [d]:
 *       for every (s) <-l- (s')
 *          RESOLVE (s') -l-> (s) ->* [d]
 *       for every (s) <- [r] where r matches d:
 *          RESOLVE [r] -> (s) ->* [d]
 *       for every (s) <=l= [d'], [d'] *<- [r'], [r'] <=l= (s')
 *          RESOLVE (s') =l=> [r'] ->* [d'] =l=> (s) ->* [d]
 *    case [r] ->* [d]:
 *       for every [r] <=l= (s), [d] =l=> (s'), (s') ->* [d']:
 *          RESOLVE (s) =l=> [r] ->* [d] =l=> (s') ->* [d']
 * </pre>
 */

public class BackwardResolvingScopeGraph implements IScopeGraph, INameResolution {

    private static final long serialVersionUID = 467691464366824128L;

    // (s) <-l- (s)
    private final Object2ObjectPMultimap<IScope,ILabeledScope> scopeChildren;

    // (s) <- [r]
    private final Object2ObjectPMultimap<IScope,IOccurrence> scopeRefs;

    // [r] <=l= (s)
    private final Object2ObjectPMultimap<IOccurrence,ILabeledScope> refImports;

    // [d] =l=> (s)
    private final Object2ObjectPMultimap<IOccurrence,ILabeledScope> declExports;

    // (s) <=l= [d]
    private final Object2ObjectPMultimap<IScope,ILabeledOccurrence> scopeExports;

    // (s) -> [d] # not necessary for resolution
    private final Object2ObjectPMultimap<IScope,IOccurrence> scopeDecls;

    // (s) ->* [d]
    private final Object2ObjectPMultimap<IScope,IDeclPath> scopeReachableDecls;


    // [r] ->* [d]
    private final Object2ObjectPMultimap<IOccurrence,IFullPath> refDecls;

    // [d] *<- [r]
    private final Object2ObjectPMultimap<IOccurrence,IFullPath> declRefs;


    public BackwardResolvingScopeGraph() {
        this.scopeChildren = new Object2ObjectOpenHashPMultimap<>();
        this.scopeRefs = new Object2ObjectOpenHashPMultimap<>();
        this.refImports = new Object2ObjectOpenHashPMultimap<>();
        this.declExports = new Object2ObjectOpenHashPMultimap<>();
        this.scopeExports = new Object2ObjectOpenHashPMultimap<>();
        this.scopeDecls = new Object2ObjectOpenHashPMultimap<>();
        this.scopeReachableDecls = new Object2ObjectOpenHashPMultimap<>();
        this.refDecls = new Object2ObjectOpenHashPMultimap<>();
        this.declRefs = new Object2ObjectOpenHashPMultimap<>();
    }

    private BackwardResolvingScopeGraph(Object2ObjectPMultimap<IScope,ILabeledScope> scopeChildren,
            Object2ObjectPMultimap<IScope,IOccurrence> scopeRefs,
            Object2ObjectPMultimap<IOccurrence,ILabeledScope> refImports,
            Object2ObjectPMultimap<IOccurrence,ILabeledScope> declExports,
            Object2ObjectPMultimap<IScope,ILabeledOccurrence> scopeExports,
            Object2ObjectPMultimap<IScope,IOccurrence> scopeDecls,
            Object2ObjectPMultimap<IScope,IDeclPath> scopeReachableDecls,
            Object2ObjectPMultimap<IOccurrence,IFullPath> refDecls,
            Object2ObjectPMultimap<IOccurrence,IFullPath> declRefs) {
        this.scopeChildren = scopeChildren;
        this.scopeRefs = scopeRefs;
        this.refImports = refImports;
        this.declExports = declExports;
        this.scopeExports = scopeExports;
        this.scopeDecls = scopeDecls;
        this.scopeReachableDecls = scopeReachableDecls;
        this.refDecls = refDecls;
        this.declRefs = declRefs;
    }


    @Override public BackwardResolvingScopeGraph addDecl(IOccurrence declaration, IScope scope) {
        return new BackwardResolvingScopeGraph(scopeChildren, scopeRefs, refImports, declExports, scopeExports,
                scopeDecls.put(scope, declaration), scopeReachableDecls, refDecls, declRefs)
                        .resolve(new DeclPath(scope, declaration));
    }

    @Override public BackwardResolvingScopeGraph addRef(IOccurrence reference, IScope scope) {
        Stack<IPath> paths = new ObjectArrayList<>();
        for (IDeclPath tail : scopeReachableDecls.get(scope)) {
            try {
                paths.push(new RefPath(reference, tail));
            } catch (PathException e) {
            }
        }
        return new BackwardResolvingScopeGraph(scopeChildren, scopeRefs.put(scope, reference), refImports, declExports,
                scopeExports, scopeDecls, scopeReachableDecls, refDecls, declRefs).resolve(paths);
    }

    @Override public BackwardResolvingScopeGraph addLink(IScope source, ILabel label, IScope target) {
        Stack<IPath> paths = new ObjectArrayList<>();
        for (IDeclPath tail : scopeReachableDecls.get(target)) {
            try {
                paths.push(new DirectPath(source, label, tail));
            } catch (PathException ex) {
            }
        }
        return new BackwardResolvingScopeGraph(scopeChildren.put(target, new LabeledScope(label, source)), scopeRefs,
                refImports, declExports, scopeExports, scopeDecls, scopeReachableDecls, refDecls, declRefs)
                        .resolve(paths);
    }

    @Override public BackwardResolvingScopeGraph addExport(IOccurrence declaration, ILabel label, IScope scope) {
        Stack<IPath> paths = new ObjectArrayList<>();
        for (IFullPath reach : declRefs.get(declaration)) {
            for (ILabeledScope import_ : refImports.get(reach.reference())) {
                for (IDeclPath tail : scopeReachableDecls.get(scope)) {
                    try {
                        paths.push(new NamedPath(import_.scope(), import_.label(), reach, tail));
                    } catch (PathException ex) {
                    }
                }
            }

        }
        return new BackwardResolvingScopeGraph(scopeChildren, scopeRefs, refImports,
                declExports.put(declaration, new LabeledScope(label, scope)),
                scopeExports.put(scope, new LabeledOccurrence(label, declaration)), scopeDecls, scopeReachableDecls,
                refDecls, declRefs).resolve(paths);
    }

    @Override public BackwardResolvingScopeGraph addImport(IOccurrence reference, ILabel label, IScope scope) {
        Stack<IPath> paths = new ObjectArrayList<>();
        for (IFullPath reach : refDecls.get(reference)) {
            for (ILabeledScope export : declExports.get(reach.declaration())) {
                for (IDeclPath tail : scopeReachableDecls.get(export.scope())) {
                    try {
                        paths.push(new NamedPath(scope, export.label(), reach, tail));
                    } catch (PathException ex) {
                    }
                }

            }
        }
        return new BackwardResolvingScopeGraph(scopeChildren, scopeRefs,
                refImports.put(reference, new LabeledScope(label, scope)), declExports, scopeExports, scopeDecls,
                scopeReachableDecls, refDecls, declRefs).resolve(paths);
    }


    private BackwardResolvingScopeGraph resolve(final IPath... initialPaths) {
        return resolve(new ObjectArrayList<>(initialPaths));
    }

    private BackwardResolvingScopeGraph resolve(final Stack<IPath> worklist) {
        BackwardResolvingScopeGraph localGraph = this;
        while (!worklist.isEmpty()) {
            try {
                final IPath path = worklist.pop();
                localGraph = path.accept(new IPathVisitor<BackwardResolvingScopeGraph>() {

                    @Override public BackwardResolvingScopeGraph visit(IDeclPath path) {
                        IScope scope = path.sourceScope();
                        for (ILabeledScope child : scopeChildren.get(scope)) {
                            try {
                                worklist.push(new DirectPath(child.scope(), child.label(), path));
                            } catch (PathException e) {
                            }
                        }
                        for (IOccurrence reference : scopeRefs.get(scope)) {
                            try {
                                worklist.push(new RefPath(reference, path));
                            } catch (PathException e) {
                            }
                        }
                        for (ILabeledOccurrence export : scopeExports.get(scope)) {
                            for (IFullPath inner : declRefs.get(export.occurrence())) {
                                for (ILabeledScope import_ : refImports.get(inner.reference())) {
                                    if (export.label().equals(import_.label())) {
                                        try {
                                            worklist.push(new NamedPath(import_.scope(), export.label(), inner, path));
                                        } catch (PathException e) {
                                        }
                                    }
                                }
                            }

                        }
                        return new BackwardResolvingScopeGraph(scopeChildren, scopeRefs, refImports, declExports,
                                scopeExports, scopeDecls, scopeReachableDecls.put(scope, path), refDecls, declRefs);
                    }

                    @Override public BackwardResolvingScopeGraph visit(IFullPath path) {
                        IOccurrence ref = path.reference();
                        IOccurrence decl = path.declaration();
                        for (ILabeledScope import_ : refImports.get(ref)) {
                            for (ILabeledScope export : declExports.get(decl)) {
                                if (export.label().equals(import_.label())) {
                                    for (IDeclPath tail : scopeReachableDecls.get(export.scope())) {
                                        try {
                                            worklist.push(new NamedPath(import_.scope(), export.label(), path, tail));
                                        } catch (PathException e) {
                                        }
                                    }
                                }
                            }

                        }
                        return new BackwardResolvingScopeGraph(scopeChildren, scopeRefs, refImports, declExports,
                                scopeExports, scopeDecls, scopeReachableDecls, refDecls.put(ref, path),
                                declRefs.put(decl, path));
                    }

                    @Override public BackwardResolvingScopeGraph visit(IRefPath path) throws PathException {
                        throw new IllegalStateException();
                    }

                    @Override public BackwardResolvingScopeGraph visit(IScopePath path) throws PathException {
                        throw new IllegalStateException();
                    }

                });
            } catch (PathException e) {
                throw new IllegalStateException(e);
            }
        }
        return localGraph;
    }


    @Override public PSet<IDeclPath> reachables(IScope scope) {
        return scopeReachableDecls.get(scope);
    }

    @Override public PSet<IFullPath> reachables(IOccurrence reference) {
        return refDecls.get(reference);
    }

}