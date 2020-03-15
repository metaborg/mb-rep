package org.spoofax.interpreter.terms;

import java.io.IOException;
import org.spoofax.terms.StrategoArrayList;

/**
 * Internal interface used to share code between the {@link StrategoArrayList} and {@link StrategoArrayListTail}
 */
interface InternalIStrategoArrayList {
    IStrategoTerm[] internalGetBackingArray();

    int internalGetOffset();

    int internalGetEndOffset();

    boolean internalDoSlowMatch(IStrategoTerm second);

    int internalHashFunction();

    void internalAppendAnnotations(Appendable output, int maxDepth) throws IOException;

    void internalPrintAnnotations(ITermPrinter pp);
}
