package org.spoofax.interpreter.library.index.primitives;

import jakarta.annotation.Nullable;

import org.spoofax.interpreter.library.index.IIndex;

public interface IIndexContext {
    public abstract @Nullable IIndex index();
}
