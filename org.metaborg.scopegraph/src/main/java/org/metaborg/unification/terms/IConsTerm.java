package org.metaborg.unification.terms;

import org.metaborg.unification.IListTerm;
import org.metaborg.unification.ITerm;

public interface IConsTerm extends IListTerm {

    ITerm getHead();

    IListTerm getTail();

}