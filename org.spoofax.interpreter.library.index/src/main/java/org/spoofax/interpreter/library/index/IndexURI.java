package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.isTermAppl;
import static org.spoofax.interpreter.core.Tools.isTermList;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * The key to used to map {@link IndexEntry}. Consists of a constructor, namespace, path and optional type.
 * 
 * @author Gabriël Konat
 */
public class IndexURI implements Serializable {
    private static final long serialVersionUID = 1619836759792533807L;

    private final IStrategoConstructor constructor;
    private final IStrategoTerm identifier;
    private final IStrategoTerm type;

    private transient IStrategoAppl cachedTerm;

    /**
     * Use {@link IndexEntryFactory#createURI}.
     */
    protected IndexURI(IStrategoConstructor constructor, IStrategoTerm identifier, IStrategoTerm type) {
        this.constructor = constructor;
        this.identifier = identifier;
        this.type = type;

        assert constructor != null && identifier != null;
    }

    public IStrategoConstructor getConstructor() {
        return constructor;
    }

    public IStrategoTerm getIdentifier() {
        return identifier;
    }

    public IStrategoTerm getType() {
        return type;
    }

    /**
     * Returns a parent URI by taking the tail of the path. If the path has no tail, null is returned.
     */
    public IndexURI getParent(ITermFactory factory) {
        // TODO: Maybe this should be performed by a user-defined strategy?
        if(isTermList(identifier)) {
            IStrategoList parentPath = getParentPath((IStrategoList) identifier, factory);
            if(parentPath == null)
                return null;
            return new IndexURI(constructor, parentPath, type);
        } else if(isTermAppl(identifier)) {
            IStrategoAppl appl = (IStrategoAppl) identifier;
            for(int i = 0; i < identifier.getSubtermCount(); ++i) {
                if(isTermList(identifier.getSubterm(i))) {
                    IStrategoList parentPath = getParentPath((IStrategoList) identifier.getSubterm(i), factory);
                    if(parentPath == null)
                        return null;
                    IStrategoTerm[] subterms = identifier.getAllSubterms().clone();
                    subterms[i] = parentPath;
                    return new IndexURI(constructor, factory.makeAppl(appl.getConstructor(), subterms), type);
                }
            }
        }
        return null;
    }
    
    private IStrategoList getParentPath(IStrategoList path, ITermFactory factory) {
        if(path.size() > 1) {
            IStrategoTerm head = path.head();
            if(head.getTermType() == IStrategoTerm.APPL && head.getSubtermCount() == 0)
                // Retain the head of the path if it is a namespace (APPL with 0 subterms).
                return factory.makeListCons(head, path.tail().tail());
            else
                return path.tail();
        }
        return null;
    }

    /**
     * Returns the term representation of this entry.
     */
    public IStrategoAppl toTerm(ITermFactory factory, IStrategoTerm value) {
        if(cachedTerm != null)
            return cachedTerm;

        if(IndexEntryFactory.isDefData(constructor)) {
            cachedTerm = factory.makeAppl(constructor, identifier, type, value);
        } else if(constructor.getArity() == 2) {
            cachedTerm = factory.makeAppl(constructor, identifier, value);
        } else if(constructor.getArity() == 1) {
            cachedTerm = factory.makeAppl(constructor, identifier);
        } else {
            IStrategoTerm[] terms = new IStrategoTerm[constructor.getArity()];
            terms[0] = identifier;
            IStrategoTuple values = (IStrategoTuple) value;
            System.arraycopy(values.getAllSubterms(), 0, terms, 1, values.getSubtermCount());
            cachedTerm = factory.makeAppl(constructor, terms);
        }

        return cachedTerm;
    }

    @Override
    public String toString() {
        String result = constructor.getName() + "(" + identifier + ")";
        if(type != null)
            result += "," + type;
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((constructor == null) ? 0 : constructor.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof IndexURI))
            return false;

        IndexURI other = (IndexURI) obj;

        if(constructor == null) {
            if(other.constructor != null)
                return false;
        } else if(!constructor.equals(other.constructor))
            return false;

        if(identifier == null) {
            if(other.identifier != null)
                return false;
        } else if(!identifier.equals(other.identifier))
            return false;

        if(type == null) {
            if(other.type != null)
                return false;
        } else if(!type.equals(other.type))
            return false;

        return true;
    }
}
