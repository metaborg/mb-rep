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

    private static final long serialVersionUID = -1212433450601997725L;

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
    
    @Override
    protected boolean doSlowMatch(IStrategoTerm second, int commonStorageType) {
        if (second.getTermType() != PLACEHOLDER)
            return false;
        
        if (!getTemplate().match(((IStrategoPlaceholder) second).getTemplate()))
        	return false;
        
        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if (annotations == secondAnnotations) {
            return true;
        } else if (annotations.match(secondAnnotations)) {
            if (commonStorageType == SHARABLE) internalSetAnnotations(secondAnnotations);
            return true;
        } else {
            return false;
        }
    }
    
    @Override
	public void writeAsString(Appendable output, int maxDepth) throws IOException {
    	output.append('<');
    	getTemplate().writeAsString(output, maxDepth - 1);
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
