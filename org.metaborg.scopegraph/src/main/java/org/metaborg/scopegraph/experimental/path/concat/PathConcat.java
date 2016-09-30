package org.metaborg.scopegraph.experimental.path.concat;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.DeclPath;
import org.metaborg.scopegraph.experimental.path.FullPath;
import org.metaborg.scopegraph.experimental.path.IPath;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.scopegraph.experimental.path.RefPath;
import org.metaborg.scopegraph.experimental.path.ScopePath;
import org.pcollections.PSet;

public class PathConcat {

    public static FullPath concat(RefPath left, DeclPath right) throws PathException {
        return new RefDeclPathConcat(left, right);
    }

    public static RefPath concat(RefPath left, ScopePath right) throws PathException {
        return new RefScopePathConcat(left, right);
    }

    public static DeclPath concat(ScopePath left, DeclPath right) throws PathException {
        return new ScopeDeclPathConcat(left, right);
    }

    public static ScopePath concat(ScopePath left, ScopePath right) throws PathException {
        return new ScopeScopePathConcat(left, right);
    }

    public static <T> PSet<T> union(PSet<T> left, PSet<T> right) {
        if (left.size() > right.size()) {
            return left.plusAll(right);
        } else {
            return right.plusAll(left);
        }
    }

    public static boolean cyclic(IPath left, IPath right, IScope connection) {
        boolean swap = left.size() < right.size();
        PSet<IScope> larger = swap ? right.scopes() : left.scopes();
        PSet<IScope> smaller = swap ? left.scopes() : right.scopes();
        for (IScope scope : smaller) {
            if (!scope.equals(connection) && larger.contains(scope)) {
                return true;
            }
        }
        return false;
    }

    public static boolean recursive(IPath path, IOccurrence reference) {
        return path.references().contains(reference);
    }

}