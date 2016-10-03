package org.metaborg.scopegraph;

import java.io.Serializable;

import org.metaborg.scopegraph.path.IDeclPath;
import org.metaborg.scopegraph.path.IFullPath;
import org.pcollections.PSet;

public interface INameResolution extends Serializable {

    PSet<IDeclPath> reachables(IScope scope);

    PSet<IFullPath> reachables(IOccurrence reference);

}