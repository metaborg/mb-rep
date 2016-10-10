package org.metaborg.unification.terms;

import javax.annotation.Nullable;

import org.metaborg.unification.IAny;

public interface ITermVar extends IAny {

    @Nullable String getResource();

    String getName();

}