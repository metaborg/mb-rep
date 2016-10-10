package org.metaborg.nabl2.solution;

import java.util.Collection;

public interface IScope {

    Collection<IOccurrence> declarations();

    Collection<ILabeledScope> directEdges();

    Collection<ILabeledOccurrence> importEdges();

    Collection<ILabeledOccurrence> exportEdges();

    Collection<IOccurrence> references();

}
