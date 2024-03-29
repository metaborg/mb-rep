package org.spoofax.terms.skeleton;


import java.io.IOException;
import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.interpreter.terms.TermType;
import org.spoofax.terms.StrategoTerm;
import org.spoofax.terms.util.ArrayIterator;
import org.spoofax.terms.util.NotImplementedException;
import org.spoofax.terms.util.TermUtils;

import jakarta.annotation.Nullable;

public abstract class SkeletonStrategoAppl extends StrategoTerm implements IStrategoAppl {

	private static final long serialVersionUID = -2522680523775044390L;

	public SkeletonStrategoAppl(@Nullable IStrategoList annotations) {
		super(annotations);
	}

	@Deprecated
	final public IStrategoTerm[] getArguments() {
		throw new NotImplementedException();
	}

	@Override final public String getName() {
		return getConstructor().getName();
	}

	@Override final public int getSubtermCount() {
		return getConstructor().getArity();
	}

	@Deprecated
	@Override final public int getTermType() {
		return getType().getValue();
	}

	@Override final public TermType getType() {
		return TermType.APPL;
	}

	@Override final protected boolean doSlowMatch(IStrategoTerm second) {
		if(!TermUtils.isAppl(second))
			return false;
		final IStrategoAppl o = (IStrategoAppl) second;
		if(getConstructor() != o.getConstructor())
			return false;

		final IStrategoTerm[] kids = getAllSubterms();
		final IStrategoTerm[] secondKids = o.getAllSubterms();
		if(kids != secondKids) {
			for(int i = 0, sz = kids.length; i < sz; i++) {
				final IStrategoTerm kid = kids[i];
				final IStrategoTerm secondKid = secondKids[i];
				if(kid != secondKid && !kid.match(secondKid)) {
					return false;
				}
			}
		}

		final IStrategoList annotations = getAnnotations();
		final IStrategoList secondAnnotations = second.getAnnotations();
		if(annotations == secondAnnotations) {
			return true;
		} else
			return annotations.match(secondAnnotations);
	}

	@Override final public void writeAsString(Appendable output, int maxDepth) throws IOException {
		output.append(getName());
		final IStrategoTerm[] kids = getAllSubterms();
		if(kids.length > 0) {
			output.append('(');
			if(maxDepth == 0) {
				output.append("...");
			} else {
				kids[0].writeAsString(output, maxDepth - 1);
				for(int i = 1; i < kids.length; i++) {
					output.append(',');
					kids[i].writeAsString(output, maxDepth - 1);
				}
			}
			output.append(')');
		}
		appendAnnotations(output, maxDepth);
	}

	@Deprecated
	@Override public final void prettyPrint(ITermPrinter pp) {
		new NotImplementedException();
	}

	@Override final protected int hashFunction() {
		long r = getConstructor().hashCode();
		int accum = 6673;
		final IStrategoTerm[] kids = getAllSubterms();
		for(int i = 0; i < kids.length; i++) {
			r += kids[i].hashCode() * accum;
			accum *= 7703;
		}
		return (int) (r >> 12);
	}

	@Override public Iterator<IStrategoTerm> iterator() {
		return new ArrayIterator<IStrategoTerm>(getAllSubterms());
	}

}
