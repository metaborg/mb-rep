package org.metaborg.scopegraph.context;

import java.util.Collection;

import javax.annotation.Nullable;

public interface IScopeGraphContext<U extends IScopeGraphUnit> {

    /** Get unit for the given resource */
    @Nullable U unit(String source);

    /** Get all units in this context */
    Collection<U> units();

    /** Remove a unit from this context */
    void removeUnit(String source);

}