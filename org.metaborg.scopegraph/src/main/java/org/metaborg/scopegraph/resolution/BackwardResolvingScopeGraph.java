package org.metaborg.scopegraph.resolution;

import org.metaborg.fastutil.persistent.Object2ObjectOpenHashPMultimap;
import org.metaborg.fastutil.persistent.Object2ObjectPMultimap;
import org.metaborg.regexp.IRegExp;
import org.metaborg.regexp.IRegExpMatcher;
import org.metaborg.regexp.RegExpMatcher;
import org.metaborg.regexp.Reverser;
import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.ILabeledOccurrence;
import org.metaborg.scopegraph.ILabeledScope;
import org.metaborg.scopegraph.INameResolution;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeGraph;
import org.metaborg.scopegraph.path.IDeclPath;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.scopegraph.path.PathException;
import org.metaborg.scopegraph.path.backward.DeclPath;
import org.metaborg.scopegraph.path.backward.DirectPath;
import org.metaborg.scopegraph.path.backward.NamedPath;
import org.metaborg.scopegraph.path.backward.RefPath;
import org.metaborg.scopegraph.wf.IWFDeclPath;
import org.metaborg.scopegraph.wf.IWFFullPath;
import org.metaborg.scopegraph.wf.IWFPath;
import org.metaborg.scopegraph.wf.IWFPathVisitor;
import org.metaborg.scopegraph.wf.WFDeclPath;
import org.metaborg.scopegraph.wf.WFFullPath;
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
 *    case (s) ->* [d] unless path is final & not accepting:
 *       for every (s) <-l- (s')
 *          RESOLVE (s') -l-> (s) ->* [d]
 *       for every (s) <- [r] where r matches d:
 *          RESOLVE [r] -> (s) ->* [d]
 *       for every (s) <=l= [d'], [d'] *<- [r'], [r'] <=l= (s')
 *          RESOLVE (s') =l=> [r'] ->* [d'] =l=> (s) ->* [d]
 *    case [r] ->* [d] where path is accepting:
 *       for every [r] <=l= (s), [d] =l=> (s'), (s') ->* [d']:
 *          RESOLVE (s) =l=> [r] ->* [d] =l=> (s') ->* [d']
 * </pre>
 */

public class BackwardResolvingScopeGraph implements IScopeGraph, INameResolution {

    private static final long serialVersionUID = 467691464366824128L;

    // (s) <-l- (s)
    private final Object2ObjectPMultimap<IScope, ILabeledScope> scopeChildren;

    // (s) <- [r]
    private final Object2ObjectPMultimap<IScope, IOccurrence> scopeRefs;

    // [r] <=l= (s)
    private final Object2ObjectPMultimap<IOccurrence, ILabeledScope> refImports;

    // [d] =l=> (s)
    private final Object2ObjectPMultimap<IOccurrence, ILabeledScope> declExports;

    // (s) <=l= [d]
    private final Object2ObjectPMultimap<IScope, ILabeledOccurrence> scopeExports;

    // (s) -> [d] # not necessary for resolution
    private final Object2ObjectPMultimap<IScope, IOccurrence> scopeDecls;

    // (s) ->* [d]
    private final Object2ObjectPMultimap<IScope, IWFDeclPath> scopeReachableDecls;

    // [r] ->* [d]
    private final Object2ObjectPMultimap<IOccurrence, IWFFullPath> refDecls;

    // [d] *<- [r]
    private final Object2ObjectPMultimap<IOccurrence, IWFFullPath> declRefs;

    private final IRegExpMatcher<ILabel> initialWF;

    public BackwardResolvingScopeGraph(IRegExp<ILabel> initialWF) {
        Reverser<ILabel> reverser = new Reverser<>(initialWF.getBuilder());
        IRegExp<ILabel> reverseWF = initialWF.accept(reverser);
        this.initialWF = RegExpMatcher.create(reverseWF);
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

    private BackwardResolvingScopeGraph(IRegExpMatcher<ILabel> initialWF,
            Object2ObjectPMultimap<IScope, ILabeledScope> scopeChildren,
            Object2ObjectPMultimap<IScope, IOccurrence> scopeRefs,
            Object2ObjectPMultimap<IOccurrence, ILabeledScope> refImports,
            Object2ObjectPMultimap<IOccurrence, ILabeledScope> declExports,
            Object2ObjectPMultimap<IScope, ILabeledOccurrence> scopeExports,
            Object2ObjectPMultimap<IScope, IOccurrence> scopeDecls,
            Object2ObjectPMultimap<IScope, IWFDeclPath> scopeReachableDecls,
            Object2ObjectPMultimap<IOccurrence, IWFFullPath> refDecls,
            Object2ObjectPMultimap<IOccurrence, IWFFullPath> declRefs) {
        this.initialWF = initialWF;
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

    @Override
    public BackwardResolvingScopeGraph addDecl(IOccurrence declaration, IScope scope) {
        if(scopeDecls.get(scope).contains(declaration)) {
            return this;
        }
        DeclPath newPath = DeclPath.of(scope, declaration);
        return new BackwardResolvingScopeGraph(initialWF, scopeChildren, scopeRefs, refImports, declExports,
                scopeExports, scopeDecls.put(scope, declaration), scopeReachableDecls, refDecls, declRefs)
                        .resolve(WFDeclPath.of(newPath, initialWF));
    }

    @Override
    public BackwardResolvingScopeGraph addRef(IOccurrence reference, IScope scope) {
        if(scopeRefs.get(scope).contains(reference)) {
            return this;
        }
        Stack<IWFPath> paths = new ObjectArrayList<>();
        for(IWFDeclPath wftail : scopeReachableDecls.get(scope)) {
            try {
                RefPath newPath = RefPath.of(reference, wftail.path());
                paths.push(WFFullPath.of(newPath, wftail.wf()));
            } catch(PathException e) {
            }
        }
        return new BackwardResolvingScopeGraph(initialWF, scopeChildren, scopeRefs.put(scope, reference), refImports,
                declExports, scopeExports, scopeDecls, scopeReachableDecls, refDecls, declRefs).resolve(paths);
    }

    @Override
    public BackwardResolvingScopeGraph addLink(IScope source, ILabel label, IScope target) {
        LabeledScope labeledScope = new LabeledScope(label, source);
        if(scopeChildren.get(target).contains(labeledScope)) {
            return this;
        }
        Stack<IWFPath> paths = new ObjectArrayList<>();
        for(IWFDeclPath wftail : scopeReachableDecls.get(target)) {
            try {
                DirectPath newPath = DirectPath.of(source, label, wftail.path());
                IRegExpMatcher<ILabel> newWF = wftail.wf().match(label);
                paths.push(WFDeclPath.of(newPath, newWF));
            } catch(PathException ex) {
            }
        }
        return new BackwardResolvingScopeGraph(initialWF, scopeChildren.put(target, labeledScope), scopeRefs,
                refImports, declExports, scopeExports, scopeDecls, scopeReachableDecls, refDecls, declRefs)
                        .resolve(paths);
    }

    @Override
    public BackwardResolvingScopeGraph addExport(IOccurrence declaration, ILabel label, IScope scope) {
        LabeledScope labeledScope = new LabeledScope(label, scope);
        if(declExports.get(declaration).contains(labeledScope)) {
            return this;
        }
        Stack<IWFPath> paths = new ObjectArrayList<>();
        for(IWFFullPath wfreach : declRefs.get(declaration)) {
            IFullPath reach = wfreach.path();
            for(ILabeledScope import_ : refImports.get(reach.reference())) {
                for(IWFDeclPath wftail : scopeReachableDecls.get(scope)) {
                    try {
                        NamedPath newPath = NamedPath.of(import_.scope(), import_.label(), reach, wftail.path());
                        IRegExpMatcher<ILabel> newWF = wftail.wf().match(label);
                        paths.push(WFDeclPath.of(newPath, newWF));
                    } catch(PathException ex) {
                    }
                }
            }

        }
        return new BackwardResolvingScopeGraph(initialWF, scopeChildren, scopeRefs, refImports,
                declExports.put(declaration, labeledScope),
                scopeExports.put(scope, new LabeledOccurrence(label, declaration)), scopeDecls, scopeReachableDecls,
                refDecls, declRefs).resolve(paths);
    }

    @Override
    public BackwardResolvingScopeGraph addImport(IOccurrence reference, ILabel label, IScope scope) {
        LabeledScope labeledScope = new LabeledScope(label, scope);
        if(refImports.get(reference).contains(labeledScope)) {
            return this;
        }
        Stack<IWFPath> paths = new ObjectArrayList<>();
        for(IWFFullPath wfreach : refDecls.get(reference)) {
            IFullPath reach = wfreach.path();
            for(ILabeledScope export : declExports.get(reach.declaration())) {
                for(IWFDeclPath wftail : scopeReachableDecls.get(export.scope())) {
                    try {
                        NamedPath newPath = NamedPath.of(scope, export.label(), reach, wftail.path());
                        IRegExpMatcher<ILabel> newWF = wftail.wf().match(export.label());
                        paths.push(WFDeclPath.of(newPath, newWF));
                    } catch(PathException ex) {
                    }
                }

            }
        }
        return new BackwardResolvingScopeGraph(initialWF, scopeChildren, scopeRefs,
                refImports.put(reference, labeledScope), declExports, scopeExports, scopeDecls, scopeReachableDecls,
                refDecls, declRefs).resolve(paths);
    }

    private BackwardResolvingScopeGraph resolve(final IWFPath... initialPaths) {
        return resolve(new ObjectArrayList<>(initialPaths));
    }

    private BackwardResolvingScopeGraph resolve(final Stack<IWFPath> worklist) {
        BackwardResolvingScopeGraph localGraph = this;
        while(!worklist.isEmpty()) {
            final IWFPath wfpath = worklist.pop();
            BackwardResolvingScopeGraph newGraph = wfpath.accept(new IWFPathVisitor<BackwardResolvingScopeGraph>() {

                @Override
                public BackwardResolvingScopeGraph visit(IWFDeclPath wfpath) {
                    IRegExpMatcher<ILabel> wf = wfpath.wf();
                    if(wf.isFinal() && !wf.isAccepting()) {
                        return null;
                    }
                    IDeclPath path = wfpath.path();
                    IScope scope = path.sourceScope();
                    for(ILabeledScope child : scopeChildren.get(scope)) {
                        try {
                            IRegExpMatcher<ILabel> newWF = wf.match(child.label());
                            DirectPath newPath = DirectPath.of(child.scope(), child.label(), path);
                            worklist.push(WFDeclPath.of(newPath, newWF));
                        } catch(PathException e) {
                        }
                    }
                    for(IOccurrence reference : scopeRefs.get(scope)) {
                        try {
                            RefPath newPath = RefPath.of(reference, path);
                            worklist.push(WFFullPath.of(newPath, wf));
                        } catch(PathException e) {
                        }
                    }
                    for(ILabeledOccurrence export : scopeExports.get(scope)) {
                        for(IWFFullPath wfinner : declRefs.get(export.occurrence())) {
                            IFullPath inner = wfinner.path();
                            for(ILabeledScope import_ : refImports.get(inner.reference())) {
                                if(export.label().equals(import_.label())) {
                                    try {
                                        NamedPath newPath = NamedPath.of(import_.scope(), export.label(), inner, path);
                                        IRegExpMatcher<ILabel> newWF = wf.match(export.label());
                                        worklist.push(WFDeclPath.of(newPath, newWF));
                                    } catch(PathException e) {
                                    }
                                }
                            }
                        }

                    }
                    return new BackwardResolvingScopeGraph(initialWF, scopeChildren, scopeRefs, refImports, declExports,
                            scopeExports, scopeDecls, scopeReachableDecls.put(scope, wfpath), refDecls, declRefs);
                }

                @Override
                public BackwardResolvingScopeGraph visit(IWFFullPath wfpath) {
                    if(!wfpath.wf().isAccepting()) {
                        return null;
                    }
                    IFullPath path = wfpath.path();
                    IOccurrence ref = path.reference();
                    IOccurrence decl = path.declaration();
                    for(ILabeledScope import_ : refImports.get(ref)) {
                        for(ILabeledScope export : declExports.get(decl)) {
                            if(export.label().equals(import_.label())) {
                                for(IWFDeclPath wftail : scopeReachableDecls.get(export.scope())) {
                                    IDeclPath tail = wftail.path();
                                    IRegExpMatcher<ILabel> wf = wftail.wf();
                                    try {
                                        NamedPath newPath = NamedPath.of(import_.scope(), export.label(), path, tail);
                                        IRegExpMatcher<ILabel> newWF = wf.match(export.label());
                                        worklist.push(WFDeclPath.of(newPath, newWF));
                                    } catch(PathException e) {
                                    }
                                }
                            }
                        }

                    }
                    return new BackwardResolvingScopeGraph(initialWF, scopeChildren, scopeRefs, refImports, declExports,
                            scopeExports, scopeDecls, scopeReachableDecls, refDecls.put(ref, wfpath),
                            declRefs.put(decl, wfpath));
                }

            });
            if(newGraph != null) {
                localGraph = newGraph;
            }
        }
        return localGraph;
    }

    @Override
    public PSet<IWFDeclPath> reachables(IScope scope) {
        return scopeReachableDecls.get(scope);
    }

    @Override
    public PSet<IWFFullPath> reachables(IOccurrence reference) {
        return refDecls.get(reference);
    }

}