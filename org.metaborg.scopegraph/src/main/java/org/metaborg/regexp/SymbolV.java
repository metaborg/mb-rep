package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
abstract class SymbolV<S> implements IRegExp<S> {

    public abstract S getSymbol();

    public abstract IRegExpBuilder<S> getBuilder();

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
