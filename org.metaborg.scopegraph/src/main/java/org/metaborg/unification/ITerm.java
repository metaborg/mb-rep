package org.metaborg.unification;

import java.io.Serializable;

public interface ITerm extends Serializable {

    <T> T apply(ITermFunction<T> function);

    boolean test(ITermPredicate predicate);

    boolean isGround();

}