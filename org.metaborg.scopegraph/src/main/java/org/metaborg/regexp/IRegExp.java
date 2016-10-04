package org.metaborg.regexp;

import java.io.Serializable;

public interface IRegExp<S> extends Serializable {

    <T> T accept(IRegExpVisitor<S,T> visitor);

}
