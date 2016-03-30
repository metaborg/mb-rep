package org.metaborg.scopegraph.path.impl;

import java.util.List;

import javax.annotation.Nullable;

import org.metaborg.scopegraph.IDecl;
import org.metaborg.scopegraph.path.IDeclStep;
import org.metaborg.scopegraph.path.IPath;
import org.metaborg.scopegraph.path.IStep;

public class Path implements IPath {
    private final List<IStep> steps;
    private final boolean complete;
    private final IDecl decl;


    public Path(List<IStep> steps) {
        this.steps = steps;

        final IStep lastStep = steps.get(steps.size() - 1);
        if(lastStep instanceof IDeclStep) {
            final IDeclStep declStep = (IDeclStep) lastStep;
            this.complete = true;
            this.decl = declStep.decl();
        } else {
            this.complete = false;
            this.decl = null;
        }
    }

    @Override public Iterable<IStep> steps() {
        return steps;
    }

    @Override public boolean complete() {
        return complete;
    }

    @Override public @Nullable IDecl decl() {
        return decl;
    }
}
