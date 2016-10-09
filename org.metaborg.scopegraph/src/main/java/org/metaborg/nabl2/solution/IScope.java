package org.metaborg.nabl2.solution;

import java.util.Collection;

public interface IScope {

    Collection<IOccurrence> declarations();

    Collection<ILabeledScope> directEdges();

    Collection<IlabeledOccurrence> importEdges();

    Collection<IlabeledOccurrence> exportEdges();

    Collection<IOccurrence> references();

}