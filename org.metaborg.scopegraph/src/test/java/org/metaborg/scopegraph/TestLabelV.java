package org.metaborg.scopegraph;

import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@SuppressWarnings("serial")
public abstract class TestLabelV implements ILabel {

    public abstract String label();

}