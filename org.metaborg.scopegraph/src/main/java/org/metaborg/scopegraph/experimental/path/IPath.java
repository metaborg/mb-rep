package org.metaborg.scopegraph.experimental.path;

import java.io.Serializable;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.pcollections.PSet;

public interface IPath extends Iterable<IStep>, Serializable {

    int size();

    PSet<IScope> scopes();

    PSet<IOccurrence> references();

    <T> T accept(IPathVisitor<T> visitor) throws PathException;

}