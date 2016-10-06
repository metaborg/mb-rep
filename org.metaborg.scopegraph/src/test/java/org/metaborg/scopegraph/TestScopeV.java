package org.metaborg.scopegraph;

import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@SuppressWarnings("serial")
public abstract class TestScopeV implements IScope {

    public abstract int id();

}
