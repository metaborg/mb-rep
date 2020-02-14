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

import java.io.IOException;

import static org.spoofax.terms.TermFactory.EMPTY_LIST;

public abstract class StrategoTerm extends AbstractSimpleTerm implements IStrategoTerm, Cloneable {

    private static final long serialVersionUID = -2803845954655431574L;

    private static final int UNKNOWN_HASH = -1;

    private transient int hashCode = UNKNOWN_HASH;

    private IStrategoList annotations;

    protected StrategoTerm(IStrategoList annotations) {
        // FIXME: remove assert (annotations == TermFactory.EMPTY_LIST)
        assert annotations == null || !annotations.isEmpty() || annotations == TermFactory.EMPTY_LIST;
        if(annotations != TermFactory.EMPTY_LIST)
            this.annotations = annotations;
    }

    protected StrategoTerm() {
        this(null);
    }

    /**
     * Equality test.
     */
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
        if(annotations == null || annotations == EMPTY_LIST || annotations.isEmpty()) {
            this.hashCode = hashCode;
        } else {
            this.hashCode = hashCode * 2423 + annotations.hashCode();
        }
    }

    protected abstract int hashFunction();

    @Override
    public String toString() {
        return toString(Integer.MAX_VALUE);
    }

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
        writeAsString(output, Integer.MAX_VALUE);
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

    public final IStrategoList getAnnotations() {
        return annotations == null ? TermFactory.EMPTY_LIST : annotations;
    }

    public final void internalSetAnnotations(IStrategoList annotations) {
        if(annotations == TermFactory.EMPTY_LIST || annotations.isEmpty())
            annotations = null; // essential for hash code calculation

        if(this.annotations != annotations) {
            this.annotations = annotations;
            this.hashCode = UNKNOWN_HASH;
        }
    }

    @Deprecated
    public final boolean isList() {
        return getTermType() == LIST;
    }
}
