package org.metaborg.scopegraph.wf;

import org.metaborg.regexp.IRegExpMatcher;
import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.path.IPath;
import org.metaborg.scopegraph.path.PathException;


public interface IWFPath {

    IPath path();

    IRegExpMatcher<ILabel> wf();

    <T> T accept(IWFPathVisitor<T> visitor) throws PathException;

}