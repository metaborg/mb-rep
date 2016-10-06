package org.metaborg.regexp;

import java.io.Serializable;

public interface IRegExp<S> extends Serializable {

    IRegExpBuilder<S> getBuilder();

    boolean isNullable();

    <T> T accept(IRegExpFunction<S,T> visitor);

}