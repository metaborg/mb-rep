package org.metaborg.scopegraph.wf;

import java.io.Serializable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.regexp.IRegExpMatcher;
import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.scopegraph.path.PathException;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class WFFullPathV implements Serializable, WFPath {

    @Override public abstract IFullPath path();

    @Override public abstract IRegExpMatcher<ILabel> wf();

    @Override public <T> T accept(WFPathVisitor<T> visitor) throws PathException {
        return visitor.visit((WFFullPath) this);
    }

}