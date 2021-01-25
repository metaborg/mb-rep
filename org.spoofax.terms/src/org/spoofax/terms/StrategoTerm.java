/*
 * Created on 2. feb.. 2007
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 *
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.interpreter.terms.TermType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.spoofax.terms.TermFactory.EMPTY_LIST;

public abstract class StrategoTerm extends AbstractSimpleTerm implements IStrategoTerm, Cloneable {

    private static final long serialVersionUID = -2803845954655431574L;

    private static final int UNKNOWN_HASH = -1;

    private transient int hashCode = UNKNOWN_HASH;

    @Nullable private IStrategoList annotations = null;

    protected StrategoTerm(@Nullable IStrategoList annotations) {
        if(annotations != null && !annotations.isEmpty())
            this.annotations = annotations;
    }

    protected StrategoTerm() {
        this(null);
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        // Override this implementation to provide a more efficient one.
        return TermList.ofUnsafe(getAllSubterms());
    }

    /**
     * Equality test.
     */
    @Override
    public final boolean match(IStrategoTerm second) {
        if(this == second)
            return true;
        if(second == null)
            return false;

        return hashCode() == second.hashCode() && doSlowMatch(second);
    }

    protected abstract boolean doSlowMatch(IStrategoTerm second);

    @Override
    public final boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof IStrategoTerm))
            return false;
        return match((IStrategoTerm) obj);
    }

    @Override
    public int hashCode() {
	    if(hashCode == UNKNOWN_HASH) {
            initImmutableHashCode();
        }
        return hashCode;
    }

    protected final void initImmutableHashCode() {
        int hashCode = hashFunction();
        if(annotations == null || annotations.isEmpty()) {
            this.hashCode = hashCode;
        } else {
            this.hashCode = hashCode * 2423 + annotations.hashCode();
        }
    }

    protected abstract int hashFunction();

    @Override
    public String toString() {
        return toString(-1);
    }

    @Override
    public String toString(int maxDepth) {
        StringBuilder result = new StringBuilder();
        try {
            writeAsString(result, maxDepth);
        } catch(IOException e) {
            throw new RuntimeException(e); // shan't happen
        }
        return result.toString();
    }

    public final void writeToString(Appendable output) throws IOException {
        writeAsString(output, -1);
    }

    protected void appendAnnotations(Appendable sb, int maxDepth) throws IOException {
        IStrategoList annos = getAnnotations();
        if(annos.size() == 0)
            return;

        sb.append('{');
        annos.getSubterm(0).writeAsString(sb, maxDepth);
        for(annos = annos.tail(); !annos.isEmpty(); annos = annos.tail()) {
            sb.append(',');
            annos.head().writeAsString(sb, maxDepth);
        }
        sb.append('}');
    }

    @Deprecated
    protected void printAnnotations(ITermPrinter pp) {
        IStrategoList annos = getAnnotations();
        if(annos.size() == 0)
            return;

        pp.print("{");
        annos.head().prettyPrint(pp);
        for(annos = annos.tail(); !annos.isEmpty(); annos = annos.tail()) {
            pp.print(",");
            annos.head().prettyPrint(pp);
        }
        pp.print("}");
    }

    @Override
    protected StrategoTerm clone() {
        try {
            return (StrategoTerm) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e); // silly checked exceptions...
        }
    }

    public StrategoTerm clone(boolean stripAttachments) {
        StrategoTerm result = clone();
        if(stripAttachments)
            result.clearAttachments();
        return result;
    }

    @Override
    public final IStrategoList getAnnotations() {
        return annotations == null ? TermFactory.EMPTY_LIST : annotations;
    }

    public final void internalSetAnnotations(IStrategoList annotations) {
        if(annotations != null && !annotations.isEmpty() && this == EMPTY_LIST) {
            throw new IllegalArgumentException("Attempting to internally mutate the shared EMPTY_LIST");
        }
        if(annotations == TermFactory.EMPTY_LIST || annotations == null || annotations.isEmpty())
            annotations = null; // essential for hash code calculation

        if(this.annotations != annotations) {
            this.annotations = annotations;
            this.hashCode = UNKNOWN_HASH;
        }
    }

    @Override
    @Deprecated
    public int getTermType() {
        return getType().getValue();
    }

    @Override
    public abstract TermType getType();

    @Deprecated
    @Override
    public final boolean isList() {
        return getType() == TermType.LIST;
    }

    private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        // Set hashCode to UNKNOWN_HASH here because the no-arg constructor of this class is not called when an object
        // of this class is deserialized, causing the hashCode to be instantiated with the default value for an int: 0,
        // instead of UNKNOWN_HASH, which then causes all equality checks against this object to fail. The no-arg
        // constructor is not called because according to the JLS on serialization "For serializable objects, the no-arg
        // constructor for the first non-serializable supertype is run.", which in this case is the Object class.
        this.hashCode = UNKNOWN_HASH;
    }
}
