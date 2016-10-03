package org.metaborg.scopegraph.experimental.path.free;

import org.metaborg.scopegraph.experimental.path.IDeclPath;
import org.metaborg.scopegraph.experimental.path.IFullPath;
import org.metaborg.scopegraph.experimental.path.IRefPath;
import org.metaborg.scopegraph.experimental.path.IScopePath;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.pcollections.PSet;

public class PathConcat {

    public static IFullPath concat(IRefPath left, IDeclPath right) throws PathException {
        return new RefDeclPathConcat(left, right);
    }

    public static IRefPath concat(IRefPath left, IScopePath right) throws PathException {
        return new RefScopePathConcat(left, right);
    }

    public static IDeclPath concat(IScopePath left, IDeclPath right) throws PathException {
        return new ScopeDeclPathConcat(left, right);
    }

    public static IScopePath concat(IScopePath left, IScopePath right) throws PathException {
        return new ScopeScopePathConcat(left, right);
    }

    public static <T> PSet<T> union(PSet<T> left, PSet<T> right) {
        if (left.size() > right.size()) {
            return left.plusAll(right);
        } else {
            return right.plusAll(left);
        }
    }

}