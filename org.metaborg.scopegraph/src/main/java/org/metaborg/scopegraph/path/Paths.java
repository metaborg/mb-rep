package org.metaborg.scopegraph.path;

import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.pcollections.PSet;

public class Paths {

    public static boolean isCyclic(IPath left, IPath right, IScope connection) {
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

    public static boolean isRecursive(IPath path, IOccurrence reference) {
        return path.references().contains(reference);
    }

}