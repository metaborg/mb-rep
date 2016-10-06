package org.metaborg.scopegraph;

import java.io.Serializable;

import org.metaborg.scopegraph.wf.WFDeclPath;
import org.metaborg.scopegraph.wf.WFFullPath;
import org.pcollections.PSet;

public interface INameResolution extends Serializable {

    PSet<WFDeclPath> reachables(IScope scope);

    PSet<WFFullPath> reachables(IOccurrence reference);

}