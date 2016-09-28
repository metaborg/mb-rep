package org.metaborg.scopegraph.indices;

import javax.annotation.Nullable;

public interface ITermIndex {

    /**
     * Origin file of the term
     * 
     * @return
     */
    @Nullable String resource();

    /**
     * Node id of the term
     * 
     * @return
     */
    int nodeId();

}
