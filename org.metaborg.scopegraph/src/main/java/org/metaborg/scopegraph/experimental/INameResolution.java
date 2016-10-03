package org.metaborg.scopegraph.experimental;

import java.io.Serializable;

import org.metaborg.scopegraph.experimental.path.IDeclPath;
import org.metaborg.scopegraph.experimental.path.IFullPath;
import org.pcollections.PSet;

public interface INameResolution extends Serializable {

    PSet<IDeclPath> reachables(IScope scope);

    PSet<IFullPath> reachables(IOccurrence reference);

}