package org.metaborg.scopegraph.wf;

import org.metaborg.regexp.IRegExpMatcher;
import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.path.IPath;
import org.metaborg.scopegraph.path.PathException;


public interface WFPath {

    IPath path();

    IRegExpMatcher<ILabel> wf();

    <T> T accept(WFPathVisitor<T> visitor) throws PathException;

}