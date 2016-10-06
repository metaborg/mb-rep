package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

import com.google.common.base.Preconditions;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
abstract class SymbolV<S> implements IRegExp<S> {

    @Value.Check protected void check() {
        Preconditions.checkState(getAlphabet().contains(getSymbol()));
    }

    public abstract S getSymbol();

    public abstract IAlphabet<S> getAlphabet();

    @Override public boolean isNullable() {
        return false;
    }

    @Override public <T> T accept(IRegExpFunction<S,T> visitor) {
        return visitor.symbol(getSymbol());
    }

    @Override public String toString() {
        return getSymbol().toString();
    }

}
