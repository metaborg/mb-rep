package org.metaborg.scopegraph.impl;

import java.io.Serializable;

import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.ScopeGraphException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class Occurrence implements IOccurrence, Serializable {

    private static final long serialVersionUID = -1439304700129430915L;

    private final String namespace;
    private final IStrategoTerm name;
    private final IStrategoTerm index;

    public Occurrence(IStrategoTerm term) throws ScopeGraphException {
        if(!Tools.isTermAppl(term) || !Tools.hasConstructor((IStrategoAppl)term, "Occurrence", 3)) {
            throw new ScopeGraphException("Term is not an Occurrence: "+term);
        }
        this.namespace = namespaceFromTerm(term.getSubterm(0));
        this.name = term.getSubterm(1);
        this.index = term.getSubterm(2);
    }


    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public IStrategoTerm name() {
        return name;
    }

    @Override
    public IStrategoTerm index() {
        return index;
    } 
 
    private String namespaceFromTerm(IStrategoTerm term) {
        if(Tools.isTermAppl(term) && Tools.hasConstructor((IStrategoAppl)term, "Label")) {
            return Tools.asJavaString(term.getSubterm(0));
        }
        return null;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((index == null) ? 0 : index.hashCode());
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
        Occurrence other = (Occurrence) obj;
        if (index == null) {
            if (other.index != null)
                return false;
        } else if (!index.equals(other.index))
            return false;
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
        return (namespace != null ? namespace : "") + "{"+name+" "+index+"}";
    }
    
}
