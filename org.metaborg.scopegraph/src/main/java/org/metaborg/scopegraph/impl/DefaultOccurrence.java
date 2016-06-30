package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.Identifier;
import org.metaborg.scopegraph.Occurrence;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value @AllArgsConstructor
public class DefaultOccurrence implements Occurrence {

    Identifier id;
    Integer index; 
 
    public DefaultOccurrence(String namespace, String name, int index) {
        this(new DefaultIdentifier(namespace, name), index);
    }
    
    @Override
    public String toString() {
        return String.format("%s{%s @%d}", id.getNamespace(), id.getName(), index);
    }
    
}
