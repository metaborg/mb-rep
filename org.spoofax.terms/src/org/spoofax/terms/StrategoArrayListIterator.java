package org.spoofax.terms;

import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoArrayList;
import org.spoofax.interpreter.terms.IStrategoTerm;

public final class StrategoArrayListIterator implements Iterator<IStrategoTerm> {
    private final IStrategoArrayList strategoArrayList;
    private int position;

    public StrategoArrayListIterator(IStrategoArrayList strategoArrayList) {
        this.strategoArrayList = strategoArrayList;
        this.position = 0;
    }


    @Override public IStrategoTerm next() {
        IStrategoTerm value = this.strategoArrayList.getSubterm(position);
        position += 1;
        return value;
    }

    @Override public boolean hasNext() {
        return position < this.strategoArrayList.getSubtermCount();
    }
}