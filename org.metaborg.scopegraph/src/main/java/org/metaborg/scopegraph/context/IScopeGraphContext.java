package org.metaborg.scopegraph.context;

import java.util.Collection;

public interface IScopeGraphContext<U extends IScopeGraphUnit> {

    /** Get unit for the given resource */
    U unit(String resource);

    /** Get all units in this context */
    Collection<U> units();

}