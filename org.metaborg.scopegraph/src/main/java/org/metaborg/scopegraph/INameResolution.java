package org.metaborg.scopegraph;

import java.io.Serializable;

import org.metaborg.scopegraph.wf.IWFDeclPath;
import org.metaborg.scopegraph.wf.IWFFullPath;
import org.pcollections.PSet;

public interface INameResolution extends Serializable {

    PSet<IWFDeclPath> reachables(IScope scope);

    PSet<IWFFullPath> reachables(IOccurrence reference);

}