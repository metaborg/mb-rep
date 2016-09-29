package org.metaborg.unification.terms;

import java.io.Serializable;

public interface ITerm extends Serializable {

    <T> T accept(ITermVisitor<T> visitor);
}