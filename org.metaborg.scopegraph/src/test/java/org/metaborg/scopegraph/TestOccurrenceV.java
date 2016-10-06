package org.metaborg.scopegraph;

import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@SuppressWarnings("serial")
public abstract class TestOccurrenceV implements IOccurrence {

    public abstract String name();

    public abstract int index();

    @Override public boolean matches(IOccurrence other) {
        return name().equals(((TestOccurrence) other).name());
    }

}
