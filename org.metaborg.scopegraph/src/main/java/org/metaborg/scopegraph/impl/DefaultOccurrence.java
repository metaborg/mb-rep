package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.Identifier;
import org.metaborg.scopegraph.Occurrence;

public class DefaultOccurrence implements Occurrence {

    private final Identifier id;
    private final Integer index; 
 
    public DefaultOccurrence(String namespace, String name, Integer index) {
        this(new DefaultIdentifier(namespace, name), index);
    }

    public DefaultOccurrence(Identifier id, Integer index) {
        this.id = id;
        this.index = index;
    }

    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public Integer index() {
        return index;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((index == null) ? 0 : index.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultOccurrence other = (DefaultOccurrence) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (index == null) {
            if (other.index != null)
                return false;
        } else if (!index.equals(other.index))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s{%s @%d}", id.namespace(), id.name(), index);
    }
    
}
