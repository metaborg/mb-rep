package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.Identifier;

public class DefaultIdentifier implements Identifier {

    private final String namespace; 
    private final String name; 
    
    public DefaultIdentifier(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public String name() {
        return name;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
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
        DefaultIdentifier other = (DefaultIdentifier) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s{%s}", namespace, name);
    }
    
}
