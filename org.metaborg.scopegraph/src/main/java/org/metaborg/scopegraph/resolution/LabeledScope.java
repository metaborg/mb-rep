package org.metaborg.scopegraph.resolution;

import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.ILabeledScope;
import org.metaborg.scopegraph.IScope;


public class LabeledScope implements ILabeledScope {

    private static final long serialVersionUID = -399332140496657153L;

    private final ILabel label;
    private final IScope scope;

    public LabeledScope(ILabel label, IScope scope) {
        this.label = label;
        this.scope = scope;
    }

    @Override public ILabel label() {
        return label;
    }

    @Override public IScope scope() {
        return scope;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + label.hashCode();
        result = prime * result + scope.hashCode();
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LabeledScope other = (LabeledScope) obj;
        if (!label.equals(other.label))
            return false;
        if (!scope.equals(other.scope))
            return false;
        return true;
    }

    @Override public String toString() {
        return label + "/" + scope;
    }

}