package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class SymbolV<S> implements IRegExp<S> {

    public abstract S get();
    
    @Override public <T> T accept(IRegExpVisitor<S,T> visitor) {
        return visitor.symbol(get());
    }
    
    @Override public String toString() {
        return get().toString();
    }

}
