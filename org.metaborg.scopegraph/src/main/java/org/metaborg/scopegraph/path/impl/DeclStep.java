package org.metaborg.scopegraph.path.impl;

import org.metaborg.scopegraph.IDecl;
import org.metaborg.scopegraph.path.IDeclStep;

public class DeclStep implements IDeclStep {
    private final IDecl decl;


    public DeclStep(IDecl decl) {
        this.decl = decl;
    }


    @Override public IDecl decl() {
        return decl;
    }
}
