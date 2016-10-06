package org.metaborg.regexp;

import java.io.Serializable;

public interface IRegExp<S> extends Serializable {

    IAlphabet<S> getAlphabet();

    boolean isNullable();

    <T> T accept(IRegExpFunction<S,T> visitor);

}