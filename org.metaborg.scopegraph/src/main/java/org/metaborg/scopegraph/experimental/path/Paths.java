package org.metaborg.scopegraph.experimental.path;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.pcollections.PSet;

public class Paths {

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