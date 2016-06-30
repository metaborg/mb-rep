package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.Identifier;

import lombok.Value;

@Value
public class DefaultIdentifier implements Identifier {

    String namespace; 
    String name; 
    
    @Override
    public String toString() {
        return String.format("%s{%s}", namespace, name);
    }
    
}
