package org.spoofax.terms;

import java.io.IOException;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoPlaceholder;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class StrategoPlaceholder extends StrategoAppl implements IStrategoPlaceholder {

    public StrategoPlaceholder(IStrategoConstructor ctor, IStrategoTerm template, IStrategoList annotations, int storageType) {
        super(ctor, new IStrategoTerm[] { template }, annotations, storageType);
    }
    
    public IStrategoTerm getTemplate() {
        return getSubterm(0);
    }
    
    @Override
    public int getTermType() {
        return PLACEHOLDER;
    }
    
    public void writeToString(Appendable output, int maxDepth) throws IOException {
    	output.append('<');
    	getTemplate().writeToString(output, maxDepth - 1);
    	output.append('>');
    	appendAnnotations(output, maxDepth);
    }
    
    @Override
    @Deprecated
	public void prettyPrint(ITermPrinter pp) {
        pp.print("<");
        getTemplate().prettyPrint(pp);
        pp.print(">");
    }
}
